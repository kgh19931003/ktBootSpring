package com.portfolio.ktboot.filter


import com.nimbusds.oauth2.sdk.http.HTTPResponse
import com.portfolio.ktboot.proto.isNotNull
import com.portfolio.ktboot.service.MemberService
import com.portfolio.ktboot.utils.JwtTokenProvider
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
//@Component
class JwtTokenFilter (
        private val memberService: MemberService,
        private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {


    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val path = request.requestURI
        println("path : "+path)
        filterChain.doFilter(request, response)
        return

        // 🔑 로그인 관련 엔드포인트는 JWT 검사 건너뜀
        if (path.startsWith("/auth/") || path.startsWith("/login/")) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val token = jwtTokenProvider.resolveToken(request)
            // ✅ 추가: 토큰 형식 검증
            if(token.isNotBlank() && token.count { it == '.' } == 2) {
                jwtVerify(request, response, filterChain)
            }
        } catch (e: ExpiredJwtException) {
            logger.error("ExpiredJwtException: ${e.message}")
        } catch (e: Exception) {
            logger.error("token filter error: ${e.message}")
        }
        filterChain.doFilter(request, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        // 모든 경로를 허용하려면 이 메서드를 false로 반환하거나
        // 아예 제거해도 됩니다 (기본값은 false)
        return true
    }


    private fun jwtVerify(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = jwtTokenProvider.resolveToken(request)
            val isAccessTokenValid = jwtTokenProvider.validateAccessToken(token)
            var isRefreshTokenValid = false

            if(!isAccessTokenValid) isRefreshTokenValid = jwtTokenProvider.validateRefreshToken(token)

            when {
                isAccessTokenValid -> {
                    // 액세스 토큰이 유효한 경우 일반적인 인증 진행
                    val loginId = verifyJwtMemId(request, response)
                    setAuthenticationContext(loginId!!)
                }
                isRefreshTokenValid -> {
                    // 액세스 토큰은 만료되었지만 리프레시 토큰이 유효한 경우
                    val loginId = jwtTokenProvider.getRefreshUserPk(token)

                    // DB에 저장된 리프레시 토큰과 비교 검증
                    val storedRefreshToken = memberService.getStoredRefreshToken(loginId)
                    if (token == storedRefreshToken) {

                        // 새로운 액세스 및 리프레시 토큰 생성
                        val newAccessToken = jwtTokenProvider.createAccessToken(loginId)
                        val newRefreshToken = jwtTokenProvider.createRefreshToken(loginId)

                        // 토큰 저장
                        memberService.save(memberService.findByMemId(loginId).copy(accessToken = newAccessToken, refreshToken = newRefreshToken))

                        // 응답 헤더에 새로운 액세스 토큰 추가
                        response.addHeader("acessToken", newAccessToken)
                        response.addHeader("refreshToken", newRefreshToken)

                        // 인증 컨텍스트 설정
                        setAuthenticationContext(loginId)
                    } else {
                        throw ExpiredJwtException(null, null, "유효하지 않은 리프레시 토큰입니다")
                    }
                }
                else -> {
                    memberService.deleteAccessToken(jwtTokenProvider.getAccessUserPk(token))
                    memberService.deleteRefreshToken(jwtTokenProvider.getRefreshUserPk(token))
                    throw ExpiredJwtException(null, null, "모든 토큰이 만료되었습니다")
                }
            }

        }catch (e: ExpiredJwtException) {
            throw e
        }
    }


    private fun verifyJwtMemId(request: HttpServletRequest, response: HttpServletResponse): String? {
        val token = jwtTokenProvider.resolveToken(request)
        return if (token.isBlank()) null else jwtTokenProvider.getAccessUserPk(token)
    }


    // 인증 컨텍스트 설정을 위한 헬퍼 함수
    private fun setAuthenticationContext(loginId: String) {
        MDC.put("id", loginId)
        val member = memberService.loadUserByid(loginId)
        val authentication = UsernamePasswordAuthenticationToken(member, member.password, member.authorities)
        SecurityContextHolder.getContext().authentication = authentication
    }


}