package com.godtech.ktboot.orm.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ImageIntegrateRepository : JpaRepository<ImageIntegrateEntity, Int> {
    fun findByRefIdAndRefTable(refId: Int, refTable: String): List<ImageIntegrateEntity>
}