package com.godtech.ktboot.form

import java.math.BigDecimal
import java.time.LocalDateTime


data class PolicyCreateForm(
        var language: String? = null,
        val type: String? = null,
        val content: String? = null,
)

data class PolicyUpdateForm(
        val idx: Int,
        var language: String? = null,
        val type: String? = null,
        val content: String? = null,
)


data class PolicyList(
        val idx: Int?,
        var language: String? = null,
        val type: String? = null,
        val content: String? = null,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime? = null
)

data class PolicyDetailSummary(
        val idx: Int,
        var language: String? = null,
        val type: String,
        val content: String? = null,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime? = null
)

data class PolicyDetail(
        val idx: Int,
        var language: String? = null,
        val type: String,
        val content: String,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime? = null,
        val deletedAt: LocalDateTime? = null
)