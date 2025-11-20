package com.portfolio.ktboot.controller


import com.portfolio.ktboot.controller.UploadController
import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.model.Response
import com.portfolio.ktboot.orm.jpa.*
import com.portfolio.ktboot.proto.combine
import com.portfolio.ktboot.service.DynamicImageCleanupService
import com.portfolio.ktboot.service.ExcelService
import com.portfolio.ktboot.service.AlloyService
import extarctS3Path
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@RestController
@RequestMapping("/alloy") // API 요청을 위한 기본 경로
class AlloyController (
        private val alloyService: AlloyService,
        private val alloyRepository: AlloyRepository,
        private val alloyFileRepository: AlloyFileRepository,
        private val excelService: ExcelService,
        private val uploadController: UploadController,
        private val dynamicImageCleanupService: DynamicImageCleanupService
){

    @GetMapping("/one/{id}")
    fun alloyOne(@PathVariable id: Int): AlloyList {
        return alloyService.getAlloyOne(id).let{ prdInfo ->
            var file = alloyService.getfileOne(id)
            prdInfo.copy(
                    fileIndex = file.map { it.idx },
                    fileOrder = file.map { it.order },
                    fileImage = file.map { it.src },
                    fileUuid = file.map { it.uuid }
            )
        }
    }

    @GetMapping("/image-one/{prdIdx}")
    fun fileOne(@PathVariable prdIdx: Int): List<AlloyFileEntity> {
        return alloyService.getfileOne(prdIdx)
    }


    @GetMapping("/list")
    fun alloyList(form: AlloySearchForm): ListPagination<AlloyList> {
        return alloyService.getAlloyList(form)
    }

    @PostMapping("/create")
    @Transactional
    fun alloyCreate(
            @RequestPart("form") form: AlloyCreateForm,
            @RequestPart("fileImage", required = false) files: List<MultipartFile>?
    ): Any? {
        println("files = ${files?.map { it.originalFilename }}")
        val alloyEntity = AlloyEntity(
                language = form.language,
                category = "default",
                type = form.type,
                title = form.title,
                subtitle = form.subtitle,
                content = form.content,
                createdAt = LocalDateTime.now()
        )

        //println("AlloyUpdateForm : "+form)

        return try {
            alloyService.save(alloyEntity).let {

                // 신규로 등록되는 실제 파일
                files?.filterNot { it.isEmpty }?.forEachIndexed { index, file ->

                    val uploaded = uploadController.imageUpload(file, arrayOf("uploads", "alloy", "images"))

                    alloyFileRepository.save(
                            AlloyFileEntity(
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
                        tableName = "alloy",
                        idColumn = "idx",
                        contentColumn = "content",
                        rowId = it.idx!!
                )
                */

            }
        }
        catch (e: Exception){
            throw RuntimeException("파일 업로드 중 오류 발생", e)
            //Response.fail("상품 저장실패", e.message)
        }
    }

    @PostMapping("/update/{id}")
    @Transactional
    fun alloyUpdate(
            @PathVariable id: Int,
            @RequestPart("form") form: AlloyUpdateForm,
            @RequestPart("fileImage", required = false) files: List<MultipartFile>?
    ): Any? {
        val alloy = alloyRepository.findByIdx(id).copy(
                idx = id,
                language = form.language,
                category = "default",
                type = form.type,
                title = form.title,
                subtitle = form.subtitle,
                content = form.content,
                updatedAt = LocalDateTime.now()
        )

        // 이미 등록되어있는 파일 삭제
        form.fileDeleteIndex?.forEachIndexed{ index, value ->
            val file = alloyFileRepository.findByIdx(value)

            val dir = file!!.dir
            val name = file.name
            val path = dir?.combine("/" +name)!!

            alloyFileRepository.decrementOrderGreaterThan(id , file.order).let{
                uploadController.deleteFile(path, false).let{
                    alloyFileRepository.deleteByIdx(value)
                }
            }


        }

        alloyService.save(alloy).let{

            println("form.fileMultipartFileOrder : "+form.fileMultipartFileOrder)

            // 신규로 등록되는 실제 파일
            files?.filterNot { it.isEmpty }?.forEachIndexed  { index, file ->

                val uploaded = uploadController.imageUpload(file, arrayOf("uploads", "alloy", "images"))
                val multipartFileOrder = form.fileMultipartFileOrder?.get(index)

                alloyFileRepository.save(
                        AlloyFileEntity(
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

                val alloyInfo = alloyFileRepository.findByIdx(value)
                alloyFileRepository.save(
                        alloyInfo?.copy(
                                order = imageOrder
                        )
                )
            }

        }

        return alloyService.save(alloy)
    }

    @DeleteMapping("/delete/{id}")
    @Transactional
    fun alloyDelete(@PathVariable id: Int): Response<String> {
        return try {
            // 1. DB에서 파일 레코드 조회
            val files = alloyFileRepository.findByParentIdx(id)

            // 2. 파일 삭제
            files.forEach { file ->
                try {
                    val dir = file.dir ?: return@forEach
                    val name = file.name
                    val path = dir.combine("/" +name)!!
                    uploadController.deleteFile(path, false)
                } catch (ex: Exception) {
                    println("파일 삭제 실패: ${ex.message}")
                }
            }

            alloyRepository.deleteById(id)
            alloyFileRepository.deleteByParentIdx(id)
            Response.success("회원 삭제 성공")
        } catch (ex: Exception) {
            Response.fail("회원 삭제 실패: ${ex.message}")
        }
    }


    @PostMapping("/imageUpload/{idx}")
    @Transactional
    fun alloyEditorImageUpload(
            @PathVariable idx: String,
            @RequestPart("file", required = false) file: MultipartFile,
            request: HttpServletRequest
    ): Any {
        return uploadController.editorImageUpload(file, arrayOf("uploads", "editor", "alloy", "images"))
    }

    @PostMapping("/imageDelete")
    @Transactional
    fun alloyEditorImageDelete(
            @RequestBody src: List<String>
    ): Any {
        println("srcsrcsrc : "+extarctS3Path(src[0]))
        return uploadController.deleteFile(extarctS3Path(src[0]), false)
    }

    @GetMapping("/excel")
    fun downloadAlloyListExcel(
            @ModelAttribute form: AlloySearchForm,
            response: HttpServletResponse
    ) {
        excelService.alloyExcelDownload(alloyService.getAlloyList(form), response, "상품목록")
    }
}
