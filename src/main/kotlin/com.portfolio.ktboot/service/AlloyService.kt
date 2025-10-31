package com.portfolio.ktboot.service

import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.orm.jooq.AlloyDslRepository
import com.portfolio.ktboot.orm.jpa.AlloyEntity
import com.portfolio.ktboot.orm.jpa.AlloyFileEntity
import com.portfolio.ktboot.orm.jpa.AlloyFileRepository
import com.portfolio.ktboot.orm.jpa.AlloyRepository
import org.springframework.stereotype.Service

@Service
class AlloyService(
        private val alloyDslRepository: AlloyDslRepository,
        private val alloyRepository: AlloyRepository,
        private val alloyImgRepository: AlloyFileRepository
) {

    /**
     * 특정 강종 조회
     */
    fun getAlloyOne(id: Int): AlloyList {
        return try {
            alloyRepository.findByIdx(id).let{
                AlloyList(
                    idx = it.idx,
                    language= it.language,
                    title = it.title,
                    subtitle = it.subtitle,
                    type = it.type,
                    content = it.content,
                    createdAt = it.createdAt
                )
            }
        } catch (e: Exception) {
            throw Exception(e)
        }
    }


    /**
     * 특정 강종 조회
     */
    fun getfileOne(prdIdx: Int): List<AlloyFileEntity> {
        return try {
            alloyImgRepository.findByParentIdxOrderByOrderAsc(prdIdx)
        } catch (e: Exception) {
            throw Exception(e)
        }
    }

    /**
     * 특정 강종 리스트 조회
     */
    fun getAlloyList(form: AlloySearchForm): ListPagination<AlloyList> {
        return try {

            alloyDslRepository.getAlloyList(form).map{
                AlloyList(
                    idx = it.idx,
                    language= it.language,
                    title = it.title,
                    subtitle = it.subtitle,
                    type = it.type,
                    createdAt = it.createdAt,
                    content = it.content,
                    firstSrc = it.firstSrc
                )
            }

        } catch (e: Exception) {
            throw Exception(e)
        }
    }



    fun save(alloy: AlloyEntity): AlloyEntity {
        return alloyRepository.save(alloy)
    }

}