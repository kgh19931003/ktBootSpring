package com.portfolio.ktboot.service



import com.portfolio.ktboot.form.ListPagination
import com.portfolio.ktboot.form.MemberList
import com.portfolio.ktboot.form.MemberSearchForm
import com.portfolio.ktboot.orm.jooq.MemberDslRepository
import com.portfolio.ktboot.orm.jpa.MemberEntity
import com.portfolio.ktboot.orm.jpa.MemberRepository
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberDslRepository: MemberDslRepository,
    private val memberRepository: MemberRepository
) {

    /**
     * 특정 회원정보 조회
     */
    fun getMemberOne(id: Int): MemberList {
        return try {
            memberRepository.findByIdx(id).let{
                MemberList(
                    idx = it.idx,
                    id = it.id,
                    name = it.name,
                    gender = it.gender,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
        } catch (e: Exception) {
            throw Exception(e)
        }
    }

    /**
     * 특정 회원정보 조회
     */
    fun getMemberList(form: MemberSearchForm): ListPagination<MemberList> {
        return try {
            memberDslRepository.getMemberList(form).map{
                MemberList(
                    idx = it.idx,
                    id = it.id,
                    name = it.name,
                    gender = it.gender,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
        } catch (e: Exception) {
            throw Exception(e)
        }
    }


    fun findByMemId(id: String): MemberEntity {
        val member = memberRepository.findById(id)
        return member
    }


    fun loadUserByid(username: String): User {
        val member = memberRepository.findById(username)
        return User(member.id, member.password, emptyList())
    }

    fun existsMember(id: String): Boolean {
        return memberRepository.existsById(id)
    }

    fun findMember(id: String): User {
        val member = memberRepository.findById(id)
        return User(member.id, member.password, emptyList())
    }

    fun getStoredRefreshToken(loginId: String): String? {
        // DB에서 저장된 리프레시 토큰을 조회하는 로직 구현
        // 리프레시 토큰이 없으면 null 반환
        return memberRepository.findById(loginId).refreshToken
    }

    fun deleteAccessToken(loginId: String): String? {
        // DB에서 저장된 리프레시 토큰을 조회하는 로직 구현
        // 리프레시 토큰이 없으면 null 반환
        return memberRepository.deleteAccessTokenById(loginId).accessToken
    }


    fun deleteRefreshToken(loginId: String): String? {
        // DB에서 저장된 리프레시 토큰을 조회하는 로직 구현
        // 리프레시 토큰이 없으면 null 반환
        return memberRepository.deleteRefreshTokenById(loginId).refreshToken
    }


    fun save(member: MemberEntity): MemberEntity {
        return memberRepository.save(member)
    }

}