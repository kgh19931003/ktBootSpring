import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime?.formatToString(pattern: String = "yyyy-MM-dd HH:mm:ss"): String? {
    return this?.format(DateTimeFormatter.ofPattern(pattern))
}