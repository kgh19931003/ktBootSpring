package com.godtech.ktboot.form

import java.math.BigDecimal
import java.time.LocalDate

data class ApiResponse(
        val success: Boolean,
        val message: String
)

data class MemberSearchForm(
        var fromDate: LocalDate? = null,
        var toDate: LocalDate? = null,
        var language: String? = null,
        var idx: Int? = null,
        var id: String? = null,
        var password: String? = null,
        var name: String? = null,
        var gender: String? = null
) : ListForm()


data class ProductSearchForm(
        var fromDate: LocalDate? = null,
        var toDate: LocalDate? = null,
        var language: String? = null,
        var idx: Int? = null,
        var name: String? = null,
        var price: Int? = null
) : ListForm()


data class BlogSearchForm(
        var fromDate: LocalDate? = null,
        var toDate: LocalDate? = null,
        var language: String? = null,
        var idx: Int? = null,
        var sourceOrgan: String? = null,
        var title: String? = null,
        val subtitle: String? = null,
        var content: String? = null,
        var category: String? = null,
        var regDate: String? = null
) : ListForm()


data class InquirySearchForm(
        var fromDate: LocalDate? = null,
        var toDate: LocalDate? = null,
        var language: String? = null,
        val idx: Int? = null,
        val category: String? = null,
        val companyName: String? = null,
        val manager: String? = null,
        val tel: String? = null,
        val email: String? = null,
        val content: String? = null,
        val imageUrl: String? = null,
        val privateAgree: String = "N"
) : ListForm()


data class PolicySearchForm(
        var fromDate: LocalDate? = null,
        var toDate: LocalDate? = null,
        var language: String? = null,
        val idx: Int? = null,
        val type: String? = null,
        val content: String? = null,
        val privateAgree: String = "N"
) : ListForm()

data class PerformanceSearchForm(
        var fromDate: LocalDate? = null,
        var toDate: LocalDate? = null,
        var language: String? = null,
        val idx: Int? = null,
        val title: String? = null,
        val subtitle: String? = null,
        val category: String? = null,
        val content: String? = null,
) : ListForm()

data class AlloySearchForm(
        var fromDate: LocalDate? = null,
        var toDate: LocalDate? = null,
        var language: String? = null,
        val idx: Int? = null,
        val title: String? = null,
        val subtitle: String? = null,
        val category: String? = null,
        val type: String? = null,
        val content: String? = null,
) : ListForm()

data class LoginRequest(
        val id: String? = null,
        val pass: String? = null
)


enum class UploadPath(val PATH: String) {
    PERFORMANCE("/upload/editor/performance/images/")
}