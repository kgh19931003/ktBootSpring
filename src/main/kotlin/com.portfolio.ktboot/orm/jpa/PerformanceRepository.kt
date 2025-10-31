package com.portfolio.ktboot.orm.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PerformanceRepository : JpaRepository<PerformanceEntity, Int> {
    fun existsByIdx(idx: Int): Boolean

    fun findByIdx(idx: Int): PerformanceEntity

    fun deleteByIdx(idx: Int)

}