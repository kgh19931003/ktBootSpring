import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.io.IOException
import org.apache.tika.Tika
import org.springframework.http.ResponseEntity

fun deleteImageFile(filePath: String) {
    try {
        val path = Paths.get(filePath)
        if (Files.exists(path)) {
            Files.delete(path)
            println("파일 삭제 완료: $filePath")
        } else {
            println("파일이 존재하지 않음: $filePath")
        }
    } catch (e: IOException) {
        println("파일 삭제 실패: ${e.message}")
    }
}


fun isAllowedExtension(filename: String): Boolean {
    val allowedExtensions = setOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    val ext = filename.substringAfterLast('.', "").lowercase()
    return ext in allowedExtensions
}


fun tikaAllowedImageFile(file: MultipartFile): ResponseEntity<Any> {
    val allowedMimeTypes = setOf("image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp", "image/avif")
    val allowedExtensions = setOf("jpg", "jpeg", "png", "gif", "bmp", "webp", "avif")
    val tika = Tika() // Apache Tika 라이브러리로 파일 내용 분석

    val mimeType = file.contentType ?: throw RuntimeException("파일 MIME 타입이 없습니다: ${file.originalFilename}")
    if (mimeType !in allowedMimeTypes) {
        throw RuntimeException("허용되지 않는 MIME 타입입니다: $mimeType (${file.originalFilename})")
    }

    // 2) 확장자 검사
    val extension = file.originalFilename?.substringAfterLast('.')?.lowercase() ?: ""
    if (extension !in allowedExtensions) {
        throw RuntimeException("허용되지 않는 확장자입니다: $extension (${file.originalFilename})")
    }

    // 3) 실제 파일 내용 검사 (Apache Tika)
    val detectedMimeType = tika.detect(file.inputStream)
    if (detectedMimeType !in allowedMimeTypes) {
        throw RuntimeException("파일 내용이 이미지가 아닙니다: $detectedMimeType (${file.originalFilename})")
    }

    return ResponseEntity.ok("파일 및 데이터 업로드 성공")
}