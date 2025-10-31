package com.portfolio.ktboot.controller.admin


import com.portfolio.ktboot.controller.UploadController
import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.model.Response
import com.portfolio.ktboot.orm.jpa.*
import com.portfolio.ktboot.proto.combine
import com.portfolio.ktboot.service.DynamicImageCleanupService
import com.portfolio.ktboot.service.ExcelService
import com.portfolio.ktboot.service.PerformanceService
import extarctS3Path
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@RestController
@RequestMapping("/admin/performance") // API 요청을 위한 기본 경로
class AdminPerformanceController (
        private val performanceService: PerformanceService,
        private val performanceRepository: PerformanceRepository,
        private val performanceFileRepository: PerformanceFileRepository,
        private val excelService: ExcelService,
        private val uploadController: UploadController,
        private val dynamicImageCleanupService: DynamicImageCleanupService
){

    @GetMapping("/one/{id}")
    fun performanceOne(@PathVariable id: Int): PerformanceList {
        return performanceService.getPerformanceOne(id).let{ prdInfo ->
            var file = performanceService.getfileOne(id)
            prdInfo.copy(
                    fileIndex = file.map { it.idx },
                    fileOrder = file.map { it.order },
                    fileImage = file.map { it.src },
                    fileUuid = file.map { it.uuid }
            )
        }
    }

    @GetMapping("/image-one/{prdIdx}")
    fun fileOne(@PathVariable prdIdx: Int): List<PerformanceFileEntity> {
        return performanceService.getfileOne(prdIdx)
    }


    @GetMapping("/list")
    fun performanceList(form: PerformanceSearchForm): ListPagination<PerformanceList> {
        return performanceService.getPerformanceList(form)
    }

    @PostMapping("/create")
    @Transactional
    fun performanceCreate(
            @RequestPart("form") form: PerformanceCreateForm,
            @RequestPart("fileImage", required = false) files: List<MultipartFile>?
    ): Any? {
        println("files = ${files?.map { it.originalFilename }}")
        val performanceEntity = PerformanceEntity(
                language = form.language,
                category = form.category,
                title = form.title,
                subtitle = form.subtitle,
                content = form.content,
                createdAt = LocalDateTime.now()
        )

        //println("PerformanceUpdateForm : "+form)

        return try {
            performanceService.save(performanceEntity).let {

                // 신규로 등록되는 실제 파일
                files?.filterNot { it.isEmpty }?.forEachIndexed { index, file ->

                    val uploaded = uploadController.imageUpload(file, arrayOf("uploads", "performance", "images"))

                    performanceFileRepository.save(
                            PerformanceFileEntity(
                                    language = form.language,
                                    parentIdx = it.idx,
                                    originName = uploaded["originalName"],
                                    name = uploaded["savedName"],
                                    dir = uploaded["relativePath"],
                                    src = uploaded["src"],
                                    size = uploaded["size"]?.toDouble(),
                                    contentType = file.contentType?.substringAfter("/") ?: uploaded["extension"],
                                    createdAt = LocalDateTime.now()
                            )
                    )
                }


                /*
                dynamicImageCleanupService.cleanupUnusedImages(
                        tableName = "performance",
                        idColumn = "idx",
                        contentColumn = "content",
                        rowId = it.idx!!
                )
                */

            }
        }
        catch (e: Exception){
            throw RuntimeException("파일 업로드 중 오류 발생")
            //Response.fail("상품 저장실패", e.message)
        }
    }

    @PostMapping("/update/{id}")
    @Transactional
    fun performanceUpdate(
            @PathVariable id: Int,
            @RequestPart("form") form: PerformanceUpdateForm,
            @RequestPart("fileImage", required = false) files: List<MultipartFile>?
    ): Any? {
        val performance = performanceRepository.findByIdx(id).copy(
                idx = id,
                language = form.language,
                category = form.category,
                title = form.title,
                subtitle = form.subtitle,
                content = form.content,
                updatedAt = LocalDateTime.now()
        )

        // 이미 등록되어있는 파일 삭제
        form.fileDeleteIndex?.forEachIndexed{ index, value ->
            val file = performanceFileRepository.findByIdx(value)

            val dir = file!!.dir
            val name = file.name
            val path = dir?.combine("/" +name)!!

            performanceFileRepository.decrementOrderGreaterThan(id , file.order).let{
                uploadController.deleteImageFile(path).let{
                    performanceFileRepository.deleteByIdx(value)
                }
            }


        }

        performanceService.save(performance).let{

            println("form.fileMultipartFileOrder : "+form.fileMultipartFileOrder)

            // 신규로 등록되는 실제 파일
            files?.filterNot { it.isEmpty }?.forEachIndexed  { index, file ->

                val uploaded = uploadController.imageUpload(file, arrayOf("uploads", "performance", "images"))
                val multipartFileOrder = form.fileMultipartFileOrder?.get(index)

                performanceFileRepository.save(
                        PerformanceFileEntity(
                                language = form.language,
                                parentIdx = id,
                                originName = uploaded["originalName"],
                                name = uploaded["savedName"],
                                dir = uploaded["relativePath"],
                                src = uploaded["src"],
                                size = uploaded["size"]?.toDouble(),
                                order = multipartFileOrder,
                                contentType = file.contentType?.substringAfter("/") ?: uploaded["extension"],
                                createdAt = LocalDateTime.now()
                        )
                )

            }


            // 이미 등록되어있는 파일 정렬
            form.fileIndex?.forEachIndexed{ index, value ->
                val imageIndex = index
                val imageOrder = form.fileOrder?.get(imageIndex)

                println("imageIndex : $imageIndex , imageOrder : $imageOrder")

                val performanceInfo = performanceFileRepository.findByIdx(value)
                performanceFileRepository.save(
                        performanceInfo?.copy(
                                order = imageOrder
                        )
                )
            }

        }

        return performanceService.save(performance)
    }

    @DeleteMapping("/delete/{id}")
    @Transactional
    fun performanceDelete(@PathVariable id: Int): Response<String> {
        return try {
            // 1. DB에서 파일 레코드 조회
            val files = performanceFileRepository.findByParentIdx(id)

            // 2. 파일 삭제
            files.forEach { file ->
                try {
                    val dir = file.dir ?: return@forEach
                    val name = file.name
                    val path = dir.combine("/" +name)!!
                    uploadController.deleteImageFile(path)
                } catch (ex: Exception) {
                    println("파일 삭제 실패: ${ex.message}")
                }
            }

            performanceRepository.deleteById(id)
            performanceFileRepository.deleteByParentIdx(id)
            Response.success("회원 삭제 성공")
        } catch (ex: Exception) {
            Response.fail("회원 삭제 실패: ${ex.message}")
        }
    }


    @PostMapping("/imageUpload/{idx}")
    @Transactional
    fun performanceEditorImageUpload(
            @PathVariable idx: String,
            @RequestPart("file", required = false) file: MultipartFile,
            request: HttpServletRequest
    ): Any {
        return uploadController.editorImageUpload(file, arrayOf("uploads", "editor", "performance", "images"))
    }

    @PostMapping("/imageDelete")
    @Transactional
    fun performanceEditorImageDelete(
            @RequestBody src: List<String>
    ): Any {
        println("srcsrcsrc : "+extarctS3Path(src[0]))
        return uploadController.deleteImageFile(extarctS3Path(src[0]))
    }

    @GetMapping("/excel")
    fun downloadPerformanceListExcel(
            @ModelAttribute form: PerformanceSearchForm,
            response: HttpServletResponse
    ) {
        excelService.performanceExcelDownload(performanceService.getPerformanceList(form), response, "상품목록")
    }
}
