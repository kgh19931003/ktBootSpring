package com.portfolio.ktboot.service

import com.portfolio.ktboot.orm.jpa.repository.MemberRepository
import org.springframework.stereotype.Service

// 새로운 Service 클래스 생성
@Service
class TokenValidationService(
    private val memberRepository: MemberRepository
) {
    fun validateRefreshTokenInDb(refreshToken: String, memId: String): Boolean {
        val member = memberRepository.findByIdAndRefreshToken(memId, refreshToken)
        return member.refreshToken == refreshToken
    }
}