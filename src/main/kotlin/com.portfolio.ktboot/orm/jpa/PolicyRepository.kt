package com.portfolio.ktboot.orm.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PolicyRepository : JpaRepository<PolicyEntity, Int> {
    fun existsByIdx(idx: Int): Boolean

    fun findByIdx(idx: Int): PolicyEntity

    fun findByTypeAndLanguage(type: String, language: String): PolicyEntity

    fun deleteByIdx(idx: Int)

}