package com.portfolio.ktboot.interceptor

import com.portfolio.ktboot.utils.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SkipAuth

@Component
class JwtInterceptor(private val jwtTokenProvider: JwtTokenProvider) : HandlerInterceptor {
    override fun preHandle(
            request: HttpServletRequest,
            response: HttpServletResponse,
            handler: Any
    ): Boolean {

        // 로그 찍기
        println("===== JwtInterceptor start =====")
        println("Request URI: ${request.requestURI}")
        println("X-Skip-Auth Header: ${request.getHeader("X-Skip-Auth")}")
        println("skipAuth Query Param: ${request.getParameter("skipAuth")}")

        val method = (handler as? HandlerMethod)?.method
        if (method?.isAnnotationPresent(SkipAuth::class.java) == true) return true

        val skipHeader = request.getHeader("X-Skip-Auth")?.toBoolean() ?: false
        val skipParam = request.getParameter("skipAuth")?.toBoolean() ?: false
        if (skipHeader || skipParam) return true

        // Authorization 헤더가 없으면 그냥 통과
        val authHeader = request.getHeader("Authorization")
        val token = authHeader?.takeIf { it.startsWith("Bearer ") }?.removePrefix("Bearer ")?.trim()

        if (token.isNullOrEmpty()) {
            // 토큰이 없으면 그냥 통과
            return true
        }

        // 토큰이 있으면 검증
        if (!jwtTokenProvider.validateAccessToken(token)) {
            throw RuntimeException("Invalid Token")
        }

        return true
    }
}
