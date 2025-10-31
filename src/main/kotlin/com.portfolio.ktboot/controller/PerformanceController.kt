package com.portfolio.ktboot.controller


import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.model.Response
import com.portfolio.ktboot.orm.jpa.*
import com.portfolio.ktboot.proto.combine
import com.portfolio.ktboot.service.ExcelService
import com.portfolio.ktboot.service.PerformanceService
import deleteImageFile
import isAllowedExtension
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import nowAsRegularFormat
import nowAsTimestamp
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import tikaAllowedImageFile
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/performance") // API 요청을 위한 기본 경로
class PerformanceController (
        private val performanceService: PerformanceService,
        private val performanceRepository: PerformanceRepository,
        private val performanceFileRepository: PerformanceFileRepository,
        private val excelService: ExcelService
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
            @RequestPart("form") form: PerformanceUpdateForm,
            @RequestPart("fileImage", required = false) files: List<MultipartFile>?
    ): Any? {
        val performanceEntity = PerformanceEntity(
            language = form.language,
            category = form.category,
            title = form.title,
            subtitle = form.subtitle,
            content = form.content,
        )

        println("PerformanceUpdateForm : "+form)

        return try {
            performanceService.save(performanceEntity).let {

                // 신규로 등록되는 실제 파일
                files?.filterNot { it.isEmpty }?.forEachIndexed { index, file ->
                    tikaAllowedImageFile(files!!.get(0))
                    val originalName = file.originalFilename ?: "unknown.png"
                    val extension = file.originalFilename?.substringAfterLast('.', "") ?: "png"
                    val savedName = nowAsTimestamp().combine(".$extension")
                    val root = System.getProperty("user.dir")
                    val uploadDir = Paths.get(root, "uploads", "performance", "images")
                    val relativePath = uploadDir.toString().removePrefix(root).replace("\\", "/")
                    val src = relativePath.combine("/" + savedName!!)

                    // 신규 파일 저장
                    if (!Files.exists(uploadDir)) {
                        Files.createDirectories(uploadDir)
                    }

                    val targetPath = uploadDir.resolve(savedName)
                    file.transferTo(targetPath.toFile())

                    performanceFileRepository.save(
                            PerformanceFileEntity(
                                    language = form.language,
                                    parentIdx = it.idx,
                                    originName = originalName,
                                    name = savedName,
                                    dir = relativePath,
                                    src = src,
                                    contentType = file.contentType?.substringAfter("/") ?: extension,
                                    createdAt = LocalDateTime.now()
                            )
                    )

                }

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
            val fileInfo = performanceFileRepository.findByIdx(value)

            val root = System.getProperty("user.dir")  // 예: /home/ubuntu/project
            val imagePath = Paths.get(root, "uploads", "performance", "images", fileInfo?.name).toString()

            performanceFileRepository.decrementOrderGreaterThan(id ,fileInfo!!.order).let{
                deleteImageFile(imagePath).let{
                    performanceFileRepository.deleteByIdx(value)
                }
            }
        }

        performanceService.save(performance).let{

            println("form.fileMultipartFileOrder : "+form.fileMultipartFileOrder)

            // 신규로 등록되는 실제 파일
            files?.filterNot { it.isEmpty }?.forEachIndexed  { index, file ->
                tikaAllowedImageFile(file)
                val originalName = file.originalFilename ?: "unknown.png"
                val extension = file.originalFilename?.substringAfterLast('.', "") ?: "png"
                val savedName = nowAsTimestamp().combine(".$extension")
                val root = System.getProperty("user.dir")
                val uploadDir = Paths.get(root, "uploads", "performance", "images")
                val relativePath = uploadDir.toString().removePrefix(root).replace("\\", "/")
                val src = relativePath.combine("/"+savedName!!)
                val multipartFileOrder = form.fileMultipartFileOrder?.get(index)



                    // 신규 파일 저장
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir)
                }

                val targetPath = uploadDir.resolve(savedName)
                file.transferTo(targetPath.toFile())

                performanceFileRepository.save(
                        PerformanceFileEntity(
                                language = form.language,
                                parentIdx = id,
                                originName = originalName,
                                name = savedName,
                                dir = relativePath,
                                src = src,
                                order = multipartFileOrder,
                                contentType = file.contentType?.substringAfter("/") ?: extension,
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
            performanceRepository.deleteById(id)
            Response.success("회원 삭제 성공")
        } catch (ex: Exception) {
            Response.fail("회원 삭제 실패: ${ex.message}")
        }
    }



    @GetMapping("/excel")
    fun downloadPerformanceListExcel(
            @ModelAttribute form: PerformanceSearchForm,
            response: HttpServletResponse
    ) {
        excelService.performanceExcelDownload(performanceService.getPerformanceList(form), response, "상품목록")
    }
}
