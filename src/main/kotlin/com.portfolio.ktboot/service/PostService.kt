package com.portfolio.ktboot.service

import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.orm.jooq.PostDslRepository
import com.portfolio.ktboot.orm.jpa.entity.PostEntity
import com.portfolio.ktboot.orm.jpa.entity.PostFileEntity
import com.portfolio.ktboot.orm.jpa.repository.PostFileRepository
import com.portfolio.ktboot.orm.jpa.repository.PostRepository
import org.springframework.stereotype.Service

@Service
class PostService(
        private val postDslRepository: PostDslRepository,
        private val postRepository: PostRepository,
        private val postImgRepository: PostFileRepository
) {

    /**
     * 특정 상품정보 조회
     */
    fun getPostOne(id: Int): PostList {
        return try {
            postRepository.findByIdx(id).let{
                PostList(
                    idx = it.idx,
                    language= it.language,
                    category = it.category,
                    title = it.title,
                    subtitle = it.subtitle,
                    content = it.content,
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
    fun getfileOne(prdIdx: Int): List<PostFileEntity> {
        return try {
            postImgRepository.findByParentIdxOrderByOrderAsc(prdIdx)
        } catch (e: Exception) {
            throw Exception(e)
        }
    }

    /**
     * 특정 상품정보 리스트 조회
     */
    fun getPostList(form: PostSearchForm): ListPagination<PostList> {
        return try {
            postDslRepository.getPostList(form).map{
                PostList(
                    idx = it.idx,
                    language= it.language,
                    category = it.category,
                    title = it.title,
                    subtitle = it.subtitle,
                    createdAt = it.createdAt,
                    content = it.content,
                    firstSrc = it.firstSrc
                )
            }
        } catch (e: Exception) {
            throw Exception(e)
        }
    }



    fun save(post: PostEntity): PostEntity {
        return postRepository.save(post)
    }

}