package com.portfolio.ktboot.orm.jooq

import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.jooq.portfolio.tables.references.BLOG
import com.portfolio.ktboot.jooq.portfolio.tables.references.INQUIRY
import com.portfolio.ktboot.jooq.portfolio.tables.references.POLICY
import com.portfolio.ktboot.proto.isNotNull
import com.portfolio.ktboot.proto.`when`
import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class PolicyDslRepository(
        private val dsl: DSLContext,
) {

    // ✅ 공통 조건절 생성
    fun getPolicyQuery(form: PolicySearchForm): SelectConditionStep<Record6<Int?, String?, String?, String, LocalDateTime?, LocalDateTime?>> {
        return dsl.select(
                POLICY.IDX.`as`("idx"),
                POLICY.LANGUAGE.`as`("language"),
                POLICY.TYPE.`as`("type"),
                POLICY.CONTENT.cast(String::class.java).`as`("content"), // ✅ CLOB → String 변환
                POLICY.CREATED_AT.`as`("createdAt"),
                POLICY.UPDATED_AT.`as`("updatedAt")
        )
                .from(POLICY)
                .where(POLICY.DELETED_AT.isNull) // Soft delete 필터
                .`when`(form.type?.isNotBlank() == true, POLICY.TYPE.like("%${form.type}%"))
                .`when`(form.content?.isNotBlank() == true, POLICY.CONTENT.like("%${form.content}%"))
                .`when`(form.language?.isNotBlank() == true, POLICY.LANGUAGE.like("%${form.language}%"))
    }

    // ✅ 단건 조회
    fun getPolicyOne(form: PolicySearchForm): List<PolicyList> {
        return getPolicyQuery(form)
                .limit(1)
                .fetch { it.into(PolicyList::class.java) }
    }

    // ✅ 리스트 조회 (페이징 포함)
    fun getPolicyList(form: PolicySearchForm): ListPagination<PolicyList> {
        val query = getPolicyQuery(form)
        return ListPagination.of(dsl, query, form) { record ->
            record.into(PolicyList::class.java)
        }
    }
}
