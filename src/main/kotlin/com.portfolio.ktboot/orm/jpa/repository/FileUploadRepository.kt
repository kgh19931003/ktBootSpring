package com.portfolio.ktboot.orm.jpa.repository

import com.portfolio.ktboot.orm.jpa.entity.FileUploadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface FileUploadRepository : JpaRepository<FileUploadEntity, Int> {

    // 기존 메서드들
    fun existsByIdx(idx: Int): Boolean

    fun findByIdx(idx: Int): FileUploadEntity?

    fun findByIdxAndOrder(idx: Int, order: Int?): FileUploadEntity?

    fun findByParentIdx(idx: Int): List<FileUploadEntity>

    fun findByParentIdxOrderByOrderAsc(idx: Int): List<FileUploadEntity>

    // UUID 관련 메서드
    fun findByUuid(uuid: String): FileUploadEntity?

    fun existsByUuid(uuid: String): Boolean

    // 카테고리와 타입으로 조회
    fun findByParentIdxAndCategoryOrderByOrderAsc(
            parentIdx: Int,
            category: String
    ): List<FileUploadEntity>

    fun findByParentIdxAndTypeOrderByOrderAsc(
            parentIdx: Int,
            type: String
    ): List<FileUploadEntity>

    fun findByCategoryAndTypeOrderByOrderAsc(
            category: String,
            type: String
    ): List<FileUploadEntity>

    // 카테고리별 존재 여부 확인
    fun existsByParentIdxAndCategory(parentIdx: Int, category: String): Boolean

    fun existsByParentIdxAndCategoryAndType(
            parentIdx: Int,
            category: String,
            type: String
    ): Boolean

    // Order 조정 쿼리
    @Modifying
    @Query(
            "UPDATE FileUploadEntity f " +
                    "SET f.order = f.order - 1 " +
                    "WHERE f.order > :order AND f.parentIdx = :parentIdx"
    )
    fun decrementOrderGreaterThan(
            @Param("parentIdx") parentIdx: Int,
            @Param("order") order: Int?
    ): Int

    @Modifying
    @Query(
            "UPDATE FileUploadEntity f " +
                    "SET f.order = f.order - 1 " +
                    "WHERE f.order > :order " +
                    "AND f.parentIdx = :parentIdx " +
                    "AND f.category = :category " +
                    "AND f.type = :type"
    )
    fun decrementOrderGreaterThanByCategory(
            @Param("parentIdx") parentIdx: Int,
            @Param("order") order: Int?,
            @Param("category") category: String,
            @Param("type") type: String
    ): Int

    @Modifying
    @Query(
            "UPDATE FileUploadEntity f " +
                    "SET f.order = f.order + 1 " +
                    "WHERE f.order >= :order AND f.parentIdx = :parentIdx"
    )
    fun incrementOrderGreaterThanOrEqual(
            @Param("parentIdx") parentIdx: Int,
            @Param("order") order: Int
    ): Int

    // 삭제 메서드들
    fun deleteByIdx(idx: Int)

    fun deleteByUuid(uuid: String)

    fun deleteByParentIdx(idx: Int)

    fun deleteByParentIdxAndCategory(parentIdx: Int, category: String)

    fun deleteByParentIdxAndType(parentIdx: Int, type: String)

    fun deleteByParentIdxAndCategoryAndType(
            parentIdx: Int,
            category: String,
            type: String
    )

    @Modifying
    @Query("DELETE FROM FileUploadEntity f WHERE f.uuid IN :uuids")
    fun deleteByUuidIn(@Param("uuids") uuids: List<String>)

    @Modifying
    @Query("DELETE FROM FileUploadEntity f WHERE f.idx IN :indexes")
    fun deleteByIdxIn(@Param("indexes") indexes: List<Int>)

    // 카운트 메서드들
    fun countByParentIdx(parentIdx: Int): Long

    fun countByParentIdxAndCategory(parentIdx: Int, category: String): Long

    fun countByParentIdxAndCategoryAndType(
            parentIdx: Int,
            category: String,
            type: String
    ): Long

    // 최대/최소 order 조회
    @Query(
            "SELECT MAX(f.order) FROM FileUploadEntity f " +
                    "WHERE f.parentIdx = :parentIdx"
    )
    fun findMaxOrderByParentIdx(@Param("parentIdx") parentIdx: Int): Int?

    @Query(
            "SELECT MAX(f.order) FROM FileUploadEntity f " +
                    "WHERE f.parentIdx = :parentIdx " +
                    "AND f.category = :category " +
                    "AND f.type = :type"
    )
    fun findMaxOrderByParentIdxAndCategoryAndType(
            @Param("parentIdx") parentIdx: Int,
            @Param("category") category: String,
            @Param("type") type: String
    ): Int?

    // 저장
    override fun <S : FileUploadEntity?> save(entity: S): S

    override fun <S : FileUploadEntity?> saveAll(entities: MutableIterable<S>): MutableList<S>
}