package com.portfolio.ktboot.form

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

data class PostSearchForm(
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


/**
 * 수행사례 검색 폼
 */
data class FileUploadSearchForm(
        val idx: Int? = null,
        val language: String? = null,
        val type: String? = null,
        val category: String? = null,
        val title: String? = null
) : ListForm()

// Response DTO
// Response DTO
data class FileUploadDetailResponse(
        val idx: Int,
        val language: String,
        // 금속 이미지
        val fileImage: List<String> = emptyList(),
        val fileImageIndex: List<Int> = emptyList(),
        val fileImageOrder: List<Int> = emptyList(),
        val fileImageDeleteIndex: List<Int> = emptyList(),
        val fileImageMultipartFileOrder: List<Int> = emptyList(),
        val fileImageOriginalName: List<String> = emptyList(),
        // 금속 비디오
        val fileVideo: List<String> = emptyList(),
        val fileIndex: List<Int> = emptyList(),
        val fileOrder: List<Int> = emptyList(),
        val fileDeleteIndex: List<Int> = emptyList(),
        val fileMultipartFileOrder: List<Int> = emptyList(),
        val fileOriginalName: List<String> = emptyList(),
)



data class LoginRequest(
        val id: String? = null,
        val pass: String? = null
)

