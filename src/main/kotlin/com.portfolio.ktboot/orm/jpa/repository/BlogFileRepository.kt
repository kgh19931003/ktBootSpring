package com.portfolio.ktboot.orm.jpa.repository

import com.portfolio.ktboot.orm.jpa.entity.BlogFileEntity
import com.portfolio.ktboot.orm.jpa.entity.ProductFileEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BlogFileRepository : JpaRepository<BlogFileEntity, Int> {
    fun existsByIdx(idx: Int): Boolean

    fun findByIdx(idx: Int): ProductFileEntity?
    fun findByIdxAndOrder(idx: Int, order: Int?): BlogFileEntity?

    fun findByParentIdx(idx: Int): List<ProductFileEntity>

    fun findByParentIdxOrderByOrderAsc(idx: Int): List<BlogFileEntity>

    @Modifying
    @Query(
            "UPDATE ProductFileEntity pi " +
                    "SET pi.order = pi.order - 1 " +
                    "WHERE pi.order > :order AND pi.parentIdx = :prd_idx"
    )
    fun decrementOrderGreaterThan(
            @Param("prd_idx") prd_idx: Int,
            @Param("order") order: Int?
    ): Int

    override fun <S : BlogFileEntity?> save(entity: S): S
    fun deleteByIdx(idx: Int)


}