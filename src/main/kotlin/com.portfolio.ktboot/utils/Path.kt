import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.io.IOException
import org.apache.tika.Tika
import org.springframework.http.ResponseEntity

fun toThumbnailPath(path: String): String {
    val index = path.lastIndexOf(".")
    return if (index != -1) {
        path.substring(0, index) + "_thumbnail" + path.substring(index)
    } else {
        path + "_thumbnail"  // 확장자가 없는 경우
    }
}


fun extarctS3Path(path: String): String{
    return path.replace(Regex("^https?://[^/]+"), "").trimStart('/')
}