package com.portfolio.ktboot.controller

import UploadedFileInfo
import com.portfolio.ktboot.model.Response
import com.portfolio.ktboot.proto.combine
import com.portfolio.ktboot.service.S3Service
import io.swagger.v3.oas.annotations.Operation
import jakarta.servlet.http.HttpServletRequest
import net.coobird.thumbnailator.Thumbnails
import nowAsTimestamp
import nowAsYYMMDDFormat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import tikaAllowedImageFile
import toThumbnailPath
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import javax.imageio.ImageIO

@RestController
@RequestMapping("/api/upload")
class UploadController(private val s3Service: S3Service) {

    // 컨트롤러 클래스 안에서
    init {
        ImageIO.scanForPlugins()
    }

    @Autowired
    private lateinit var env: Environment


    @PostMapping("/image", consumes = ["multipart/form-data"])
    fun imageUpload(@RequestParam("file") file: MultipartFile, arrayPath: Array<String>): Map<String, String?> {
        tikaAllowedImageFile(file)

        val size = file.size.toString()
        val extension = file.originalFilename?.substringAfterLast('.', "") ?: "png"
        val savedName = nowAsTimestamp().combine(".$extension").toString()
        val root = System.getProperty("user.dir")
        val uploadDir = Paths.get(root, *arrayPath, nowAsYYMMDDFormat())
        val relativePath = uploadDir.toString().removePrefix(root).replace("\\", "/").removePrefix("/")
        var src = relativePath.combine("/$savedName")
        var thumbnailSrc: String? = null
        val activeProfiles = env.activeProfiles

        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir)
        }

        // 원본 파일 저장
        val targetPath = uploadDir.resolve(savedName)
        file.transferTo(targetPath.toFile())

        if (activeProfiles.contains("aws") || activeProfiles.contains("windows")) {
            // 임시 파일 생성
            val tempFile = targetPath.toFile()

            val key = "${relativePath}/$savedName"
            src = s3Service.uploadFile(tempFile, key).toString()

            // 썸네일 생성
            val thumbnailName = savedName.substringBeforeLast(".") + "_thumbnail." + extension
            val thumbnailTemp = File.createTempFile("thumb_", ".$extension")
            val bufferedImage = ImageIO.read(tempFile)
            if (bufferedImage == null) {
                throw IllegalArgumentException("지원하지 않는 이미지 형식이거나 파일이 손상되었습니다: ${file.originalFilename}")
            }
            Thumbnails.of(bufferedImage)
                    .size(200, 200) // 원하는 썸네일 최대 크기
                    .keepAspectRatio(true)
                    .toFile(thumbnailTemp)

            val thumbnailKey = "${relativePath}/$thumbnailName"
            thumbnailSrc = s3Service.uploadFile(thumbnailTemp, thumbnailKey).toString()

            // 임시 파일 삭제
            thumbnailTemp.delete()
        } else {
            // 로컬 저장 시 썸네일 생성
            val thumbnailName = savedName.substringBeforeLast(".") + "_thumbnail." + extension
            val thumbnailPath = uploadDir.resolve(thumbnailName)
            Thumbnails.of(targetPath.toFile())
                    .size(200, 200)
                    .keepAspectRatio(true)
                    .toFile(thumbnailPath.toFile())
            thumbnailSrc = relativePath.combine("/$thumbnailName")
        }

        return mapOf(
                "size" to size,
                "originalName" to file.originalFilename,
                "savedName" to savedName,
                "relativePath" to relativePath,
                "src" to src,
                "thumbnail" to thumbnailSrc,
                "extension" to extension
        )
    }

    @PostMapping("/editor", consumes = ["multipart/form-data"])
    @Operation(summary = "에디터 파일 업로드", description = "에디터 단일 파일 업로드")
    fun editorImageUpload(@RequestParam("file") file: MultipartFile, arrayPath: Array<String>): Map<String, String> {
        tikaAllowedImageFile(file)

        var imageUrl = "";
        val extension = file.originalFilename?.substringAfterLast('.', "") ?: "png"
        val savedName = nowAsTimestamp().combine(".$extension").toString()
        val root = System.getProperty("user.dir")
        val uploadDir = Paths.get(root, *arrayPath, nowAsYYMMDDFormat())
        val relativePath = uploadDir.toString().removePrefix(root).replace("\\", "/").removePrefix("/")
        val activeProfiles = env.activeProfiles


        if (activeProfiles.contains("aws") || activeProfiles.contains("windows")) {
            // MultipartFile → 임시 File로 변환
            val tempFile = File.createTempFile(relativePath, savedName)
            file.transferTo(tempFile)

            val key = "${relativePath}/${savedName}"
            imageUrl = s3Service.uploadFile(tempFile, key).toString()

            // 임시 파일 삭제
            tempFile.delete()
        } else {
            // 신규 파일 저장
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir)
            }
            val targetPath = uploadDir.resolve(savedName)
            file.transferTo(targetPath.toFile())

            imageUrl = "$relativePath/$savedName"
        }


        return mapOf("url" to imageUrl)
    }

    @DeleteMapping("/file")
    @Operation(summary = "파일 삭제", description = "S3 또는 로컬 파일 삭제")
    fun deleteImageFile(@RequestParam("path") path: String): Response<String> {
        return try {
            val activeProfiles = env.activeProfiles

            if (activeProfiles.contains("aws") || activeProfiles.contains("windows")) {
                // S3에서 삭제
                println("thumbnail_image : "+toThumbnailPath(path))
                s3Service.deleteFiles(mutableListOf(path, toThumbnailPath(path)))
            } else {
                // 로컬 파일 삭제
                val root = System.getProperty("user.dir")
                val filePath = Paths.get(root, path)
                val filePathThumbnail = Paths.get(root, toThumbnailPath(path))
                if (Files.exists(filePath)) {
                    Files.delete(filePath)
                    Files.delete(filePathThumbnail)
                }
            }

            Response.success("파일 삭제 성공")
        } catch (ex: Exception) {
            Response.fail("파일 삭제 실패: ${ex.message}")
        }
    }


}
