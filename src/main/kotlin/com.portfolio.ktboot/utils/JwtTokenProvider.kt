package com.portfolio.ktboot.utils


import com.portfolio.ktboot.orm.jpa.MemberRepository
import com.portfolio.ktboot.proto.removeBearerPrefix
import com.portfolio.ktboot.service.TokenValidationService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import java.security.SignatureException
import java.util.*

@Component
class JwtTokenProvider(
    private val userDetailsService: UserDetailsService,
    private val memberRepository: MemberRepository,
    private val tokenValidationService: TokenValidationService
) {

    // 액세스 토큰용 시크릿 키
    private var secretKey = "qwhDhLFtrxWfs951DcODc3PWtwTNxU6vvwAX"

    // 리프레시 토큰용 시크릿 키
    private var refreshSecretKey = "qwdAhLatrxWcs951bcODc35WtwTNxU6vvwdX"

    // 토큰 유효시간 30분
    private val tokenValidTime = 180 * 60 * 1000L

    // 리프레시 토큰 유효시간 60분
    private val refreshTokenValidTime = 240 * 60 * 1000L

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected fun init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.toByteArray())
    }

    // JWT 토큰 생성
    fun createAccessToken(memId: String?): String {
        val claims: Claims = Jwts.claims().setSubject(memId) // JWT payload 에 저장되는 정보단위
        claims["memId"] = memId // 정보는 key / value 쌍으로 저장된다.
        val now = Date()
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setClaims(claims) // 정보 저장
            .setIssuedAt(now) // 토큰 발행 시간 정보
            .setExpiration(Date(now.time + tokenValidTime)) // set Expire Time
            .signWith(SignatureAlgorithm.HS256, secretKey) // 사용할 암호화 알고리즘과
            // signature 에 들어갈 secret값 세팅
            .compact()
    }

    fun createRefreshToken(memId: String): String {
        val claims: Claims = Jwts.claims().setSubject(memId)
        claims["memId"] = memId
        val now = Date()
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + refreshTokenValidTime)) // 리프레시 토큰은 긴 유효기간
            .signWith(SignatureAlgorithm.HS256, refreshSecretKey) // 리프레시 토큰용 별도 시크릿 키
            .compact()
    }

    // JWT 토큰에서 인증 정보 조회
    fun getAuthentication(token: String): UsernamePasswordAuthenticationToken {
        val userDetails = userDetailsService.loadUserByUsername(getAccessUserPk(token))
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    // 액세스 토큰에서 회원 정보 추출
    fun getAccessUserPk(token: String): String {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body.subject
    }

    fun getRefreshUserPk(token: String): String {
        return Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(token).body.subject
    }

    // Request의 Header에서 token 값을 가져옵니다. "X-AUTH-TOKEN" : "TOKEN값'
    fun resolveToken(request: HttpServletRequest): String {
        return request.getHeader("Authorization").removeBearerPrefix() ?: ""
    }

    // 토큰의 유효성 + 만료일자 확인
    fun validateAccessToken(jwtToken: String): Boolean {
        return try {
            val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken)
            claims.body.expiration.after(Date())
        } catch (e: Exception) {
            false
        }
    }


    // 리프레시 토큰 검증
    fun validateRefreshToken(refreshToken: String): Boolean {
        return try {
            val claims = Jwts.parser()
                .setSigningKey(refreshSecretKey)  // 리프레시 토큰용 시크릿 키 사용
                .parseClaimsJws(refreshToken)

            // 만료 시간 검증
            val isNotExpired = !claims.body.expiration.before(Date())

            // 추가적인 검증 로직
            // 예: DB에 저장된 리프레시 토큰과 비교
            val isValidInDb = tokenValidationService.validateRefreshTokenInDb(refreshToken, claims.body.subject)

            isNotExpired && isValidInDb
        } catch (e: Exception) {
            when (e) {
                is ExpiredJwtException -> {
                    // 만료된 토큰 처리
                    handleExpiredRefreshToken(e.claims.subject)
                    false
                }
                is SignatureException -> {
                    // 시그니처 불일치
                    false
                }
                else -> {
                    // 기타 예외 처리
                    false
                }
            }
        }
    }

    // 만료된 리프레시 토큰 처리
    private fun handleExpiredRefreshToken(memId: String) {
        memberRepository.deleteAccessTokenById(memId)
        memberRepository.deleteRefreshTokenById(memId)
    }
}