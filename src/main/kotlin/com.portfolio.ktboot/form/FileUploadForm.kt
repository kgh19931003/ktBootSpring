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
        val metalImageOrder: List<Int>? = null,
        val metalImageIndex: List<Int>? = null,
        val metalImageOriginalIndex: List<Int>? = null,
        val metalImageDeleteIndex: List<Int>? = null,
        val metalImageMultipartFileOrder: List<Int>? = null,

        // 금속 비디오 관련
        val metalVideoOrder: List<Int>? = null,
        val metalVideoIndex: List<Int>? = null,
        val metalVideoOriginalIndex: List<Int>? = null,
        val metalVideoDeleteIndex: List<Int>? = null,
        val metalVideoMultipartFileOrder: List<Int>? = null,

        // 플라스틱 이미지 관련
        val plasticImageOrder: List<Int>? = null,
        val plasticImageIndex: List<Int>? = null,
        val plasticImageOriginalIndex: List<Int>? = null,
        val plasticImageDeleteIndex: List<Int>? = null,
        val plasticImageMultipartFileOrder: List<Int>? = null,

        // 플라스틱 비디오 관련
        val plasticVideoOrder: List<Int>? = null,
        val plasticVideoIndex: List<Int>? = null,
        val plasticVideoOriginalIndex: List<Int>? = null,
        val plasticVideoDeleteIndex: List<Int>? = null,
        val plasticVideoMultipartFileOrder: List<Int>? = null,

        // 보수 이미지 관련
        val repairImageOrder: List<Int>? = null,
        val repairImageIndex: List<Int>? = null,
        val repairImageOriginalIndex: List<Int>? = null,
        val repairImageDeleteIndex: List<Int>? = null,
        val repairImageMultipartFileOrder: List<Int>? = null,

        // 보수 비디오 관련
        val repairVideoOrder: List<Int>? = null,
        val repairVideoIndex: List<Int>? = null,
        val repairVideoOriginalIndex: List<Int>? = null,
        val repairVideoDeleteIndex: List<Int>? = null,
        val repairVideoMultipartFileOrder: List<Int>? = null,

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
        val metalImageOrder: List<Int>? = null,
        val metalImageIndex: List<Int>? = null,
        val metalImageOriginalIndex: List<Int>? = null,
        val metalImageDeleteIndex: List<Int>? = null,
        val metalImageMultipartFileOrder: List<Int>? = null,

        // 금속 비디오 관련
        val metalVideoOrder: List<Int>? = null,
        val metalVideoIndex: List<Int>? = null,
        val metalVideoOriginalIndex: List<Int>? = null,
        val metalVideoDeleteIndex: List<Int>? = null,
        val metalVideoMultipartFileOrder: List<Int>? = null,

        // 플라스틱 이미지 관련
        val plasticImageOrder: List<Int>? = null,
        val plasticImageIndex: List<Int>? = null,
        val plasticImageOriginalIndex: List<Int>? = null,
        val plasticImageDeleteIndex: List<Int>? = null,
        val plasticImageMultipartFileOrder: List<Int>? = null,

        // 플라스틱 비디오 관련
        val plasticVideoOrder: List<Int>? = null,
        val plasticVideoIndex: List<Int>? = null,
        val plasticVideoOriginalIndex: List<Int>? = null,
        val plasticVideoDeleteIndex: List<Int>? = null,
        val plasticVideoMultipartFileOrder: List<Int>? = null,

        // 보수 이미지 관련
        val repairImageOrder: List<Int>? = null,
        val repairImageIndex: List<Int>? = null,
        val repairImageOriginalIndex: List<Int>? = null,
        val repairImageDeleteIndex: List<Int>? = null,
        val repairImageMultipartFileOrder: List<Int>? = null,

        // 보수 비디오 관련
        val repairVideoOrder: List<Int>? = null,
        val repairVideoIndex: List<Int>? = null,
        val repairVideoOriginalIndex: List<Int>? = null,
        val repairVideoDeleteIndex: List<Int>? = null,
        val repairVideoMultipartFileOrder: List<Int>? = null,

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
        var metalImage: List<String?>? = null,
        var metalImageIndex: List<Int?>? = null,
        var metalImageOriginalIndex: List<Int?>? = null,
        var metalImageOrder: List<Int?>? = null,
        var metalImageDeleteIndex: List<Int?>? = null,
        var metalImageMultipartFileOrder: List<Int?>? = null,

        // 금속 비디오 관련
        var metalFileImage: List<String?>? = null,
        var metalFileIndex: List<Int?>? = null,
        var metalFileOriginalIndex: List<Int?>? = null,
        var metalFileOrder: List<Int?>? = null,
        var metalFileDeleteIndex: List<Int?>? = null,
        var metalFileMultipartFileOrder: List<Int?>? = null,

        // 플라스틱 이미지 관련
        var plasticImage: List<String?>? = null,
        var plasticImageIndex: List<Int?>? = null,
        var plasticImageOriginalIndex: List<Int?>? = null,
        var plasticImageOrder: List<Int?>? = null,
        var plasticImageDeleteIndex: List<Int?>? = null,
        var plasticImageMultipartFileOrder: List<Int?>? = null,

        // 플라스틱 비디오 관련
        var plasticFileImage: List<String?>? = null,
        var plasticFileIndex: List<Int?>? = null,
        var plasticFileOriginalIndex: List<Int?>? = null,
        var plasticFileOrder: List<Int?>? = null,
        var plasticFileDeleteIndex: List<Int?>? = null,
        var plasticFileMultipartFileOrder: List<Int?>? = null,

        // 보수 이미지 관련
        var repairImage: List<String?>? = null,
        var repairImageIndex: List<Int?>? = null,
        var repairImageOriginalIndex: List<Int?>? = null,
        var repairImageOrder: List<Int?>? = null,
        var repairImageDeleteIndex: List<Int?>? = null,
        var repairImageMultipartFileOrder: List<Int?>? = null,

        // 보수 비디오 관련
        var repairFileImage: List<String?>? = null,
        var repairFileIndex: List<Int?>? = null,
        var repairFileOriginalIndex: List<Int?>? = null,
        var repairFileOrder: List<Int?>? = null,
        var repairFileDeleteIndex: List<Int?>? = null,
        var repairFileMultipartFileOrder: List<Int?>? = null,

        var createdAt: LocalDateTime? = null,
        var updatedAt: LocalDateTime? = null,
)

