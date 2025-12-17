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
@RequestMapping("/admin/file-upload")
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
        val fileImages = fileUploadService.getFilesByCategory("image", "file")
        val fileVideos = fileUploadService.getFilesByCategory("video", "file")

        return FileUploadDetailResponse(
                idx = 0,
                language = "ko",
                //  이미지
                fileImage = fileImages.map { it.src!! },
                fileImageIndex = fileImages.map { it.idx!! },
                fileImageOrder = fileImages.map { it.order!! },
                fileImageOriginalName = fileImages.map { it.originName!! },
                //  비디오
                fileVideo = fileVideos.map { it.src!! },
                fileIndex = fileVideos.map { it.idx!! },
                fileOrder = fileVideos.map { it.order!! },
                fileOriginalName = fileVideos.map { it.originName!! },
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
        @RequestPart("fileFileImage", required = false) fileImages: List<MultipartFile>?,
        @RequestPart("fileFileVideo", required = false) fileVideos: List<MultipartFile>?
    ): Response<String> {
        return try {

            println("fileVideos : ${fileVideos}")

            if (!fileImages.isNullOrEmpty()) {
                // ✅  이미지 업로드
                processFileUploads(
                        fileImages,
                        form.fileImageMultipartFileOrder,
                        form.language ?: "ko",   // 혹시 몰라서 기본값
                        "file",
                        "image"
                )
            } else {
                // ✅  이미지 수정 처리
                updateFiles(
                        fileImages,
                        form.fileImageDeleteIndex,
                        form.fileImageOrder,
                        form.fileImageIndex,
                        form.fileImageMultipartFileOrder,
                        form.language ?: "ko",
                        "file",
                        "image"
                )
            }


            if (!fileVideos.isNullOrEmpty()) {
                //  비디오 업로드
                processFileUploads(
                        fileVideos,
                        form.fileVideoMultipartFileOrder,
                        form.language ?: "ko",   // 혹시 몰라서 기본값
                        "file",
                        "video"
                )
            }
            else{
                //  비디오 처리
                updateFiles(fileVideos, form.fileVideoDeleteIndex,
                        form.fileVideoOrder, form.fileVideoIndex, form.fileVideoMultipartFileOrder,
                        form.language ?: "ko", "file", "video")
            }


            Response.success("등록 성공")
        } catch (e: Exception) {
            Response.fail("등록 실패: ${e.message}")
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

            fileUploadFileRepository.incrementOrderGreaterThan(index, order, type)

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

                fileUploadFileRepository.decrementOrderGreaterThan(idx, file.order, type).let{
                    uploadController.deleteFile(path, false).let{
                        fileUploadFileRepository.deleteByIdx(idx)
                    }
                }

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

