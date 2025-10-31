package com.portfolio.ktboot.controller.admin


import com.portfolio.ktboot.controller.UploadController
import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.orm.jpa.*
import com.portfolio.ktboot.service.PolicyService
import jakarta.servlet.http.HttpServletRequest
import jakarta.transaction.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@RestController
@RequestMapping("/admin/policy") // API 요청을 위한 기본 경로
class AdminPolicyController (
        private val policyService: PolicyService,
        private val policyRepository: PolicyRepository,
        private val uploadController: UploadController
){

    @GetMapping("/one/{id}")
    fun policyOne(@PathVariable id: Int): PolicyList {
        return policyService.getPolicyOne(id)
    }

    @GetMapping("/one")
    fun policyType(@RequestParam type: String, @RequestParam language: String): PolicyList {
        return policyService.getPolicyOneType(type, language)
    }

    @GetMapping("/list")
    fun policyList(form: PolicySearchForm): ListPagination<PolicyList> {
        return policyService.getPolicyList(form)
    }

    @PostMapping("/create")
    @Transactional
    fun policyCreate(
            @RequestBody form: PolicyCreateForm
    ): Any? {
        val policyEntity = PolicyEntity(
                language = form.language,
                type = form.type,
                content = form.content,
                createdAt = LocalDateTime.now()
        )

        return try {
            policyService.save(policyEntity)
        } catch (e: Exception) {
            throw RuntimeException("파일 업로드 중 오류 발생: ${e.message}")
        }
    }

    @PutMapping("/update/{idx}")
    @Transactional
    fun policyUpdate(@PathVariable idx: Int, @RequestBody form: PolicySearchForm): PolicyEntity {
        // 직접 객체의 필드를 수정
        val inquiry = policyRepository.findByIdx(idx).copy(
                idx = idx,
                language = form.language,
                type = form.type,
                content = form.content,
                updatedAt = LocalDateTime.now()
        )
        // 수정된 객체를 저장
        return policyService.save(inquiry)
    }


    @PostMapping("/imageUpload/{idx}")
    @Transactional
    fun policyEditorImageUpload(
            @PathVariable idx: String,
            @RequestPart("file", required = false) file: MultipartFile,
            request: HttpServletRequest
    ): Any {
        return uploadController.editorImageUpload(file, arrayOf("uploads", "editor", "policy", "images"))
    }

}
