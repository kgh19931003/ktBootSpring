package com.godtech.ktboot.orm.jooq

import com.godtech.ktboot.form.*
import com.godtech.ktboot.jooq.godtech.tables.references.BLOG
import com.godtech.ktboot.jooq.godtech.tables.references.INQUIRY
import com.godtech.ktboot.proto.isNotNull
import com.godtech.ktboot.proto.`when`
import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class InquiryDslRepository(
        private val dsl: DSLContext,
) {

    // ✅ 공통 조건절 생성
    fun getInquiryQuery(form: InquirySearchForm): SelectConditionStep<Record11<Int?, String?, String?, String?, String?, String?, String?, String, String?, LocalDateTime?, LocalDateTime?>> {
        return dsl.select(
                INQUIRY.IDX.`as`("idx"),
                INQUIRY.LANGUAGE.`as`("language"),
                INQUIRY.CATEGORY.`as`("category"),
                INQUIRY.COMPANY_NAME.`as`("companyName"),
                INQUIRY.MANAGER.`as`("manager"),
                INQUIRY.TEL.`as`("tel"),
                INQUIRY.EMAIL.`as`("email"),
                INQUIRY.CONTENT.cast(String::class.java).`as`("content"), // ✅ CLOB → String 변환
                INQUIRY.IMAGE_URL.`as`("imageUrl"),
                INQUIRY.CREATED_AT.`as`("createdAt"),
                INQUIRY.UPDATED_AT.`as`("updatedAt")
        )
                .from(INQUIRY)
                .where(INQUIRY.DELETED_AT.isNull) // Soft delete 필터
                .`when`(form.category?.isNotBlank() == true, INQUIRY.CATEGORY.like("%${form.category}%"))
                .`when`(form.companyName?.isNotBlank() == true, INQUIRY.COMPANY_NAME.like("%${form.companyName}%"))
                .`when`(form.manager?.isNotBlank() == true, INQUIRY.MANAGER.like("%${form.manager}%"))
                .`when`(form.tel?.isNotBlank() == true, INQUIRY.TEL.like("%${form.tel}%"))
                .`when`(form.email?.isNotBlank() == true, INQUIRY.EMAIL.like("%${form.email}%"))
                .`when`(form.content?.isNotBlank() == true, INQUIRY.CONTENT.like("%${form.content}%"))
                .`when`(form.language?.isNotBlank() == true, INQUIRY.LANGUAGE.like("%${form.language}%"))
    }

    // ✅ 단건 조회
    fun getInquiryOne(form: InquirySearchForm): List<InquiryList> {
        return getInquiryQuery(form)
                .limit(1)
                .fetch { it.into(InquiryList::class.java) }
    }

    // ✅ 리스트 조회 (페이징 포함)
    fun getInquiryList(form: InquirySearchForm): ListPagination<InquiryList> {
        val query = getInquiryQuery(form)
        return ListPagination.of(dsl, query, form) { record ->
            record.into(InquiryList::class.java)
        }
    }
}
