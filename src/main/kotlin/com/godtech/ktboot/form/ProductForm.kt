package com.godtech.ktboot.form

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime


data class ProductUpdateForm(
        var language: String? = null,
        val name: String? = null,
        val price: Int? = null,
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

data class ProductCreateForm(
    val idx: Int?,
    var language: String? = null,
    val name: String? = null,
    val price: String? = null,
)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class ProductList(
        val idx: Int?,
        var language: String? = null,
        val fileIndex: List<Int?>? = null,
        val fileOriginalIndex: List<Int?>? = null,
        val fileOrder: List<Int?>? = null,
        val name: String? = null,
        val price: Int? = null,
        val fileUuid: List<String?>? = null,
        var fileImage: List<String?>? = null,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime? = null,
)


