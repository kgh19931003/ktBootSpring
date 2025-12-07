package com.portfolio.ktboot.orm.jooq


import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.proto.isNotNull
import com.portfolio.ktboot.proto.`when`
import com.portfolio.ktboot.form.ListPagination
import com.portfolio.ktboot.jooq.portfolio.tables.references.FILE_UPLOAD
import com.portfolio.ktboot.proto.isNotNull
import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class FileUploadDslRepository(
    private val dsl: DSLContext,
) {
    fun getFileUploadQuery(form: FileUploadSearchForm): SelectConditionStep<Record5<Int?, String?, String?, LocalDateTime?, LocalDateTime?>> {
        return dsl.select(
                FILE_UPLOAD.IDX.`as`("idx"),
                FILE_UPLOAD.LANGUAGE.`as`("language"),
                FILE_UPLOAD.CATEGORY.`as`("category"),
                FILE_UPLOAD.CREATED_AT.`as`("createdAt"),
                FILE_UPLOAD.UPDATED_AT.`as`("updatedAt")
            )
            .from(FILE_UPLOAD)
            .where(DSL.noCondition())
            .`when`(form.idx.isNotNull(), FILE_UPLOAD.IDX.eq(form.idx))
            .`when`(form.language?.isNotBlank() == true, FILE_UPLOAD.LANGUAGE.like("%${form.language}%"))
            .`when`(form.category?.isNotBlank() == true, FILE_UPLOAD.CATEGORY.like("%${form.category}%"))
    }


    fun getFileUploadOne(form: FileUploadSearchForm): List<FileUploadList> {
        return getFileUploadQuery(form).fetch { it.into(FileUploadList::class.java) }
    }

    fun getFileUploadList(form: FileUploadSearchForm): ListPagination<FileUploadList> {
        val query = getFileUploadQuery(form)
        return ListPagination.of(dsl, query, form) { record ->
            record.into(FileUploadList::class.java)
        }
    }

}