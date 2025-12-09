package com.portfolio.ktboot.controller.admin


import com.portfolio.ktboot.controller.UploadController
import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.model.Response
import com.portfolio.ktboot.orm.jpa.entity.PostEntity
import com.portfolio.ktboot.orm.jpa.entity.PostFileEntity
import com.portfolio.ktboot.orm.jpa.repository.PostFileRepository
import com.portfolio.ktboot.orm.jpa.repository.PostRepository
import com.portfolio.ktboot.proto.combine
import com.portfolio.ktboot.service.DynamicImageCleanupService
import com.portfolio.ktboot.service.ExcelService
import com.portfolio.ktboot.service.PostService
import extarctS3Path
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@RestController
@RequestMapping("/admin/post") // API 요청을 위한 기본 경로
class AdminPostController (
    private val postService: PostService,
    private val postRepository: PostRepository,
    private val postFileRepository: PostFileRepository,
    private val excelService: ExcelService,
    private val uploadController: UploadController,
    private val dynamicImageCleanupService: DynamicImageCleanupService
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
            @RequestPart("form") form: PostCreateForm,
            @RequestPart("fileImage", required = false) files: List<MultipartFile>?
    ): Any? {
        println("files = ${files?.map { it.originalFilename }}")
        val postEntity = PostEntity(
                language = form.language,
                category = form.category,
                title = form.title,
                subtitle = form.subtitle,
                content = form.content,
                createdAt = LocalDateTime.now()
        )

        //println("PostUpdateForm : "+form)

        return try {
            postService.save(postEntity).let {

                // 신규로 등록되는 실제 파일
                files?.filterNot { it.isEmpty }?.forEachIndexed { index, file ->

                    val uploaded = uploadController.imageUpload(file, arrayOf("uploads", "post", "images"))

                    postFileRepository.save(
                            PostFileEntity(
                                    language = form.language,
                                    parentIdx = it.idx,
                                    originName = uploaded["originalName"],
                                    name = uploaded["savedName"],
                                    dir = uploaded["relativePath"],
                                    src = uploaded["src"],
                                    size = uploaded["size"]?.toDouble(),
                                    contentType = file.contentType?.substringAfter("/") ?: uploaded["extension"],
                                    createdAt = LocalDateTime.now()
                            )
                    )
                }


                /*
                dynamicImageCleanupService.cleanupUnusedImages(
                        tableName = "post",
                        idColumn = "idx",
                        contentColumn = "content",
                        rowId = it.idx!!
                )
                */

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
            val file = postFileRepository.findByIdx(value)

            val dir = file!!.dir
            val name = file.name
            val path = dir?.combine("/" +name)!!

            postFileRepository.decrementOrderGreaterThan(id , file.order).let{
                uploadController.deleteFile(path, false).let{
                    postFileRepository.deleteByIdx(value)
                }
            }


        }

        postService.save(post).let{

            println("form.fileMultipartFileOrder : "+form.fileMultipartFileOrder)

            // 신규로 등록되는 실제 파일
            files?.filterNot { it.isEmpty }?.forEachIndexed  { index, file ->

                val uploaded = uploadController.imageUpload(file, arrayOf("uploads", "post", "images"))
                val multipartFileOrder = form.fileMultipartFileOrder?.get(index)

                postFileRepository.save(
                        PostFileEntity(
                                language = form.language,
                                parentIdx = id,
                                originName = uploaded["originalName"],
                                name = uploaded["savedName"],
                                dir = uploaded["relativePath"],
                                src = uploaded["src"],
                                size = uploaded["size"]?.toDouble(),
                                order = multipartFileOrder,
                                contentType = file.contentType?.substringAfter("/") ?: uploaded["extension"],
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
            // 1. DB에서 파일 레코드 조회
            val files = postFileRepository.findByParentIdx(id)

            // 2. 파일 삭제
            files.forEach { file ->
                try {
                    val dir = file.dir ?: return@forEach
                    val name = file.name
                    val path = dir.combine("/" +name)!!
                    uploadController.deleteFile(path, false)
                } catch (ex: Exception) {
                    println("파일 삭제 실패: ${ex.message}")
                }
            }

            postRepository.deleteById(id)
            postFileRepository.deleteByParentIdx(id)
            Response.success("회원 삭제 성공")
        } catch (ex: Exception) {
            Response.fail("회원 삭제 실패: ${ex.message}")
        }
    }


    @PostMapping("/imageUpload/{idx}")
    @Transactional
    fun postEditorImageUpload(
            @PathVariable idx: String,
            @RequestPart("file", required = false) file: MultipartFile,
            request: HttpServletRequest
    ): Any {
        return uploadController.editorImageUpload(file, arrayOf("uploads", "editor", "post", "images"))
    }

    @PostMapping("/imageDelete")
    @Transactional
    fun postEditorImageDelete(
            @RequestBody src: List<String>
    ): Any {
        println("srcsrcsrc : "+extarctS3Path(src[0]))
        return uploadController.deleteFile(extarctS3Path(src[0]), false)
    }

    @GetMapping("/excel")
    fun downloadPostListExcel(
            @ModelAttribute form: PostSearchForm,
            response: HttpServletResponse
    ) {
        println("postService.getPostList(form) : "+postService.getPostList(form))
        excelService.postExcelDownload(postService.getPostList(form), response, "게시글 목록")
    }
}
