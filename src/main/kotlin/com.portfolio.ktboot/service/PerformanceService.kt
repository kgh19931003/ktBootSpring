package com.portfolio.ktboot.service

import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.orm.jooq.PerformanceDslRepository
import com.portfolio.ktboot.orm.jpa.entity.PerformanceEntity
import com.portfolio.ktboot.orm.jpa.entity.PerformanceFileEntity
import com.portfolio.ktboot.orm.jpa.repository.PerformanceFileRepository
import com.portfolio.ktboot.orm.jpa.repository.PerformanceRepository
import org.springframework.stereotype.Service

@Service
class PerformanceService(
    private val performanceDslRepository: PerformanceDslRepository,
    private val performanceRepository: PerformanceRepository,
    private val performanceImgRepository: PerformanceFileRepository
) {

    /**
     * 특정 상품정보 조회
     */
    fun getPerformanceOne(id: Int): PerformanceList {
        return try {
            performanceRepository.findByIdx(id).let{
                PerformanceList(
                    idx = it.idx,
                    language= it.language,
                    category = it.category,
                    title = it.title,
                    subtitle = it.subtitle,
                    content = it.content,
                    createdAt = it.createdAt
                )
            }
        } catch (e: Exception) {
            throw Exception(e)
        }
    }


    /**
     * 특정 상품정보 조회
     */
    fun getfileOne(prdIdx: Int): List<PerformanceFileEntity> {
        return try {
            performanceImgRepository.findByParentIdxOrderByOrderAsc(prdIdx)
        } catch (e: Exception) {
            throw Exception(e)
        }
    }

    /**
     * 특정 상품정보 리스트 조회
     */
    fun getPerformanceList(form: PerformanceSearchForm): ListPagination<PerformanceList> {
        return try {
            performanceDslRepository.getPerformanceList(form).map{
                PerformanceList(
                    idx = it.idx,
                    language= it.language,
                    category = it.category,
                    title = it.title,
                    subtitle = it.subtitle,
                    createdAt = it.createdAt,
                    content = it.content,
                    firstSrc = it.firstSrc
                )
            }
        } catch (e: Exception) {
            throw Exception(e)
        }
    }



    fun save(performance: PerformanceEntity): PerformanceEntity {
        return performanceRepository.save(performance)
    }

}