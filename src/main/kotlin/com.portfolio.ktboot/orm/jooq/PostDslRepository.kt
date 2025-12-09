package com.portfolio.ktboot.orm.jooq


import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.jooq.portfolio.tables.references.POST
import com.portfolio.ktboot.jooq.portfolio.tables.references.POST_FILE
import com.portfolio.ktboot.proto.isNotNull
import com.portfolio.ktboot.proto.`when`
import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class PostDslRepository(
    private val dsl: DSLContext,
) {
    fun getPostQuery(form: PostSearchForm): SelectConditionStep<Record9<
            Int?, String?, String?, String?, String?, String?, LocalDateTime?, LocalDateTime?, String?
            >> {
        val firstFileSrc = DSL
                .select(POST_FILE.SRC)
                .from(POST_FILE)
                .where(POST_FILE.PARENT_IDX.eq(POST.IDX))
                .orderBy(POST_FILE.IDX.asc())
                .limit(1)
                .asField<String>("firstSrc")   // ✅ 3.18에서는 이렇게 써야 함

        return dsl.select(
                POST.IDX.`as`("idx"),
                POST.LANGUAGE.`as`("language"),
                POST.CATEGORY.`as`("category"),
                POST.TITLE.`as`("title"),
                POST.SUBTITLE.`as`("subtitle"),
                POST.CONTENT.`as`("content"),
                POST.CREATED_AT.`as`("createdAt"),
                POST.UPDATED_AT.`as`("updatedAt"),
                firstFileSrc
        )
                .from(POST)
                .where(DSL.noCondition())
                .`when`(form.idx.isNotNull(), POST.IDX.eq(form.idx))
                .`when`(form.category?.isNotBlank() == true, POST.CATEGORY.eq(form.category))
                .`when`(form.title?.isNotBlank() == true, POST.TITLE.like("%${form.title}%"))
                .`when`(form.subtitle?.isNotBlank() == true, POST.SUBTITLE.like("%${form.subtitle}%"))
                .`when`(form.language?.isNotBlank() == true, POST.LANGUAGE.like("%${form.language}%"))
    }

    fun getPostOne(form: PostSearchForm): List<PostList> {
        return getPostQuery(form).fetch { it.into(PostList::class.java) }
    }

    fun getPostList(form: PostSearchForm): ListPagination<PostList> {
        val query = getPostQuery(form)
        return ListPagination.of(dsl, query, form) { record ->
            record.into(PostList::class.java)
        }
    }

}