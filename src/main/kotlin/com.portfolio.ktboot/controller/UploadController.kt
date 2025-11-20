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

        if (activeProfiles.contains("aws")) {
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
                    .size(200, 200)
                    .keepAspectRatio(true)
                    .toFile(thumbnailTemp)

            val thumbnailKey = "${relativePath}/$thumbnailName"
            thumbnailSrc = s3Service.uploadFile(thumbnailTemp, thumbnailKey).toString()

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

    @PostMapping("/video", consumes = ["multipart/form-data"])
    fun videoUpload(@RequestParam("file") file: MultipartFile, arrayPath: Array<String>): Map<String, String?> {
        // 비디오 파일 확장자 검증
        val allowedVideoExtensions = listOf("mp4", "avi", "mov", "wmv", "flv", "mkv", "webm", "m4v", "mpeg", "mpg")
        val originalFilename = file.originalFilename ?: throw IllegalArgumentException("파일명이 없습니다.")
        val extension = originalFilename.substringAfterLast('.', "").lowercase()

        if (!allowedVideoExtensions.contains(extension)) {
            throw IllegalArgumentException("허용되지 않은 비디오 형식입니다. (허용: ${allowedVideoExtensions.joinToString(", ")})")
        }

        // 파일 크기 체크 (500MB 제한)
        val maxSize = 500 * 1024 * 1024L
        if (file.size > maxSize) {
            throw IllegalArgumentException("비디오 파일 크기는 500MB를 초과할 수 없습니다.")
        }

        val size = file.size.toString()
        val savedName = nowAsTimestamp().combine(".$extension").toString()
        val root = System.getProperty("user.dir")
        val uploadDir = Paths.get(root, *arrayPath, nowAsYYMMDDFormat())
        val relativePath = uploadDir.toString().removePrefix(root).replace("\\", "/").removePrefix("/")
        var src = relativePath.combine("/$savedName")
        val activeProfiles = env.activeProfiles

        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir)
        }

        // 원본 파일 저장
        val targetPath = uploadDir.resolve(savedName)
        file.transferTo(targetPath.toFile())

        if (activeProfiles.contains("aws")) {
            val tempFile = targetPath.toFile()
            val key = "${relativePath}/$savedName"
            src = s3Service.uploadFile(tempFile, key).toString()

            // AWS 업로드 후 로컬 임시 파일 삭제
            tempFile.delete()
        }

        return mapOf(
                "size" to size,
                "originalName" to originalFilename,
                "savedName" to savedName,
                "relativePath" to relativePath,
                "src" to src,
                "extension" to extension
        )
    }

    @PostMapping("/editor", consumes = ["multipart/form-data"])
    @Operation(summary = "에디터 파일 업로드", description = "에디터 단일 파일 업로드")
    fun editorImageUpload(@RequestParam("file") file: MultipartFile, arrayPath: Array<String>): Map<String, String> {
        tikaAllowedImageFile(file)

        var imageUrl = ""
        val extension = file.originalFilename?.substringAfterLast('.', "") ?: "png"
        val savedName = nowAsTimestamp().combine(".$extension").toString()
        val root = System.getProperty("user.dir")
        val uploadDir = Paths.get(root, *arrayPath, nowAsYYMMDDFormat())
        val relativePath = uploadDir.toString().removePrefix(root).replace("\\", "/").removePrefix("/")
        val activeProfiles = env.activeProfiles

        if (activeProfiles.contains("aws")) {
            val tempFile = File.createTempFile(relativePath, savedName)
            file.transferTo(tempFile)

            val key = "${relativePath}/${savedName}"
            imageUrl = s3Service.uploadFile(tempFile, key).toString()

            tempFile.delete()
        } else {
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
    fun deleteFile(@RequestParam("path") path: String, @RequestParam("isVideo", defaultValue = "false") isVideo: Boolean): Response<String> {
        return try {
            val activeProfiles = env.activeProfiles

            if (activeProfiles.contains("aws")) {
                // S3에서 삭제
                if (isVideo) {
                    // 비디오는 썸네일이 없으므로 원본만 삭제
                    s3Service.deleteFiles(mutableListOf(path))
                } else {
                    // 이미지는 원본 + 썸네일 삭제
                    println("thumbnail_image : "+toThumbnailPath(path))
                    s3Service.deleteFiles(mutableListOf(path, toThumbnailPath(path)))
                }
            } else {
                // 로컬 파일 삭제
                val root = System.getProperty("user.dir")
                val filePath = Paths.get(root, path)

                println("filePath : "+filePath)

                if (Files.exists(filePath)) {
                    Files.delete(filePath)

                    // 이미지인 경우 썸네일도 삭제
                    if (!isVideo) {
                        val filePathThumbnail = Paths.get(root, toThumbnailPath(path))
                        if (Files.exists(filePathThumbnail)) {
                            Files.delete(filePathThumbnail)
                        }
                    }
                }
            }

            Response.success("파일 삭제 성공")
        } catch (ex: Exception) {
            Response.fail("파일 삭제 실패: ${ex.message}")
        }
    }
}