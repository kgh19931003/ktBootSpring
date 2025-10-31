package com.portfolio.ktboot.orm.jpa

import com.portfolio.ktboot.orm.jpa.MemberEntity
import org.jooq.impl.QOM.Uuid
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<MemberEntity, Int> {
    fun existsById(memId: String): Boolean

    fun findByIdx(idx: Int): MemberEntity

    fun findById(memId: String?): MemberEntity

    fun findByIdAndRefreshToken(memId: String, memUuid: String): MemberEntity


    fun deleteByIdx(idx: Int)

    fun deleteAccessTokenById(memId: String): MemberEntity

    fun deleteRefreshTokenById(memId: String): MemberEntity
}