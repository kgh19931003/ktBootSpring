package com.portfolio.ktboot.controller

import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.model.Response
import com.portfolio.ktboot.orm.jpa.*
import com.portfolio.ktboot.proto.combine
import com.portfolio.ktboot.service.ExcelService
import com.portfolio.ktboot.service.ProductService
import deleteImageFile
import isAllowedExtension
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import nowAsRegularFormat
import nowAsTimestamp
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import tikaAllowedImageFile
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/admin/product")
class AdminProductController (
        private val productService: ProductService,
        private val productRepository: ProductRepository,
        private val productFileRepository: ProductFileRepository,
        private val excelService: ExcelService,
        private val uploadController: UploadController
){

    @GetMapping("/one/{id}")
    fun productOne(@PathVariable id: Int): ProductList {
        return productService.getProductOne(id).let{ prdInfo ->
            var files = productService.getfileOne(id)

            // 이미지와 비디오 분리
            var imageFiles = files.filter { it.contentType != "video" && !it.contentType.isNullOrEmpty() && !it.contentType!!.startsWith("video/") }
            var videoFiles = files.filter { it.contentType == "video" || it.contentType?.startsWith("video/") == true }

            prdInfo.copy(
                    // 이미지
                    fileIndex = imageFiles.map { it.idx },
                    fileOrder = imageFiles.map { it.order },
                    fileImage = imageFiles.map { it.src },
                    fileUuid = imageFiles.map { it.uuid },
                    // 비디오
                    videoIndex = videoFiles.map { it.idx },
                    videoOrder = videoFiles.map { it.order },
                    fileVideo = videoFiles.map { it.src },
                    videoUuid = videoFiles.map { it.uuid }
            )
        }
    }

    @GetMapping("/image-one/{prdIdx}")
    fun fileOne(@PathVariable prdIdx: Int): List<ProductFileEntity> {
        return productService.getfileOne(prdIdx)
    }

    @GetMapping("/list")
    fun productList(form: ProductSearchForm): ListPagination<ProductList> {
        return productService.getProductList(form)
    }

    @PostMapping("/create")
    @Transactional
    fun productCreate(
            @RequestPart("form") form: ProductUpdateForm,
            @RequestPart("fileImage", required = false) files: List<MultipartFile>?,
            @RequestPart("fileVideo", required = false) videos: List<MultipartFile>?
    ): Any? {
        val productEntity = ProductEntity(
                language = form.language,
                name = form.name,
                price = form.price
        )

        println("ProductUpdateForm : "+form)

        return try {
            productService.save(productEntity).let { savedProduct ->

                // 이미지 파일 업로드
                files?.filterNot { it.isEmpty }?.forEachIndexed { index, file ->
                    val uploaded = uploadController.imageUpload(file, arrayOf("uploads", "product", "images"))

                    productFileRepository.save(
                            ProductFileEntity(
                                    language = form.language,
                                    parentIdx = savedProduct.idx,
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

                // 비디오 파일 업로드
                videos?.filterNot { it.isEmpty }?.forEachIndexed { index, video ->
                    val uploaded = uploadController.videoUpload(video, arrayOf("uploads", "product", "videos"))

                    productFileRepository.save(
                            ProductFileEntity(
                                    language = form.language,
                                    parentIdx = savedProduct.idx,
                                    originName = uploaded["originalName"],
                                    name = uploaded["savedName"],
                                    dir = uploaded["relativePath"],
                                    src = uploaded["src"],
                                    size = uploaded["size"]?.toDouble(),
                                    contentType = "video", // 비디오는 contentType을 "video"로 구분
                                    createdAt = LocalDateTime.now()
                            )
                    )
                }

                savedProduct
            }
        }
        catch (e: Exception){
            throw RuntimeException("파일 업로드 중 오류 발생: ${e.message}")
        }
    }

    @PostMapping("/update/{id}")
    @Transactional
    fun productUpdate(
            @PathVariable id: Int,
            @RequestPart("form") form: ProductUpdateForm,
            @RequestPart("fileImage", required = false) files: List<MultipartFile>?,
            @RequestPart("fileVideo", required = false) videos: List<MultipartFile>?
    ): Any? {
        val product = productRepository.findByIdx(id).copy(
                idx = id,
                language = form.language,
                name = form.name,
                price = form.price,
                updatedAt = LocalDateTime.now()
        )

        val root = System.getProperty("user.dir")

        // 이미지 파일 삭제
        form.fileDeleteIndex?.forEachIndexed{ index, value ->
            val fileInfo = productFileRepository.findByIdx(value)
            val imagePath = Paths.get("uploads", "product", "images", fileInfo?.name?.substring(0, 8), fileInfo?.name).toString()

            println("imagePath : " +imagePath)

            productFileRepository.decrementOrderGreaterThan(id, fileInfo!!.order).let{
                uploadController.deleteFile(imagePath, false).let{
                    productFileRepository.deleteByIdx(value)
                }
            }
        }

        // 비디오 파일 삭제
        form.videoDeleteIndex?.forEachIndexed{ index, value ->
            val fileInfo = productFileRepository.findByIdx(value)
            val videoPath = Paths.get("uploads", "product", "videos", fileInfo?.name?.substring(0, 8), fileInfo?.name).toString()

            productFileRepository.decrementOrderGreaterThan(id, fileInfo!!.order).let{
                uploadController.deleteFile(videoPath, true).let{
                    productFileRepository.deleteByIdx(value)
                }
            }
        }

        productService.save(product).let{

            println("form.fileMultipartFileOrder : "+form.fileMultipartFileOrder)
            println("form.videoMultipartFileOrder : "+form.videoMultipartFileOrder)

            // 이미지 파일 신규 업로드
            files?.filterNot { it.isEmpty }?.forEachIndexed { index, file ->
                val uploaded = uploadController.imageUpload(file, arrayOf("uploads", "product", "images"))
                val multipartFileOrder = form.fileMultipartFileOrder?.get(index)

                productFileRepository.save(
                        ProductFileEntity(
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

            // 비디오 파일 신규 업로드
            videos?.filterNot { it.isEmpty }?.forEachIndexed { index, video ->
                val uploaded = uploadController.videoUpload(video, arrayOf("uploads", "product", "videos"))
                val multipartFileOrder = form.videoMultipartFileOrder?.get(index)

                productFileRepository.save(
                        ProductFileEntity(
                                language = form.language,
                                parentIdx = id,
                                originName = uploaded["originalName"],
                                name = uploaded["savedName"],
                                dir = uploaded["relativePath"],
                                src = uploaded["src"],
                                size = uploaded["size"]?.toDouble(),
                                order = multipartFileOrder,
                                contentType = "video", // 비디오 구분
                                createdAt = LocalDateTime.now()
                        )
                )
            }

            // 이미지 파일 순서 정렬
            form.fileIndex?.forEachIndexed{ index, value ->
                val imageIndex = index
                val imageOrder = form.fileOrder?.get(imageIndex)

                println("imageIndex : $imageIndex , imageOrder : $imageOrder")

                val productInfo = productFileRepository.findByIdx(value)
                productFileRepository.save(
                        productInfo?.copy(
                                order = imageOrder
                        )
                )
            }

            // 비디오 파일 순서 정렬
            form.videoIndex?.forEachIndexed{ index, value ->
                val videoIndex = index
                val videoOrder = form.videoOrder?.get(videoIndex)

                println("videoIndex : $videoIndex , videoOrder : $videoOrder")

                val videoInfo = productFileRepository.findByIdx(value)
                productFileRepository.save(
                        videoInfo?.copy(
                                order = videoOrder
                        )
                )
            }
        }

        return productService.save(product)
    }

    @DeleteMapping("/delete/{id}")
    @Transactional
    fun productDelete(@PathVariable id: Int): Response<String> {
        return try {
            productRepository.deleteById(id)
            productFileRepository.deleteByParentIdx(id) // 이미지와 비디오 모두 삭제됨
            Response.success("상품 삭제 성공")
        } catch (ex: Exception) {
            Response.fail("상품 삭제 실패: ${ex.message}")
        }
    }

    @GetMapping("/excel")
    fun downloadProductListExcel(
            @ModelAttribute form: ProductSearchForm,
            response: HttpServletResponse
    ) {
        excelService.productExcelDownload(productService.getProductList(form), response, "상품목록")
    }
}