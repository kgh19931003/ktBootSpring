package com.portfolio.ktboot.orm.jooq


import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.jooq.portfolio.tables.references.BLOG
import com.portfolio.ktboot.jooq.portfolio.tables.references.MEMBER
import com.portfolio.ktboot.jooq.portfolio.tables.references.ALLOY
import com.portfolio.ktboot.jooq.portfolio.tables.references.ALLOY_FILE
import com.portfolio.ktboot.proto.isNotNull
import com.portfolio.ktboot.proto.`when`
import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime

@Repository
class AlloyDslRepository(
    private val dsl: DSLContext,
) {
    fun getAlloyQuery(form: AlloySearchForm): SelectSeekStep1<Record10<Int?, String?, String?, String?, String?, String?, String?, LocalDateTime?, LocalDateTime?, String>, LocalDateTime?> {
        val firstFileSrc = DSL
                .select(ALLOY_FILE.SRC)
                .from(ALLOY_FILE)
                .where(ALLOY_FILE.PARENT_IDX.eq(ALLOY.IDX))
                .orderBy(ALLOY_FILE.IDX.asc())
                .limit(1)
                .asField<String>("firstSrc")   // ✅ 3.18에서는 이렇게 써야 함

        return dsl.select(
                ALLOY.IDX.`as`("idx"),
                ALLOY.LANGUAGE.`as`("language"),
                ALLOY.CATEGORY.`as`("category"),
                ALLOY.TYPE.`as`("type"),
                ALLOY.TITLE.`as`("title"),
                ALLOY.SUBTITLE.`as`("subtitle"),
                ALLOY.CONTENT.`as`("content"),
                ALLOY.CREATED_AT.`as`("createdAt"),
                ALLOY.UPDATED_AT.`as`("updatedAt"),
                firstFileSrc
        )
                .from(ALLOY)
                .where(DSL.noCondition())
                .`when`(form.idx.isNotNull(), ALLOY.IDX.eq(form.idx))
                .`when`(form.category?.isNotBlank() == true, ALLOY.CATEGORY.eq(form.category))
                .`when`(form.type?.isNotBlank() == true, ALLOY.TYPE.eq(form.type))
                .`when`(form.title?.isNotBlank() == true, ALLOY.TITLE.like("%${form.title}%"))
                .`when`(form.subtitle?.isNotBlank() == true, ALLOY.SUBTITLE.like("%${form.subtitle}%"))
                .`when`(form.language?.isNotBlank() == true, ALLOY.LANGUAGE.like("%${form.language}%"))
                .orderBy(ALLOY.CREATED_AT.asc()) // ✅ CREATED_AT 기준 정순 정렬
    }

    fun getAlloyTitles(form: AlloySearchForm): List<String?> {
        return dsl.select(ALLOY.TITLE)
                .from(ALLOY)
                .where(DSL.noCondition())
                .`when`(form.idx.isNotNull(), ALLOY.IDX.eq(form.idx))
                .`when`(form.category?.isNotBlank() == true, ALLOY.CATEGORY.eq(form.category))
                .`when`(form.type?.isNotBlank() == true, ALLOY.TYPE.eq(form.type))
                .`when`(form.title?.isNotBlank() == true, ALLOY.TITLE.like("%${form.title}%"))
                .`when`(form.subtitle?.isNotBlank() == true, ALLOY.SUBTITLE.like("%${form.subtitle}%"))
                .`when`(form.language?.isNotBlank() == true, ALLOY.LANGUAGE.like("%${form.language}%"))
                .fetch(ALLOY.TITLE)  // ✅ 결과를 List<String> 으로 바로 매핑
    }

    fun getAlloyOne(form: AlloySearchForm): List<AlloyList> {
        return getAlloyQuery(form).fetch { it.into(AlloyList::class.java) }
    }

    fun getAlloyList(form: AlloySearchForm): ListPagination<AlloyList> {
        val query = getAlloyQuery(form)
        return ListPagination.of(dsl, query, form) { record ->
            record.into(AlloyList::class.java)
        }
    }

}