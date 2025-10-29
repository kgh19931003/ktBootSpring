package com.godtech.ktboot.controller


import com.godtech.ktboot.form.*
import com.godtech.ktboot.interceptor.SkipAuth
import com.godtech.ktboot.model.Response
import com.godtech.ktboot.orm.jpa.*
import com.godtech.ktboot.proto.combine
import com.godtech.ktboot.proto.isInt
import com.godtech.ktboot.proto.isNotNull
import com.godtech.ktboot.proto.isNull
import com.godtech.ktboot.service.BlogService
import com.godtech.ktboot.service.ExcelService
import isAllowedExtension
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import nowAsRegularFormat
import nowAsTimestamp
import nowAsYYMMDDFormat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import org.springframework.core.env.Environment
import org.springframework.http.ResponseEntity
import tikaAllowedImageFile

@RestController
@RequestMapping("/blog") // API 요청을 위한 기본 경로11
class BlogController (
        private val blogService: BlogService,
        private val blogRepository: BlogRepository,
        private val excelService: ExcelService,
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
        @RequestPart("file", required = false) files: List<MultipartFile>?,
        request: HttpServletRequest
    ): Any {
        tikaAllowedImageFile(files!!.get(0))
        val originalName = files.get(0).originalFilename ?: "unknown.png"
        val extension = files.get(0).originalFilename?.substringAfterLast('.', "") ?: "png"
        val savedName = nowAsTimestamp().combine(".$extension").toString()
        val root = System.getProperty("user.dir")
        val uploadDir = Paths.get(root, "uploads", "editor", "blog", "images", nowAsYYMMDDFormat())
        val relativePath = uploadDir.toString().removePrefix(root).replace("\\", "/")

        // 신규 파일 저장
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir)
        }
        val targetPath = uploadDir.resolve(savedName)
        files?.get(0)?.transferTo(targetPath.toFile())

        // 현재 프로필 확인
        val activeProfiles = env.activeProfiles
        val imageUrl = if (activeProfiles.contains("operation")) {
            "$relativePath/$savedName"
        } else {
            "${request.scheme}://${request.serverName}:${request.serverPort}$relativePath/$savedName"
        }

        return mapOf("url" to imageUrl)
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
