package com.portfolio.ktboot.form

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime


data class ProductUpdateForm(
        var language: String? = null,
        val name: String? = null,
        val price: Int? = null,

        // 이미지 관련
        val fileUrl: List<String>? = null,
        val fileIndex: List<Int>? = null,
        val fileOriginalIndex: List<Int>? = null,
        val fileDeleteIndex: List<Int>? = null,
        val fileMultipartFileOrder: List<Int>? = null,
        val fileOrder: List<Int>? = null,
        val fileUuid: List<String>? = null,
        val fileDeleteUuid: List<String>? = null,

        // 비디오 관련
        val videoUrl: List<String>? = null,
        val videoIndex: List<Int>? = null,
        val videoOriginalIndex: List<Int>? = null,
        val videoDeleteIndex: List<Int>? = null,
        val videoMultipartFileOrder: List<Int>? = null,
        val videoOrder: List<Int>? = null,
        val videoUuid: List<String>? = null,
        val videoDeleteUuid: List<String>? = null
        // 이미지/비디오 파일 제외 — MultipartFile로 따로 받음
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

        // 이미지 관련
        val fileIndex: List<Int?>? = null,
        val fileOriginalIndex: List<Int?>? = null,
        val fileOrder: List<Int?>? = null,
        val fileUuid: List<String?>? = null,
        val fileDeleteUuid: List<String?>? = null,
        val fileDeleteIndex: List<Int?>? = null,
        val fileMultipartFileOrder: List<Int?>? = null,
        var fileImage: List<String?>? = null,

        // 비디오 관련
        val videoIndex: List<Int?>? = null,
        val videoOriginalIndex: List<Int?>? = null,
        val videoOrder: List<Int?>? = null,
        val videoUuid: List<String?>? = null,
        val videoDeleteUuid: List<String?>? = null,
        val videoDeleteIndex: List<Int?>? = null,
        val videoMultipartFileOrder: List<Int?>? = null,
        var fileVideo: List<String?>? = null,

        val name: String? = null,
        val price: Int? = null,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime? = null,
)