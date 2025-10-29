package com.godtech.ktboot.orm.jooq


import com.godtech.ktboot.form.ListPagination
import com.godtech.ktboot.form.MemberList
import com.godtech.ktboot.form.MemberSearchForm
import com.godtech.ktboot.jooq.godtech.tables.references.BLOG
import com.godtech.ktboot.jooq.godtech.tables.references.INQUIRY
import com.godtech.ktboot.jooq.godtech.tables.references.MEMBER
import com.godtech.ktboot.proto.isNotNull
import com.godtech.ktboot.proto.`when`
import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class MemberDslRepository(
    private val dsl: DSLContext,
) {
    fun getMemberQuery(form: MemberSearchForm): SelectConditionStep<Record7<Int?, String?, String?, String?, String?, LocalDateTime?, LocalDateTime?>> {
        return dsl.select(
                MEMBER.IDX.`as`("idx"),
                MEMBER.LANGUAGE.`as`("language"),
                MEMBER.ID.`as`("id"),
                MEMBER.NAME.`as`("name"),
                MEMBER.GENDER.`as`("gender"),
                MEMBER.CREATED_AT.`as`("createdAt"),
                MEMBER.UPDATED_AT.`as`("updatedAt")
        )
                .from(MEMBER)
                .where(DSL.noCondition())
                .`when`(form.id.isNotNull(), MEMBER.ID.eq(form.id))
                .`when`(form.name.isNotNull(), MEMBER.NAME.eq(form.name))
                .`when`(form.gender.isNotNull(), MEMBER.GENDER.eq(form.gender))
                .`when`(form.language?.isNotBlank() == true, MEMBER.LANGUAGE.like("%${form.language}%"))
    }


    fun getMemberOne(form: MemberSearchForm): List<MemberList> {
        return getMemberQuery(form).fetch { it.into(MemberList::class.java) }
    }

    fun getMemberList(form: MemberSearchForm): ListPagination<MemberList> {
        val query = getMemberQuery(form)
        return ListPagination.of(dsl, query, form) { record ->
            record.into(MemberList::class.java)
        }
    }

}