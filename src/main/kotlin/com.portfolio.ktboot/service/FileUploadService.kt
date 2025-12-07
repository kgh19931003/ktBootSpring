package com.portfolio.ktboot.service

import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.orm.jooq.FileUploadDslRepository
import com.portfolio.ktboot.orm.jpa.entity.FileUploadEntity
import com.portfolio.ktboot.orm.jpa.repository.FileUploadRepository
import org.springframework.stereotype.Service

@Service
class FileUploadService(
        private val fileUploadDslRepository: FileUploadDslRepository,
        private val fileUploadRepository: FileUploadRepository,
) {

    /**
     * 특정 수행사례 정보 조회
     */
    fun getFileUploadOne(id: Int): FileUploadList {
        return try {
            fileUploadRepository.findByIdx(id).let {
                FileUploadList(
                        idx = it?.idx,
                        language = it?.language,
                        type = it?.type,
                        category = it?.category,
                        createdAt = it?.createdAt
                )
            }
        } catch (e: Exception) {
            throw Exception("수행사례 조회 실패: ${e.message}", e)
        }
    }

    /**
     * 특정 수행사례의 전체 파일 조회
     */
    fun getFileOne(parentIdx: Int): List<FileUploadEntity> {
        return try {
            fileUploadRepository.findByParentIdxOrderByOrderAsc(parentIdx)
        } catch (e: Exception) {
            throw Exception("파일 조회 실패: ${e.message}", e)
        }
    }

    /**
     * 카테고리와 타입별로 파일 조회
     * @parfileUpload parentIdx 부모 인덱스
     * @parfileUpload category 카테고리 (metal, plastic, repair)
     * @parfileUpload type 타입 (image, video)
     */
    fun getFilesByCategory(type: String, category: String): List<FileUploadEntity> {
        return try {
            fileUploadRepository.findByCategoryAndTypeOrderByOrderAsc(category, type)
        } catch (e: Exception) {
            throw Exception("카테고리별 파일 조회 실패: ${e.message}", e)
        }
    }

    /**
     * 특정 카테고리의 이미지만 조회
     */
    fun getImagesByCategory(parentIdx: Int, category: String): List<FileUploadEntity> {
        return getFilesByCategory(category, "image")
    }

    /**
     * 특정 카테고리의 비디오만 조회
     */
    fun getVideosByCategory(parentIdx: Int, category: String): List<FileUploadEntity> {
        return getFilesByCategory(category, "video")
    }

    /**
     * 금속 관련 모든 파일 조회
     */
    fun getMetalFiles(parentIdx: Int): Map<String, List<FileUploadEntity>> {
        return mapOf(
                "images" to getImagesByCategory(parentIdx, "metal"),
                "videos" to getVideosByCategory(parentIdx, "metal")
        )
    }

    /**
     * 플라스틱 관련 모든 파일 조회
     */
    fun getPlasticFiles(parentIdx: Int): Map<String, List<FileUploadEntity>> {
        return mapOf(
                "images" to getImagesByCategory(parentIdx, "plastic"),
                "videos" to getVideosByCategory(parentIdx, "plastic")
        )
    }

    /**
     * 보수 관련 모든 파일 조회
     */
    fun getRepairFiles(parentIdx: Int): Map<String, List<FileUploadEntity>> {
        return mapOf(
                "images" to getImagesByCategory(parentIdx, "repair"),
                "videos" to getVideosByCategory(parentIdx, "repair")
        )
    }

    /**
     * 모든 카테고리의 파일을 구조화하여 조회
     */
    fun getAllFilesByCategory(parentIdx: Int): Map<String, Map<String, List<FileUploadEntity>>> {
        return mapOf(
                "metal" to getMetalFiles(parentIdx),
                "plastic" to getPlasticFiles(parentIdx),
                "repair" to getRepairFiles(parentIdx)
        )
    }

    /**
     * 수행사례 리스트 조회
     */
    fun getFileUploadList(form: FileUploadSearchForm): ListPagination<FileUploadList> {
        return try {
            fileUploadDslRepository.getFileUploadList(form).map {
                FileUploadList(
                        idx = it.idx,
                        language = it.language,
                        type = it.type,
                        category = it.category,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt
                )
            }
        } catch (e: Exception) {
            throw Exception("수행사례 리스트 조회 실패: ${e.message}", e)
        }
    }

    /**
     * 수행사례 저장
     */
    fun save(fileUploadCase: FileUploadEntity): FileUploadEntity {
        return try {
            fileUploadRepository.save(fileUploadCase)
        } catch (e: Exception) {
            throw Exception("수행사례 저장 실패: ${e.message}", e)
        }
    }

    /**
     * 파일 저장
     */
    fun saveFile(file: FileUploadEntity): FileUploadEntity {
        return try {
            fileUploadRepository.save(file)
        } catch (e: Exception) {
            throw Exception("파일 저장 실패: ${e.message}", e)
        }
    }

    /**
     * 여러 파일 일괄 저장
     */
    fun saveFiles(files: List<FileUploadEntity>): List<FileUploadEntity> {
        return try {
            fileUploadRepository.saveAll(files.toMutableList()).toList()
        } catch (e: Exception) {
            throw Exception("파일 일괄 저장 실패: ${e.message}", e)
        }
    }

    /**
     * 특정 order 값보다 큰 파일들의 order 감소
     */
    fun decrementOrderGreaterThan(parentIdx: Int, order: Int): Int {
        return try {
            fileUploadRepository.decrementOrderGreaterThan(parentIdx, order)
        } catch (e: Exception) {
            throw Exception("파일 순서 조정 실패: ${e.message}", e)
        }
    }

    /**
     * 파일 삭제 (idx 기준)
     */
    fun deleteFileByIdx(idx: Int) {
        try {
            fileUploadRepository.deleteByIdx(idx)
        } catch (e: Exception) {
            throw Exception("파일 삭제 실패: ${e.message}", e)
        }
    }

    /**
     * 파일 삭제 (UUID 기준)
     */
    fun deleteFileByUuid(uuid: String) {
        try {
            fileUploadRepository.deleteByUuid(uuid)
        } catch (e: Exception) {
            throw Exception("파일 삭제 실패: ${e.message}", e)
        }
    }

    /**
     * 특정 수행사례의 모든 파일 삭제
     */
    fun deleteAllFilesByParentIdx(parentIdx: Int) {
        try {
            fileUploadRepository.deleteByParentIdx(parentIdx)
        } catch (e: Exception) {
            throw Exception("전체 파일 삭제 실패: ${e.message}", e)
        }
    }

    /**
     * 특정 카테고리의 파일만 삭제
     */
    fun deleteFilesByCategory(parentIdx: Int, category: String, type: String? = null) {
        try {
            if (type != null) {
                fileUploadRepository.deleteByParentIdxAndCategoryAndType(parentIdx, category, type)
            } else {
                fileUploadRepository.deleteByParentIdxAndCategory(parentIdx, category)
            }
        } catch (e: Exception) {
            throw Exception("카테고리별 파일 삭제 실패: ${e.message}", e)
        }
    }
}