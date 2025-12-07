package com.portfolio.ktboot.form

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

/**
 * 수행사례 수정 폼
 * 금속, 플라스틱, 보수 각각의 이미지/비디오를 관리
 */
data class FileUploadUpdateForm(
        var language: String? = null,
        val type: String? = null,
        val category: String? = null,
        val title: String? = null,

        // 금속 이미지 관련
        val fileImageOrder: List<Int>? = null,
        val fileImageIndex: List<Int>? = null,
        val fileImageOriginalIndex: List<Int>? = null,
        val fileImageDeleteIndex: List<Int>? = null,
        val fileImageMultipartFileOrder: List<Int>? = null,

        // 금속 비디오 관련
        val fileVideoOrder: List<Int>? = null,
        val fileVideoIndex: List<Int>? = null,
        val fileVideoOriginalIndex: List<Int>? = null,
        val fileVideoDeleteIndex: List<Int>? = null,
        val fileVideoMultipartFileOrder: List<Int>? = null,


        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime? = null,
)

/**
 * 수행사례 생성 폼
 */
data class FileUploadCreateForm(
        val idx: Int? = null,
        var language: String? = null,
        val type: String? = null,
        val category: String? = null,
        val title: String? = null,

        // 금속 이미지 관련
        val fileImageOrder: List<Int>? = null,
        val fileImageIndex: List<Int>? = null,
        val fileImageOriginalIndex: List<Int>? = null,
        val fileImageDeleteIndex: List<Int>? = null,
        val fileImageMultipartFileOrder: List<Int>? = null,

        // 금속 비디오 관련
        val fileVideoOrder: List<Int>? = null,
        val fileVideoIndex: List<Int>? = null,
        val fileVideoOriginalIndex: List<Int>? = null,
        val fileVideoDeleteIndex: List<Int>? = null,
        val fileVideoMultipartFileOrder: List<Int>? = null,



        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime? = null
)

/**
 * 수행사례 리스트 응답
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class FileUploadList(
        var idx: Int? = null,
        var language: String? = null,
        var type: String? = null,
        var category: String? = null,

        // 금속 이미지 관련
        var fileImage: List<String?>? = null,
        var fileImageIndex: List<Int?>? = null,
        var fileImageOriginalIndex: List<Int?>? = null,
        var fileImageOrder: List<Int?>? = null,
        var fileImageDeleteIndex: List<Int?>? = null,
        var fileImageMultipartFileOrder: List<Int?>? = null,

        // 금속 비디오 관련
        var fileFileImage: List<String?>? = null,
        var fileFileIndex: List<Int?>? = null,
        var fileFileOriginalIndex: List<Int?>? = null,
        var fileFileOrder: List<Int?>? = null,
        var fileFileDeleteIndex: List<Int?>? = null,
        var fileFileMultipartFileOrder: List<Int?>? = null,



        var createdAt: LocalDateTime? = null,
        var updatedAt: LocalDateTime? = null,
)

