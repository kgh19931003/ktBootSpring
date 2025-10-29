package com.godtech.ktboot.orm.jooq


import com.godtech.ktboot.form.*
import com.godtech.ktboot.jooq.godtech.tables.references.BLOG
import com.godtech.ktboot.jooq.godtech.tables.references.MEMBER
import com.godtech.ktboot.jooq.godtech.tables.references.PRODUCT
import com.godtech.ktboot.proto.isNotNull
import com.godtech.ktboot.proto.`when`
import org.jooq.*
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime

@Repository
class ProductDslRepository(
    private val dsl: DSLContext,
) {
    fun getProductQuery(form: ProductSearchForm): SelectConditionStep<Record6<Int?, String?, String?, Int?, LocalDateTime?, LocalDateTime?>> {
        return dsl.select(
                PRODUCT.IDX.`as`("idx"),
                PRODUCT.LANGUAGE.`as`("language"),
                PRODUCT.NAME.`as`("name"),
                PRODUCT.PRICE.`as`("price"),
                PRODUCT.CREATED_AT.`as`("createdAt"),
                PRODUCT.UPDATED_AT.`as`("updatedAt")
            )
            .from(PRODUCT)
            .where(DSL.noCondition())
            .`when`(form.idx.isNotNull(), PRODUCT.IDX.eq(form.idx))
            .`when`(form.name?.isNotBlank() == true, PRODUCT.NAME.like("%${form.name}%"))
            .`when`(form.price.isNotNull(), PRODUCT.PRICE.eq(form.price))
            .`when`(form.language?.isNotBlank() == true, PRODUCT.LANGUAGE.like("%${form.language}%"))
    }


    fun getProductOne(form: ProductSearchForm): List<ProductList> {
        return getProductQuery(form).fetch { it.into(ProductList::class.java) }
    }

    fun getProductList(form: ProductSearchForm): ListPagination<ProductList> {
        val query = getProductQuery(form)
        return ListPagination.of(dsl, query, form) { record ->
            record.into(ProductList::class.java)
        }
    }

}