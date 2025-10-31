package com.portfolio.ktboot.form

import java.math.BigDecimal
import java.time.LocalDateTime


data class BlogCreateForm(
    var language: String? = null,
    var sourceOrgan: String? = null,
    var title: String? = null,
    val subtitle: String? = null,
    var content: String? = null,
    val category: String? = null,
    val regDate: String? = null,
)


data class BlogUpdateForm(
    var idx: Int,
    var language: String? = null,
    var sourceOrgan: String? = null,
    var title: String? = null,
    val subtitle: String? = null,
    var content: String? = null,
    val category: String? = null,
    val regDate: String? = null,
)


data class BlogList(
    val idx: Int?,
    var language: String? = null,
    val title: String? = null,
    val category: String? = null,
    val sourceOrgan: String?= null,
    val regDate: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    var content: String? = null,
    var thumbnail: String? = null,
    val subtitle: String? = null,
    val randomIds: List<Int>? = null // 👈 추가
)


data class BlogDetailSummary(
        val idx: Int?,
        var language: String? = null,
        val sourceOrgan: String?= null,
        val title: String?,
        val subtitle: String?,
        val content: String?,
        val category: String?,
        val regDate: String?,
        val createdAt: LocalDateTime?,
        val updatedAt: LocalDateTime?,
        val thumbnail: String? = null
)

data class BlogDetail(
        val idx: Int?,
        var language: String? = null,
        val sourceOrgan: String?= null,
        val title: String? = null,
        val subtitle: String? = null,
        val content: String? = null,
        val category: String? = null,
        val regDate: String? = null,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime? = null,
        val thumbnail: String? = null,
        val randomBlog: List<BlogDetailSummary>? = null // ✅ 관련 블로그
)