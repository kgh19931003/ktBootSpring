import org.springframework.http.HttpStatus
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    // 500 Internal Server Error 처리
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleInternalServerError(exception: Exception, model: Model): String {
        model.addAttribute("error", "Internal Server Error occurred: ${exception.message}")
        return "error/500"  // error/500.html 페이지로 리디렉션
    }

    // 404 Not Found 처리
    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(exception: NoHandlerFoundException, model: Model): String {
        model.addAttribute("error", "Page not found: ${exception.message}")
        return "error/404"  // error/404.html 페이지로 리디렉션
    }

    // 예외 메시지와 함께 커스터마이징 가능
    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): String {
        return "Something went wrong! ${exception.message}"
    }
}
