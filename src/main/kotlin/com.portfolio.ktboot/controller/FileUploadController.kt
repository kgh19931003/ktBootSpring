package com.portfolio.ktboot.controller

import com.portfolio.ktboot.form.FileUploadList
import com.portfolio.ktboot.orm.jpa.repository.FileUploadRepository
import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.service.FileUploadService
import com.portfolio.ktboot.form.ListPagination
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/file-upload")
class FileUploadController(
    private val fileUploadService: FileUploadService,
    private val fileUploadRepository: FileUploadRepository,
    private val uploadController: UploadController
) {

    @GetMapping("/one")
    fun fileUploadOne(
            @RequestParam(required = false) locale: String?,
    ): FileUploadDetailResponse {

        // 각 카테고리별 파일 조회
        val fileImages = fileUploadService.getFilesByCategory("image", "file")
        val fileVideos = fileUploadService.getFilesByCategory("video", "file")

        return FileUploadDetailResponse(
                idx = 0,
                language = "ko",
                // 금속 이미지
                fileImage = fileImages.map { it.src!! },
                fileImageIndex = fileImages.map { it.idx!! },
                fileImageOrder = fileImages.map { it.order!! },
                // 금속 비디오
                fileVideo = fileVideos.map { it.src!! },
                fileIndex = fileVideos.map { it.idx!! },
                fileOrder = fileVideos.map { it.order!! },
        )
    }

    @GetMapping("/list")
    fun fileUploadList(form: FileUploadSearchForm): ListPagination<FileUploadList> {
        return fileUploadService.getFileUploadList(form)
    }


}

