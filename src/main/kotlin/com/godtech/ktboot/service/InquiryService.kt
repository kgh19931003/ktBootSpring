package com.godtech.ktboot.service



import com.godtech.ktboot.form.*
import com.godtech.ktboot.orm.jooq.InquiryDslRepository
import com.godtech.ktboot.orm.jpa.InquiryEntity
import com.godtech.ktboot.orm.jpa.InquiryRepository
import com.godtech.ktboot.orm.jpa.MemberEntity
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service

@Service
class InquiryService(
        private val inquiryDslRepository: InquiryDslRepository,
        private val inquiryRepository: InquiryRepository
) {

    /**
     * 특정 블로그 조회
     */
    fun getInquiryOne(id: Int): InquiryList {
        return try {
            inquiryRepository.findByIdx(id).let{
                InquiryList(
                    idx = it.idx,
                    language= it.language,
                    category = it.category,
                    companyName = it.companyName,
                    manager = it.manager,
                    tel = it.tel,
                    email = it.email,
                    content = it.content,
                    imageUrl = it.imageUrl,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
        } catch (e: Exception) {
            throw Exception(e)
        }
    }


    /**
     *  블로그 조회
     */
    fun getInquiryList(form: InquirySearchForm): ListPagination<InquiryList> {
        return try {
            inquiryDslRepository.getInquiryList(form).map {
                InquiryList(
                        idx = it.idx,
                        language= it.language,
                        category = it.category,
                        companyName = it.companyName,
                        manager = it.manager,
                        tel = it.tel,
                        email = it.email,
                        content = it.content,
                        imageUrl = it.imageUrl,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt
                )
            }
        } catch (e: Exception) {
            throw Exception(e)
        }
    }


    fun save(member: InquiryEntity): InquiryEntity {
        return inquiryRepository.save(member)
    }

}