package com.portfolio.ktboot.controller


import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.orm.jpa.repository.MemberRepository
import com.portfolio.ktboot.service.MemberService
import com.portfolio.ktboot.utils.JwtTokenProvider
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/login") // API 요청을 위한 기본 경로
class LoginController (
    private val memberService: MemberService,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
){

    @PostMapping("/cert")
    fun loginCert(@RequestBody request: LoginRequest): ResponseEntity<Any> {
        val member = memberRepository.findById(request.id)
                ?: return ResponseEntity.status(401).body(mapOf("error" to "아이디가 존재하지 않습니다."))

        if (!passwordEncoder.matches(request.pass, member.password)) {
            return ResponseEntity.status(401).body(mapOf("error" to "비밀번호가 일치하지 않습니다."))
        }

        val token = jwtTokenProvider.createAccessToken(request.id)

        // 토큰 DB에 저장 (선택적)
        memberRepository.save(member.copy(id = member.id, accessToken = token))

        val userInfo = MemberList(
                idx = member.idx,
                id = member.id,
                name = member.name,
                gender = member.gender,
                token = token,
                createdAt = member.createdAt
        )


        //val headers = org.springframework.http.HttpHeaders()
        //headers["Authorization"] = "Bearer $token"

        return ResponseEntity
                .ok()
                //.headers(headers)
                .body(userInfo)
    }



}
