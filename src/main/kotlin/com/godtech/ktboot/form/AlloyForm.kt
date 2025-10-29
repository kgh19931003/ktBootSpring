package com.godtech.ktboot.form

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime


data class AlloyUpdateForm(
        var language: String? = null,
        val category: String? = null,
        val type: String? = null,
        val title: String? = null,
        val subtitle: String? = null,
        val content: String? = null,
        val fileUrl: List<String>? = null,
        val fileIndex: List<Int>? = null,
        val fileOriginalIndex: List<Int>? = null,
        val fileDeleteIndex: List<Int>? = null,
        val fileMultipartFileOrder: List<Int>? = null,
        val fileOrder: List<Int>? = null,
        val fileUuid: List<String>? = null,
        val fileDeleteUuid: List<String>? = null
        // 이미지 제외 — MultipartFile로 따로 받음
)

data class AlloyCreateForm(
    var language: String? = null,
    val category: String? = null,
    val type: String? = null,
    val title: String? = null,
    val content: String? = null,
    val subtitle: String? = null,
    val fileUrl: List<String>? = null,
    val fileIndex: List<Int>? = null,
    val fileOriginalIndex: List<Int>? = null,
    val fileDeleteIndex: List<Int>? = null,
    val fileMultipartFileOrder: List<Int>? = null,
    val fileOrder: List<Int>? = null,
    val fileUuid: List<String>? = null,
    val fileDeleteUuid: List<String>? = null
)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class AlloyList(
        val idx: Int?,
        var language: String? = null,
        val fileIndex: List<Int?>? = null,
        val fileOriginalIndex: List<Int?>? = null,
        val fileOrder: List<Int?>? = null,
        val title: String? = null,
        val subtitle: String? = null,
        val type: String? = null,
        val fileUuid: List<String?>? = null,
        var fileImage: List<String?>? = null,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime? = null,
        val content: String? = null,
        val firstSrc: String? = null
)


