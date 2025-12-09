package com.portfolio.ktboot.orm.jpa.repository

import com.portfolio.ktboot.orm.jpa.entity.PostFileEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PostFileRepository : JpaRepository<PostFileEntity, Int> {
    fun existsByIdx(idx: Int): Boolean

    fun findByIdx(idx: Int): PostFileEntity?
    fun findByIdxAndOrder(idx: Int, order: Int?): PostFileEntity?

    fun findByParentIdx(idx: Int): List<PostFileEntity>

    fun findByParentIdxOrderByOrderAsc(idx: Int): List<PostFileEntity>

    @Modifying
    @Query(
            "UPDATE PostFileEntity pi " +
                    "SET pi.order = pi.order - 1 " +
                    "WHERE pi.order > :order AND pi.parentIdx = :prd_idx"
    )
    fun decrementOrderGreaterThan(
            @Param("prd_idx") prd_idx: Int,
            @Param("order") order: Int?
    ): Int

    override fun <S : PostFileEntity?> save(entity: S): S
    fun deleteByIdx(idx: Int)

    fun deleteByParentIdx(idx: Int)
}