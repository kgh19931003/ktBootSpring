package com.portfolio.ktboot.service



import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.orm.jooq.BlogDslRepository
import com.portfolio.ktboot.orm.jpa.BlogEntity
import com.portfolio.ktboot.orm.jpa.BlogRepository
import com.portfolio.ktboot.orm.jpa.MemberEntity
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service

@Service
class BlogService(
        private val blogDslRepository: BlogDslRepository,
        private val blogRepository: BlogRepository
) {

    /**
     * 특정 블로그 조회
     */
    fun getBlogOne(id: Int): BlogDetail {
        return try {
            blogRepository.findByIdx(id).let { blog ->

                val randomBlog = blogDslRepository.getRandomBlogs(blog.language).map { item ->
                    val firstImgTag = Regex("<img[^>]*>").find(item.content ?: "")?.value
                    val thumbnailSrc = firstImgTag?.let {
                        Regex("""src\s*=\s*["']([^"']+)["']""").find(it)?.groups?.get(1)?.value
                    }

                    // content 내 모든 <img> 태그 제거
                    val contentWithoutImg = item.content?.replace(Regex("<img[^>]*>"), "")

                    // content 내 모든 HTML 태그 제거
                    val contentWithoutHtml = contentWithoutImg!!
                            .replace(Regex("<[^>]*>"), "")  // 모든 HTML 태그 제거
                            .replace(Regex("&nbsp;"), " ")  // HTML 엔티티 &nbsp; 를 공백으로 변환
                            .replace(Regex("&amp;"), "&")   // &amp; 를 & 로 변환
                            .replace(Regex("&lt;"), "<")    // &lt; 를 < 로 변환
                            .replace(Regex("&gt;"), ">")    // &gt; 를 > 로 변환
                            .replace(Regex("&quot;"), "\"") // &quot; 를 " 로 변환
                            .replace(Regex("\\s+"), " ")    // 연속된 공백을 하나로 변환
                            .trim()

                    // BlogList 또는 BlogDetailSummary에 thumbnail 필드가 있다고 가정
                    item.copy(thumbnail = thumbnailSrc, content = contentWithoutHtml)
                }

                BlogDetail(
                        idx = blog.idx,
                        language= blog.language,
                        sourceOrgan= blog.sourceOrgan,
                        title = blog.title,
                        subtitle = blog.subtitle,
                        content = blog.content,
                        category = blog.category,
                        regDate = blog.regDate,
                        createdAt = blog.createdAt,
                        updatedAt = blog.updatedAt,
                        randomBlog = randomBlog
                )
            }
        } catch (e: Exception) {
            throw Exception(e)
        }
    }

    /**
     *  블로그 조회
     */
    fun getBlogList(form: BlogSearchForm): ListPagination<BlogList> {
        return try {
            blogDslRepository.getBlogList(form).map {
                val content = it.content ?: ""

                // 첫 번째 <img> 태그 전체 추출
                val firstImgTag = Regex("<img[^>]*>").find(content)?.value

                // 첫 번째 이미지 태그에서 src 속성 값만 추출
                val thumbnailSrc = firstImgTag?.let { imgTag ->
                    Regex("""src\s*=\s*["']([^"']+)["']""").find(imgTag)?.groups?.get(1)?.value
                }

                // content 내 모든 HTML 태그 제거
                val contentWithoutHtml = content
                        .replace(Regex("<[^>]*>"), "")  // 모든 HTML 태그 제거
                        .replace(Regex("&nbsp;"), " ")  // HTML 엔티티 &nbsp; 를 공백으로 변환
                        .replace(Regex("&amp;"), "&")   // &amp; 를 & 로 변환
                        .replace(Regex("&lt;"), "<")    // &lt; 를 < 로 변환
                        .replace(Regex("&gt;"), ">")    // &gt; 를 > 로 변환
                        .replace(Regex("&quot;"), "\"") // &quot; 를 " 로 변환
                        .replace(Regex("\\s+"), " ")    // 연속된 공백을 하나로 변환
                        .trim()                         // 앞뒤 공백 제거

                BlogList(
                        idx = it.idx,
                        language= it.language,
                        sourceOrgan= it.sourceOrgan,
                        title = it.title,
                        subtitle = it.subtitle,
                        category = it.category,
                        regDate = it.regDate,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt,
                        content = contentWithoutHtml,  // 모든 HTML 태그 제거한 내용
                        thumbnail = thumbnailSrc  // 첫 번째 이미지 태그 넣기
                )
            }
        } catch (e: Exception) {
            throw Exception(e)
        }
    }


    fun save(member: BlogEntity): BlogEntity {
        return blogRepository.save(member)
    }

}