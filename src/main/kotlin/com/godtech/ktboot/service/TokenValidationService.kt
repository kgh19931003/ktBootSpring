package com.godtech.ktboot.service

import com.godtech.ktboot.orm.jpa.MemberRepository
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