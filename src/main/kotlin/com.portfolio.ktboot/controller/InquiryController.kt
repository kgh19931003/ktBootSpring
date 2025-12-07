package com.portfolio.ktboot.controller


import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.orm.jpa.*
import com.portfolio.ktboot.orm.jpa.entity.InquiryEntity
import com.portfolio.ktboot.orm.jpa.repository.InquiryRepository
import com.portfolio.ktboot.proto.combine
import com.portfolio.ktboot.service.InquiryService
import com.portfolio.ktboot.service.MemberService
import com.portfolio.ktboot.utils.JwtTokenProvider
import isAllowedExtension
import jakarta.transaction.Transactional
import nowAsRegularFormat
import nowAsTimestamp
import nowAsYYMMDDFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import tikaAllowedImageFile
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/inquiry") // API 요청을 위한 기본 경로
class InquiryController (
    private val inquiryService: InquiryService,
    private val inquiryRepository: InquiryRepository,
    private val uploadController: UploadController
){

    @GetMapping("/one/{id}")
    fun inquiryOne(@PathVariable id: Int): InquiryList {
        return inquiryService.getInquiryOne(id)
    }


    @GetMapping("/list")
    fun inquiryList(form: InquirySearchForm): ListPagination<InquiryList> {
        return inquiryService.getInquiryList(form)
    }

    @PostMapping("/create")
    @Transactional
    fun inquiryCreate(
            @RequestPart("form") form: InquiryCreateForm,
            @RequestPart("file", required = false) files: List<MultipartFile>?
    ): Any? {
        val inquiryEntity = InquiryEntity(
                language = form.language,
                category = form.category,
                companyName = form.companyName,
                manager = form.manager,
                tel = form.tel,
                email = form.email,
                content = form.content,
                createdAt = LocalDateTime.now()
        )

        return try {
            // 1차 저장 → idx 확보
            val savedEntity = inquiryService.save(inquiryEntity)

            // 파일 저장 처리
            files?.filterNot { it.isEmpty }?.forEachIndexed { index, file ->
                val uploaded = uploadController.imageUpload(file, arrayOf("uploads", "performance", "images"))
                // imageUrl 을 저장 (여러 개 저장할 거면 리스트로 저장하도록 변경 필요)
                savedEntity.imageUrl = uploaded["src"]
            }

            // imageUrl 이 설정되었을 경우 다시 저장
            inquiryService.save(savedEntity)
        } catch (e: Exception) {
            throw RuntimeException("파일 업로드 중 오류 발생: ${e.message}")
        }
    }

    @PutMapping("/update/{idx}")
    @Transactional
    fun inquiryUpdate(@PathVariable idx: Int, @RequestBody form: InquirySearchForm): InquiryEntity {
        // 직접 객체의 필드를 수정
        val inquiry = inquiryRepository.findByIdx(idx).copy(
                idx = idx,
                language = form.language,
                category = form.category,
                companyName = form.companyName,
                manager = form.manager,
                tel = form.tel,
                email = form.email,
                content = form.content,
                imageUrl = form.imageUrl,
                updatedAt = LocalDateTime.now()
        )
        // 수정된 객체를 저장
        return inquiryService.save(inquiry)
    }


}
