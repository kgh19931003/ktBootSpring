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
                // 금속 비디오
                metalFileImage = metalVideos.map { it.src!! },
                metalFileIndex = metalVideos.map { it.idx!! },
                metalFileOrder = metalVideos.map { it.order!! },
                // 플라스틱 이미지
                plasticImage = plasticImages.map { it.src!! },
                plasticImageIndex = plasticImages.map { it.idx!! },
                plasticImageOrder = plasticImages.map { it.order!! },
                // 플라스틱 비디오
                plasticFileImage = plasticVideos.map { it.src!! },
                plasticFileIndex = plasticVideos.map { it.idx!! },
                plasticFileOrder = plasticVideos.map { it.order!! },
                // 보수 이미지
                repairImage = repairImages.map { it.src!! },
                repairImageIndex = repairImages.map { it.idx!! },
                repairImageOrder = repairImages.map { it.order!! },
                // 보수 비디오
                repairFileImage = repairVideos.map { it.src!! },
                repairFileIndex = repairVideos.map { it.idx!! },
                repairFileOrder = repairVideos.map { it.order!! }
        )
    }

    @GetMapping("/list")
    fun fileUploadList(form: FileUploadSearchForm): ListPagination<FileUploadList> {
        return fileUploadService.getFileUploadList(form)
    }


}

