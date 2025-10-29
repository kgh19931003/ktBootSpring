package com.godtech.ktboot.controller.admin


import com.godtech.ktboot.controller.UploadController
import com.godtech.ktboot.form.*
import com.godtech.ktboot.model.Response
import com.godtech.ktboot.orm.jpa.*
import com.godtech.ktboot.service.BlogService
import com.godtech.ktboot.service.ExcelService
import jakarta.servlet.http.HttpServletRequest
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import org.springframework.core.env.Environment

@RestController
@RequestMapping("/admin/blog") // API 요청을 위한 기본 경로11
class AdminBlogController (
        private val blogService: BlogService,
        private val blogRepository: BlogRepository,
        private val excelService: ExcelService,
        private val uploadController: UploadController
){

    @Autowired
    private lateinit var env: Environment

    @GetMapping("/one/{id}")
    fun blogOne(@PathVariable id: Int): BlogDetail {
        return blogService.getBlogOne(id)
    }


    @GetMapping("/list")
    fun blogList(form: BlogSearchForm): ListPagination<BlogList> {
        return blogService.getBlogList(form)
    }

    @PostMapping("/create")
    @Transactional
    fun blogCreate(@RequestBody form: BlogCreateForm): BlogEntity {
        val blogEntity = BlogEntity(
            language = form.language,
            sourceOrgan = form.sourceOrgan,
            title = form.title,
            subtitle = form.subtitle,
            content = form.content,
            category = form.category,
            regDate = form.regDate,
            createdAt = LocalDateTime.now()
        )

        return blogService.save(blogEntity)
    }

    @PutMapping("/update/{idx}")
    @Transactional
    fun blogUpdate(@PathVariable idx: Int, @RequestBody form: BlogSearchForm): BlogEntity {
        // 직접 객체의 필드를 수정
        val blog = blogRepository.findByIdx(idx).copy(
            idx = idx,
            language = form.language,
            sourceOrgan = form.sourceOrgan,
            title = form.title,
            subtitle = form.subtitle,
            category = form.category,
            regDate = form.regDate,
            content = form.content,
            updatedAt = LocalDateTime.now()
        )
        // 수정된 객체를 저장
        return blogService.save(blog)
    }

    @DeleteMapping("/delete/{idx}")
    @Transactional
    fun blogDelete(@PathVariable idx: Int): Response<String> {
        return try {
            blogRepository.deleteByIdx(idx)
            Response.success("회원 삭제 성공")
        } catch (ex: Exception) {
            Response.fail("회원 삭제 실패: ${ex.message}")
        }
    }


    @PostMapping("/imageUpload/{idx}")
    @Transactional
    fun blogEditorImageUpload(
        @PathVariable idx: String,
        @RequestPart("file", required = false) file: MultipartFile,
        request: HttpServletRequest
    ): Any {
        return uploadController.editorImageUpload(file, arrayOf("uploads", "editor", "blog", "images"))
    }


    /*
    @GetMapping("/excel")
    fun downloadUserListExcel(
            @ModelAttribute form: BlogSearchForm,
            response: HttpServletResponse
    ) {
        excelService.blogExcelDownload(blogService.getBlogList(form), response, "회원목록")
    }
     */
}
