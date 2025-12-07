package com.portfolio.ktboot.controller.admin

import com.portfolio.ktboot.form.FileUploadCreateForm
import com.portfolio.ktboot.form.FileUploadList
import com.portfolio.ktboot.form.FileUploadSearchForm
import com.portfolio.ktboot.controller.UploadController
import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.model.Response
import com.portfolio.ktboot.orm.jpa.entity.FileUploadEntity
import com.portfolio.ktboot.orm.jpa.repository.FileUploadRepository
import com.portfolio.ktboot.proto.combine
import com.portfolio.ktboot.service.ExcelService
import com.portfolio.ktboot.service.FileUploadService
import jakarta.transaction.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@RestController
@RequestMapping("/admin/am-case")
class AdminFileUploadController<FileUploadDetailResponse>(
    private val fileUploadService: FileUploadService,
    private val fileUploadRepository: FileUploadRepository,
    private val fileUploadFileRepository: FileUploadRepository,
    private val excelService: ExcelService,
    private val uploadController: UploadController
) {

    @GetMapping("/one")
    fun fileUploadOne(
            @RequestParam(required = false) locale: String?,
    ): com.portfolio.ktboot.form.FileUploadDetailResponse {

        // 각 카테고리별 파일 조회
        val metalImages = fileUploadService.getFilesByCategory("image", "metal", )
        val metalVideos = fileUploadService.getFilesByCategory("video", "metal")
        val plasticImages = fileUploadService.getFilesByCategory( "image", "plastic")
        val plasticVideos = fileUploadService.getFilesByCategory("video", "plastic")
        val repairImages = fileUploadService.getFilesByCategory("image", "repair")
        val repairVideos = fileUploadService.getFilesByCategory("video", "repair")

        return FileUploadDetailResponse(
                idx = 0,
                language = "ko",
                // 금속 이미지
                metalImage = metalImages.map { it.src!! },
                metalImageIndex = metalImages.map { it.idx!! },
                metalImageOrder = metalImages.map { it.order!! },
                metalImageOriginalName = metalImages.map { it.originName!! },
                // 금속 비디오
                metalFileImage = metalVideos.map { it.src!! },
                metalFileIndex = metalVideos.map { it.idx!! },
                metalFileOrder = metalVideos.map { it.order!! },
                metalFileOriginalName = metalVideos.map { it.originName!! },
                // 플라스틱 이미지
                plasticImage = plasticImages.map { it.src!! },
                plasticImageIndex = plasticImages.map { it.idx!! },
                plasticImageOrder = plasticImages.map { it.order!! },
                plasticImageOriginalName = plasticImages.map { it.originName!! },
                // 플라스틱 비디오
                plasticFileImage = plasticVideos.map { it.src!! },
                plasticFileIndex = plasticVideos.map { it.idx!! },
                plasticFileOrder = plasticVideos.map { it.order!! },
                plasticFileOriginalName = plasticVideos.map { it.originName!! },
                // 보수 이미지
                repairImage = repairImages.map { it.src!! },
                repairImageIndex = repairImages.map { it.idx!! },
                repairImageOrder = repairImages.map { it.order!! },
                repairImageOriginalName = repairImages.map { it.originName!! },
                // 보수 비디오
                repairFileImage = repairVideos.map { it.src!! },
                repairFileIndex = repairVideos.map { it.idx!! },
                repairFileOrder = repairVideos.map { it.order!! },
                repairFileOriginalName = repairVideos.map { it.originName!! }
        )
    }

    @GetMapping("/list")
    fun fileUploadList(form: FileUploadSearchForm): ListPagination<FileUploadList> {
        return fileUploadService.getFileUploadList(form)
    }

    @PostMapping("/update")
    @Transactional
    fun fileUploadUpdate(
        @RequestPart("form") form: FileUploadCreateForm,
        @RequestPart("metalFileImage", required = false) metalImages: List<MultipartFile>?,
        @RequestPart("metalFileVideo", required = false) metalVideos: List<MultipartFile>?,
        @RequestPart("plasticFileImage", required = false) plasticImages: List<MultipartFile>?,
        @RequestPart("plasticFileVideo", required = false) plasticVideos: List<MultipartFile>?,
        @RequestPart("repairFileImage", required = false) repairImages: List<MultipartFile>?,
        @RequestPart("repairFileVideo", required = false) repairVideos: List<MultipartFile>?
    ): Response<String> {
        return try {

            println("metalVideos : ${metalVideos}")

            if (!metalImages.isNullOrEmpty()) {
                // ✅ 금속 이미지 업로드
                processFileUploads(
                        metalImages,
                        form.metalImageMultipartFileOrder,
                        form.language ?: "ko",   // 혹시 몰라서 기본값
                        "metal",
                        "image"
                )
            } else {
                // ✅ 금속 이미지 수정 처리
                updateFiles(
                        metalImages,
                        form.metalImageDeleteIndex,
                        form.metalImageOrder,
                        form.metalImageIndex,
                        form.metalImageMultipartFileOrder,
                        form.language ?: "ko",
                        "metal",
                        "image"
                )
            }


            if (!metalVideos.isNullOrEmpty()) {
                // 금속 비디오 업로드
                processFileUploads(
                        metalVideos,
                        form.metalVideoMultipartFileOrder,
                        form.language ?: "ko",   // 혹시 몰라서 기본값
                        "metal",
                        "video"
                )
            }
            else{
                // 금속 비디오 처리
                updateFiles(metalVideos, form.metalVideoDeleteIndex,
                        form.metalVideoOrder, form.metalVideoIndex, form.metalVideoMultipartFileOrder,
                        form.language ?: "ko", "metal", "video")
            }


            if (!plasticImages.isNullOrEmpty()) {
                // 플라스틱 이미지 업로드
                processFileUploads(
                        plasticImages,
                        form.plasticImageMultipartFileOrder,
                        form.language ?: "ko",
                        "plastic",
                        "image"
                )
            }
            else{
                // 플라스틱 이미지 처리
                updateFiles(plasticImages, form.plasticImageDeleteIndex,
                        form.plasticImageOrder, form.plasticImageIndex, form.plasticImageMultipartFileOrder,
                        form.language ?: "ko", "plastic", "image")
            }


            if (!plasticVideos.isNullOrEmpty()) {
                // 플라스틱 비디오 업로드
                processFileUploads(
                        plasticVideos,
                        form.plasticVideoMultipartFileOrder,
                        form.language ?: "ko",
                        "plastic",
                        "video"
                )
            }
            else{
                // 플라스틱 비디오 처리
                updateFiles(plasticVideos, form.plasticVideoDeleteIndex,
                        form.plasticVideoOrder, form.plasticVideoIndex, form.plasticVideoMultipartFileOrder,
                        form.language ?: "ko", "plastic", "video")
            }


            if (!repairImages.isNullOrEmpty()) {
                // 보수 이미지 업로드
                processFileUploads(
                        repairImages,
                        form.repairImageMultipartFileOrder,
                        form.language ?: "ko",
                        "repair",
                        "image"
                )
            }
            else{
                // 보수 이미지 처리
                updateFiles(repairImages, form.repairImageDeleteIndex,
                        form.repairImageOrder, form.repairImageIndex, form.repairImageMultipartFileOrder,
                        form.language ?: "ko", "repair", "image")
            }


            if (!repairVideos.isNullOrEmpty()) {
                // 보수 비디오 업로드
                processFileUploads(
                        repairVideos,
                        form.repairVideoMultipartFileOrder,
                        form.language ?: "ko",
                        "repair",
                        "video"
                )
            }
            else{
                // 보수 비디오 처리
                updateFiles(repairVideos, form.repairVideoDeleteIndex,
                        form.repairVideoOrder, form.repairVideoIndex, form.repairVideoMultipartFileOrder,
                        form.language ?: "ko", "repair", "video")
            }

            Response.success("수행사례 등록 성공")
        } catch (e: Exception) {
            Response.fail("수행사례 등록 실패: ${e.message}")
        }
    }



    // 헬퍼 함수: 파일 업로드 처리
    private fun processFileUploads(
            files: List<MultipartFile>?,
            orderList: List<Int>?,
            language: String,
            category: String,
            type: String
    ) {
        files?.filterNot { it.isEmpty }?.forEachIndexed { index, file ->

            var uploaded = if (type == "image") {
                uploadController.imageUpload(
                        file,
                        arrayOf("uploads", "fileUpload", category, type)
                )
            } else {
                uploadController.videoUpload(
                        file,
                        arrayOf("uploads", "fileUpload", category, type)
                )
            } as MutableMap<String, String?>


            val order = orderList?.getOrNull(index) ?: index

            fileUploadFileRepository.save(
                    FileUploadEntity(
                            parentIdx = 0,
                            language = language,
                            category = category,
                            type = type,
                            originName = uploaded["originalName"],
                            name = uploaded["savedName"],
                            dir = uploaded["relativePath"],
                            src = uploaded["src"],
                            size = uploaded["size"]?.toDouble(),
                            contentType = file.contentType?.substringAfter("/") ?: uploaded["extension"],
                            order = order,
                            createdAt = LocalDateTime.now()
                    )
            )
        }
    }

    // 헬퍼 함수: 파일 업데이트 처리
    private fun updateFiles(
            files: List<MultipartFile>?,
            deleteIndexes: List<Int>?,
            fileOrders: List<Int>?,
            fileIndexes: List<Int>?,
            multipartFileOrders: List<Int>?,
            language: String,
            category: String,
            type: String
    ) {
        // 1. 삭제할 파일 처리 (DeleteIndex 활용)
        deleteIndexes?.forEach { idx ->
            try {
                val file = fileUploadFileRepository.findByIdx(idx)

                // 2. 파일 삭제
                val dir = file?.dir ?: return@forEach
                val name = file.name
                val path = dir.combine("/" +name)!!
                uploadController.deleteFile(path, false)
                fileUploadFileRepository.deleteByIdx(idx)

                println("파일 삭제 완료 - idx: $idx")
            } catch (ex: Exception) {
                println("파일 삭제 실패 (idx: $idx): ${ex.message}")
                ex.printStackTrace()
            }
        }

        // 2. 신규 파일 업로드 (multipartFileOrders에 따라 순서 지정)
        files?.filterNot { it.isEmpty }?.forEachIndexed { index, file ->
            try {
                val uploaded = uploadController.imageUpload(
                        file,
                        arrayOf("uploads", "fileUpload", category, type)
                )
                val order = multipartFileOrders?.getOrNull(index) ?: index

                println("신규 파일 업로드 - name: ${file.originalFilename}, order: $order")

                fileUploadFileRepository.save(
                        FileUploadEntity(
                                language = language,
                                category = category,
                                type = type,
                                originName = uploaded["originalName"],
                                name = uploaded["savedName"],
                                dir = uploaded["relativePath"],
                                src = uploaded["src"],
                                size = uploaded["size"]?.toDouble(),
                                contentType = file.contentType?.substringAfter("/") ?: uploaded["extension"],
                                order = order,
                                createdAt = LocalDateTime.now()
                        )
                )
            } catch (ex: Exception) {
                println("파일 업로드 실패: ${ex.message}")
                ex.printStackTrace()
            }
        }

        // 3. 기존 파일 순서 업데이트 (fileIndexes와 fileOrders 활용)
        fileIndexes?.forEachIndexed { index, fileIdx ->
            try {
                val newOrder = fileOrders?.getOrNull(index) ?: index
                val fileEntity = fileUploadFileRepository.findByIdx(fileIdx)

                if (fileEntity != null && fileEntity.order != newOrder) {
                    println("파일 순서 변경 - idx: $fileIdx, 기존 order: ${fileEntity.order}, 새 order: $newOrder")
                    fileUploadFileRepository.save(fileEntity.copy(order = newOrder))
                }
            } catch (ex: Exception) {
                println("파일 순서 업데이트 실패 (idx: $fileIdx): ${ex.message}")
            }
        }
    }
}

