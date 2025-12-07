package com.portfolio.ktboot.service

import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.orm.jooq.ProductDslRepository
import com.portfolio.ktboot.orm.jpa.entity.ProductEntity
import com.portfolio.ktboot.orm.jpa.entity.ProductFileEntity
import com.portfolio.ktboot.orm.jpa.repository.ProductFileRepository
import com.portfolio.ktboot.orm.jpa.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productDslRepository: ProductDslRepository,
    private val productRepository: ProductRepository,
    private val productImgRepository: ProductFileRepository
) {

    /**
     * 특정 상품정보 조회
     */
    fun getProductOne(id: Int): ProductList {
        return try {
            productRepository.findByIdx(id).let{
                ProductList(
                    idx = it.idx,
                    language= it.language,
                    name = it.name,
                    price = it.price,
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
    fun getfileOne(prdIdx: Int): List<ProductFileEntity> {
        return try {
            productImgRepository.findByParentIdxOrderByOrderAsc(prdIdx)
        } catch (e: Exception) {
            throw Exception(e)
        }
    }

    /**
     * 특정 상품정보 리스트 조회
     */
    fun getProductList(form: ProductSearchForm): ListPagination<ProductList> {
        return try {
            productDslRepository.getProductList(form).map{
                ProductList(
                    idx = it.idx,
                    language= it.language,
                    name = it.name,
                    price = it.price,
                    createdAt = it.createdAt
                )
            }
        } catch (e: Exception) {
            throw Exception(e)
        }
    }



    fun save(product: ProductEntity): ProductEntity {
        return productRepository.save(product)
    }

}