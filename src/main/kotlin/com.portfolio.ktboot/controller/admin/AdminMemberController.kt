package com.portfolio.ktboot.controller.admin


import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.model.Response
import com.portfolio.ktboot.orm.jpa.entity.MemberEntity
import com.portfolio.ktboot.orm.jpa.repository.MemberRepository
import com.portfolio.ktboot.service.ExcelService
import com.portfolio.ktboot.service.MemberService
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import nowAsRegularFormat
import org.apache.poi.ss.formula.functions.T
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/admin/member") // API 요청을 위한 기본 경로
class AdminMemberController (
    private val memberService: MemberService,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val excelService: ExcelService
){

    @GetMapping("/one/{id}")
    fun memberOne(@PathVariable id: Int): MemberList {
        return memberService.getMemberOne(id)
    }


    @GetMapping("/list")
    fun memberList(form: MemberSearchForm): ListPagination<MemberList> {
        return memberService.getMemberList(form)
    }

    @PostMapping("/create")
    @Transactional
    fun memberCreate(@RequestBody form: MemberCreateForm): MemberEntity {
        val memberEntity = MemberEntity(
            language = form.language,
            id = form.id,
            password = passwordEncoder.encode(form.password),
            name = form.name,
            gender = form.gender,
            createdAt = LocalDateTime.now()
        )

        return memberService.save(memberEntity)
    }

    @PutMapping("/update/{id}")
    @Transactional
    fun memberUpdate(@PathVariable id: Int, @RequestBody form: MemberSearchForm): MemberEntity {
        // 직접 객체의 필드를 수정
        val member = memberRepository.findById(form.id!!).copy(
            idx = id,
            language = form.language,
            password = passwordEncoder.encode(form.password),
            name = form.name,
            gender = form.gender,
            updatedAt = LocalDateTime.now()
        )
        // 수정된 객체를 저장
        return memberService.save(member)
    }

    @DeleteMapping("/delete/{id}")
    @Transactional
    fun memberDelete(@PathVariable id: Int): Response<String> {
        return try {
            memberRepository.deleteById(id)
            Response.success("회원 삭제 성공")
        } catch (ex: Exception) {
            Response.fail("회원 삭제 실패: ${ex.message}")
        }
    }



    @GetMapping("/excel")
    fun downloadUserListExcel(
            @ModelAttribute form: MemberSearchForm,
            response: HttpServletResponse
    ) {
        excelService.memberExcelDownload(memberService.getMemberList(form), response, "회원목록")
    }
}
