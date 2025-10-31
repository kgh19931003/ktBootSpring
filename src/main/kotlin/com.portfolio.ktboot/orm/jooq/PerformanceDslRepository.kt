package com.portfolio.ktboot.orm.jooq


import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.jooq.portfolio.tables.references.BLOG
import com.portfolio.ktboot.jooq.portfolio.tables.references.MEMBER
import com.portfolio.ktboot.jooq.portfolio.tables.references.PERFORMANCE
import com.portfolio.ktboot.jooq.portfolio.tables.references.PERFORMANCE_FILE
import com.portfolio.ktboot.proto.isNotNull
import com.portfolio.ktboot.proto.`when`
import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime

@Repository
class PerformanceDslRepository(
    private val dsl: DSLContext,
) {
    fun getPerformanceQuery(form: PerformanceSearchForm): SelectConditionStep<Record9<
            Int?, String?, String?, String?, String?, String?, LocalDateTime?, LocalDateTime?, String?
            >> {
        val firstFileSrc = DSL
                .select(PERFORMANCE_FILE.SRC)
                .from(PERFORMANCE_FILE)
                .where(PERFORMANCE_FILE.PARENT_IDX.eq(PERFORMANCE.IDX))
                .orderBy(PERFORMANCE_FILE.IDX.asc())
                .limit(1)
                .asField<String>("firstSrc")   // ✅ 3.18에서는 이렇게 써야 함

        return dsl.select(
                PERFORMANCE.IDX.`as`("idx"),
                PERFORMANCE.LANGUAGE.`as`("language"),
                PERFORMANCE.CATEGORY.`as`("category"),
                PERFORMANCE.TITLE.`as`("title"),
                PERFORMANCE.SUBTITLE.`as`("subtitle"),
                PERFORMANCE.CONTENT.`as`("content"),
                PERFORMANCE.CREATED_AT.`as`("createdAt"),
                PERFORMANCE.UPDATED_AT.`as`("updatedAt"),
                firstFileSrc
        )
                .from(PERFORMANCE)
                .where(DSL.noCondition())
                .`when`(form.idx.isNotNull(), PERFORMANCE.IDX.eq(form.idx))
                .`when`(form.category?.isNotBlank() == true, PERFORMANCE.CATEGORY.eq(form.category))
                .`when`(form.title?.isNotBlank() == true, PERFORMANCE.TITLE.like("%${form.title}%"))
                .`when`(form.subtitle?.isNotBlank() == true, PERFORMANCE.SUBTITLE.like("%${form.subtitle}%"))
                .`when`(form.language?.isNotBlank() == true, PERFORMANCE.LANGUAGE.like("%${form.language}%"))
    }

    fun getPerformanceOne(form: PerformanceSearchForm): List<PerformanceList> {
        return getPerformanceQuery(form).fetch { it.into(PerformanceList::class.java) }
    }

    fun getPerformanceList(form: PerformanceSearchForm): ListPagination<PerformanceList> {
        val query = getPerformanceQuery(form)
        return ListPagination.of(dsl, query, form) { record ->
            record.into(PerformanceList::class.java)
        }
    }

}