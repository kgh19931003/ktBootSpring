package com.portfolio.ktboot.orm.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InquiryRepository : JpaRepository<InquiryEntity, Int> {
    fun existsByIdx(idx: Int): Boolean

    fun findByIdx(idx: Int): InquiryEntity

    fun deleteByIdx(idx: Int)

}