package com.portfolio.ktboot.orm.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BlogRepository : JpaRepository<BlogEntity, Int> {
    fun existsByIdx(idx: Int): Boolean

    fun findByIdx(idx: Int): BlogEntity

    fun deleteByIdx(idx: Int)

}