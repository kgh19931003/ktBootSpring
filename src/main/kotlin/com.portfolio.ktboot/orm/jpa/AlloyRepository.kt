package com.portfolio.ktboot.orm.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AlloyRepository : JpaRepository<AlloyEntity, Int> {
    fun existsByIdx(idx: Int): Boolean

    fun findByIdx(idx: Int): AlloyEntity

    fun deleteByIdx(idx: Int)

}