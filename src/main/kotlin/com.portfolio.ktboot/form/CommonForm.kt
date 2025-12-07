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
        val fileFileImage: List<String> = emptyList(),
        val fileFileIndex: List<Int> = emptyList(),
        val fileFileOrder: List<Int> = emptyList(),
        val fileFileDeleteIndex: List<Int> = emptyList(),
        val fileFileMultipartFileOrder: List<Int> = emptyList(),
        val fileFileOriginalName: List<String> = emptyList(),

        // 플라스틱 이미지
        val plasticImage: List<String> = emptyList(),
        val plasticImageIndex: List<Int> = emptyList(),
        val plasticImageOrder: List<Int> = emptyList(),
        val plasticImageDeleteIndex: List<Int> = emptyList(),
        val plasticImageMultipartFileOrder: List<Int> = emptyList(),
        val plasticImageOriginalName: List<String> = emptyList(),
        // 플라스틱 비디오
        val plasticFileImage: List<String> = emptyList(),
        val plasticFileIndex: List<Int> = emptyList(),
        val plasticFileOrder: List<Int> = emptyList(),
        val plasticFileDeleteIndex: List<Int> = emptyList(),
        val plasticFileMultipartFileOrder: List<Int> = emptyList(),
        val plasticFileOriginalName: List<String> = emptyList(),
        // 보수 이미지
        val repairImage: List<String> = emptyList(),
        val repairImageIndex: List<Int> = emptyList(),
        val repairImageOrder: List<Int> = emptyList(),
        val repairImageDeleteIndex: List<Int> = emptyList(),
        val repairImageMultipartFileOrder: List<Int> = emptyList(),
        val repairImageOriginalName: List<String> = emptyList(),
        // 보수 비디오
        val repairFileImage: List<String> = emptyList(),
        val repairFileIndex: List<Int> = emptyList(),
        val repairFileOrder: List<Int> = emptyList(),
        val repairFileDeleteIndex: List<Int> = emptyList(),
        val repairFileMultipartFileOrder: List<Int> = emptyList(),
        val repairFileOriginalName: List<String> = emptyList(),
)



data class LoginRequest(
        val id: String? = null,
        val pass: String? = null
)


enum class UploadPath(val PATH: String) {
    PERFORMANCE("/upload/editor/performance/images/")
}