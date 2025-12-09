package com.portfolio.ktboot.controller


import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.model.Response
import com.portfolio.ktboot.orm.jpa.entity.PostEntity
import com.portfolio.ktboot.orm.jpa.entity.PostFileEntity
import com.portfolio.ktboot.orm.jpa.repository.PostFileRepository
import com.portfolio.ktboot.orm.jpa.repository.PostRepository
import com.portfolio.ktboot.proto.combine
import com.portfolio.ktboot.service.ExcelService
import com.portfolio.ktboot.service.PostService
import deleteImageFile
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import nowAsTimestamp
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import tikaAllowedImageFile
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime

@RestController
@RequestMapping("/post") // API 요청을 위한 기본 경로
class PostController (
        private val postService: PostService,
        private val postRepository: PostRepository,
        private val postFileRepository: PostFileRepository,
        private val excelService: ExcelService
){

    @GetMapping("/one/{id}")
    fun postOne(@PathVariable id: Int): PostList {
        return postService.getPostOne(id).let{ prdInfo ->
            var file = postService.getfileOne(id)
            prdInfo.copy(
                    fileIndex = file.map { it.idx },
                    fileOrder = file.map { it.order },
                    fileImage = file.map { it.src },
                    fileUuid = file.map { it.uuid }
            )
        }
    }

    @GetMapping("/image-one/{prdIdx}")
    fun fileOne(@PathVariable prdIdx: Int): List<PostFileEntity> {
        return postService.getfileOne(prdIdx)
    }


    @GetMapping("/list")
    fun postList(form: PostSearchForm): ListPagination<PostList> {
        return postService.getPostList(form)
    }

    @PostMapping("/create")
    @Transactional
    fun postCreate(
            @RequestPart("form") form: PostUpdateForm,
            @RequestPart("fileImage", required = false) files: List<MultipartFile>?
    ): Any? {
        val postEntity = PostEntity(
            language = form.language,
            category = form.category,
            title = form.title,
            subtitle = form.subtitle,
            content = form.content,
        )

        println("PostUpdateForm : "+form)

        return try {
            postService.save(postEntity).let {

                // 신규로 등록되는 실제 파일
                files?.filterNot { it.isEmpty }?.forEachIndexed { index, file ->
                    tikaAllowedImageFile(files!!.get(0))
                    val originalName = file.originalFilename ?: "unknown.png"
                    val extension = file.originalFilename?.substringAfterLast('.', "") ?: "png"
                    val savedName = nowAsTimestamp().combine(".$extension")
                    val root = System.getProperty("user.dir")
                    val uploadDir = Paths.get(root, "uploads", "post", "images")
                    val relativePath = uploadDir.toString().removePrefix(root).replace("\\", "/")
                    val src = relativePath.combine("/" + savedName!!)

                    // 신규 파일 저장
                    if (!Files.exists(uploadDir)) {
                        Files.createDirectories(uploadDir)
                    }

                    val targetPath = uploadDir.resolve(savedName)
                    file.transferTo(targetPath.toFile())

                    postFileRepository.save(
                            PostFileEntity(
                                    language = form.language,
                                    parentIdx = it.idx,
                                    originName = originalName,
                                    name = savedName,
                                    dir = relativePath,
                                    src = src,
                                    contentType = file.contentType?.substringAfter("/") ?: extension,
                                    createdAt = LocalDateTime.now()
                            )
                    )

                }

            }
        }
        catch (e: Exception){
            throw RuntimeException("파일 업로드 중 오류 발생")
            //Response.fail("상품 저장실패", e.message)
        }
    }

    @PostMapping("/update/{id}")
    @Transactional
    fun postUpdate(
            @PathVariable id: Int,
            @RequestPart("form") form: PostUpdateForm,
            @RequestPart("fileImage", required = false) files: List<MultipartFile>?
    ): Any? {
        val post = postRepository.findByIdx(id).copy(
                idx = id,
                language = form.language,
                category = form.category,
                title = form.title,
                subtitle = form.subtitle,
                content = form.content,
                updatedAt = LocalDateTime.now()
        )

        // 이미 등록되어있는 파일 삭제
        form.fileDeleteIndex?.forEachIndexed{ index, value ->
            val fileInfo = postFileRepository.findByIdx(value)

            val root = System.getProperty("user.dir")  // 예: /home/ubuntu/project
            val imagePath = Paths.get(root, "uploads", "post", "images", fileInfo?.name).toString()

            postFileRepository.decrementOrderGreaterThan(id ,fileInfo!!.order).let{
                deleteImageFile(imagePath).let{
                    postFileRepository.deleteByIdx(value)
                }
            }
        }

        postService.save(post).let{

            println("form.fileMultipartFileOrder : "+form.fileMultipartFileOrder)

            // 신규로 등록되는 실제 파일
            files?.filterNot { it.isEmpty }?.forEachIndexed  { index, file ->
                tikaAllowedImageFile(file)
                val originalName = file.originalFilename ?: "unknown.png"
                val extension = file.originalFilename?.substringAfterLast('.', "") ?: "png"
                val savedName = nowAsTimestamp().combine(".$extension")
                val root = System.getProperty("user.dir")
                val uploadDir = Paths.get(root, "uploads", "post", "images")
                val relativePath = uploadDir.toString().removePrefix(root).replace("\\", "/")
                val src = relativePath.combine("/"+savedName!!)
                val multipartFileOrder = form.fileMultipartFileOrder?.get(index)



                    // 신규 파일 저장
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir)
                }

                val targetPath = uploadDir.resolve(savedName)
                file.transferTo(targetPath.toFile())

                postFileRepository.save(
                        PostFileEntity(
                                language = form.language,
                                parentIdx = id,
                                originName = originalName,
                                name = savedName,
                                dir = relativePath,
                                src = src,
                                order = multipartFileOrder,
                                contentType = file.contentType?.substringAfter("/") ?: extension,
                                createdAt = LocalDateTime.now()
                        )
                )

            }


            // 이미 등록되어있는 파일 정렬
            form.fileIndex?.forEachIndexed{ index, value ->
                val imageIndex = index
                val imageOrder = form.fileOrder?.get(imageIndex)

                println("imageIndex : $imageIndex , imageOrder : $imageOrder")

                val postInfo = postFileRepository.findByIdx(value)
                postFileRepository.save(
                        postInfo?.copy(
                                order = imageOrder
                        )
                )
            }

        }

        return postService.save(post)
    }

    @DeleteMapping("/delete/{id}")
    @Transactional
    fun postDelete(@PathVariable id: Int): Response<String> {
        return try {
            postRepository.deleteById(id)
            Response.success("회원 삭제 성공")
        } catch (ex: Exception) {
            Response.fail("회원 삭제 실패: ${ex.message}")
        }
    }



    @GetMapping("/excel")
    fun downloadPostListExcel(
            @ModelAttribute form: PostSearchForm,
            response: HttpServletResponse
    ) {
        excelService.postExcelDownload(postService.getPostList(form), response, "상품목록")
    }
}
