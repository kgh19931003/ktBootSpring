package com.portfolio.ktboot.form

import java.math.BigDecimal
import java.time.LocalDateTime


data class InquiryCreateForm(
        var language: String? = null,
        val category: String? = null,
        val companyName: String? = null,
        val manager: String? = null,
        val tel: String? = null,
        val email: String? = null,
        val content: String? = null,
        val imageUrl: String? = null,
        val privateAgree: String = "N" // "Y" or "N"
)

data class InquiryUpdateForm(
        val idx: Int,
        var language: String? = null,
        val category: String? = null,
        val companyName: String? = null,
        val manager: String? = null,
        val tel: String? = null,
        val email: String? = null,
        val content: String? = null,
        val imageUrl: String? = null,
        val privateAgree: String = "N"
)


data class InquiryList(
        val idx: Int?,
        var language: String? = null,
        val category: String? = null,
        val companyName: String? = null,
        val manager: String? = null,
        val tel: String? = null,
        val email: String? = null,
        val content: String? = null,
        val imageUrl: String? = null,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime? = null
)

data class InquiryDetailSummary(
        val idx: Int,
        var language: String? = null,
        val category: String,
        val companyName: String? = null,
        val manager: String? = null,
        val content: String? = null,
        val tel: String? = null,
        val email: String? = null,
        val imageUrl: String? = null,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime? = null
)

data class InquiryDetail(
        val idx: Int,
        var language: String? = null,
        val category: String,
        val companyName: String? = null,
        val manager: String? = null,
        val tel: String? = null,
        val email: String? = null,
        val content: String,
        val imageUrl: String? = null,
        val privateAgree: String,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime? = null,
        val deletedAt: LocalDateTime? = null,
        val relatedInquiries: List<InquiryDetailSummary>? = null
)