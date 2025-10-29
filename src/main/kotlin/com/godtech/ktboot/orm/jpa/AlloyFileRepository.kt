package com.godtech.ktboot.orm.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AlloyFileRepository : JpaRepository<AlloyFileEntity, Int> {
    fun existsByIdx(idx: Int): Boolean

    fun findByIdx(idx: Int): AlloyFileEntity?
    fun findByIdxAndOrder(idx: Int, order: Int?): AlloyFileEntity?

    fun findByParentIdx(idx: Int): List<AlloyFileEntity>

    fun findByParentIdxOrderByOrderAsc(idx: Int): List<AlloyFileEntity>

    @Modifying
    @Query(
            "UPDATE AlloyFileEntity pi " +
                    "SET pi.order = pi.order - 1 " +
                    "WHERE pi.order > :order AND pi.parentIdx = :prd_idx"
    )
    fun decrementOrderGreaterThan(
            @Param("prd_idx") prd_idx: Int,
            @Param("order") order: Int?
    ): Int

    override fun <S : AlloyFileEntity?> save(entity: S): S
    fun deleteByIdx(idx: Int)

    fun deleteByParentIdx(idx: Int)
}