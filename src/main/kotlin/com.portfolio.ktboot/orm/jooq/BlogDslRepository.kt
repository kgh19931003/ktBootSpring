package com.portfolio.ktboot.orm.jooq


import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.jooq.portfolio.tables.references.BLOG
import com.portfolio.ktboot.jooq.portfolio.tables.references.INQUIRY

import com.portfolio.ktboot.proto.isNotNull
import com.portfolio.ktboot.proto.`when`
import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime

@Repository
class BlogDslRepository(
    private val dsl: DSLContext,
) {
    fun getBlogQuery(form: BlogSearchForm): SelectSeekStep1<Record10<Int?, String?, String?, String?, String?, String, String?, String?, LocalDateTime?, LocalDateTime?>, String?> {
        return dsl.select(
                BLOG.IDX.`as`("idx"),
                BLOG.LANGUAGE.`as`("language"),
                BLOG.SOURCE_ORGAN.`as`("sourceOrgan"),
                BLOG.TITLE.`as`("title"),
                BLOG.SUBTITLE.`as`("subtitle"),
                BLOG.CONTENT.cast(String::class.java).`as`("content"), // ✅ CLOB → String 변환
                BLOG.CATEGORY.`as`("category"),
                BLOG.REG_DATE.`as`("regDate"),
                BLOG.CREATED_AT.`as`("createdAt"),
                BLOG.UPDATED_AT.`as`("updatedAt")
            )
            .from(BLOG)
            .where(DSL.noCondition())
            .`when`(form.idx.isNotNull(), BLOG.IDX.eq(form.idx))
            .`when`(form.title?.isNotBlank() == true, BLOG.TITLE.like("%${form.title}%"))
            .`when`(form.content?.isNotBlank() == true, BLOG.CONTENT.like("%${form.content}%"))
            .`when`(form.regDate?.isNotBlank() == true, BLOG.REG_DATE.like("%${form.regDate}%"))
            .`when`(form.language?.isNotBlank() == true, BLOG.LANGUAGE.like("%${form.language}%"))
            .orderBy(BLOG.REG_DATE.desc())  // 🔽 여기 추가
    }


    fun getBlogOne(form: BlogSearchForm): List<BlogList> {
        return getBlogQuery(form).fetch { it.into(BlogList::class.java) }
    }

    fun getBlogList(form: BlogSearchForm): ListPagination<BlogList> {
        val query = getBlogQuery(form)
        return ListPagination.of(dsl, query, form) { record ->
            record.into(BlogList::class.java)
        }
    }

    fun getRandomBlogs(language: String?, limit: Int = 3): List<BlogDetailSummary> {
        return dsl.select(
                BLOG.IDX.`as`("idx"),
                BLOG.SOURCE_ORGAN.`as`("sourceOrgan"),
                BLOG.TITLE.`as`("title"),
                BLOG.CONTENT.`as`("content"),
                BLOG.CATEGORY.`as`("category"),
                BLOG.REG_DATE.`as`("regDate"),
                BLOG.CREATED_AT.`as`("createdAt"),
                BLOG.UPDATED_AT.`as`("updatedAt")
        )
                .from(BLOG)
                .where(DSL.noCondition())
                .`when`(language?.isNotBlank() == true, BLOG.LANGUAGE.like("%${language}%"))
                .orderBy(DSL.field("RAND()"))  // PostgreSQL 기준, MySQL은 RAND(), SQL Server는 NEWID()
                .limit(limit)
                .fetchInto(BlogDetailSummary::class.java)
    }

}