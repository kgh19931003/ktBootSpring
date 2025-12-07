package com.portfolio.ktboot.service



import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.orm.jooq.InquiryDslRepository
import com.portfolio.ktboot.orm.jooq.PolicyDslRepository
import com.portfolio.ktboot.orm.jpa.*
import com.portfolio.ktboot.orm.jpa.entity.PolicyEntity
import com.portfolio.ktboot.orm.jpa.repository.PolicyRepository
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service

@Service
class PolicyService(
        private val policyDslRepository: PolicyDslRepository,
        private val policyRepository: PolicyRepository
) {

    /**
     * 특정 폴리시 조회
     */
    fun getPolicyOne(id: Int): PolicyList {
        return try {
            policyRepository.findByIdx(id).let{
                PolicyList(
                    idx = it.idx,
                    language= it.language,
                    type = it.type,
                    content = it.content,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
        } catch (e: Exception) {
            throw Exception(e)
        }
    }


    /**
     * 특정 타입 폴리시 조회
     */
    fun getPolicyOneType(type: String, language: String): PolicyList {
        return try {
            policyRepository.findByTypeAndLanguage(type, language).let{
                PolicyList(
                        idx = it.idx,
                        language= it.language,
                        type = it.type,
                        content = it.content,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt
                )
            }
        } catch (e: Exception) {
            throw Exception(e)
        }
    }


    /**
     *  폴리시 조회
     */
    fun getPolicyList(form: PolicySearchForm): ListPagination<PolicyList> {
        return try {
            policyDslRepository.getPolicyList(form).map {
                PolicyList(
                        idx = it.idx,
                        language= it.language,
                        type = it.type,
                        content = it.content?.replace(Regex("<[^>]*>"), ""),
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt
                )
            }
        } catch (e: Exception) {
            throw Exception(e)
        }
    }


    fun save(member: PolicyEntity): PolicyEntity {
        return policyRepository.save(member)
    }

}