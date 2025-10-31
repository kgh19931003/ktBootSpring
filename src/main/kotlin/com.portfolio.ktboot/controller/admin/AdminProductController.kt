package com.portfolio.ktboot.controller.admin


import com.portfolio.ktboot.form.*
import com.portfolio.ktboot.model.Response
import com.portfolio.ktboot.orm.jpa.*
import com.portfolio.ktboot.proto.combine
import com.portfolio.ktboot.service.ExcelService
import com.portfolio.ktboot.service.ProductService
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
@RequestMapping("/admin/product") // API 요청을 위한 기본 경로
class AdminProductController (
        private val productService: ProductService,
        private val productRepository: ProductRepository,
        private val productFileRepository: ProductFileRepository,
        private val excelService: ExcelService
){

    @GetMapping("/one/{id}")
    fun productOne(@PathVariable id: Int): ProductList {
        return productService.getProductOne(id).let{ prdInfo ->
            var file = productService.getfileOne(id)
            prdInfo.copy(
                    fileIndex = file.map { it.idx },
                    fileOrder = file.map { it.order },
                    fileImage = file.map { it.src },
                    fileUuid = file.map { it.uuid }
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
            @RequestPart("fileImage", required = false) files: List<MultipartFile>?
    ): Any? {
        val productEntity = ProductEntity(
            language = form.language,
            name = form.name,
            price = form.price
        )

        println("ProductUpdateForm : "+form)

        return try {
            productService.save(productEntity).let {

                // 신규로 등록되는 실제 파일
                files?.filterNot { it.isEmpty }?.forEachIndexed { index, file ->
                    tikaAllowedImageFile(files!!.get(0))
                    val originalName = file.originalFilename ?: "unknown.png"
                    val extension = file.originalFilename?.substringAfterLast('.', "") ?: "png"
                    val savedName = nowAsTimestamp().combine(".$extension")
                    val root = System.getProperty("user.dir")
                    val uploadDir = Paths.get(root, "uploads", "product", "images")
                    val relativePath = uploadDir.toString().removePrefix(root).replace("\\", "/")
                    val src = relativePath.combine("/" + savedName!!)

                    // 신규 파일 저장
                    if (!Files.exists(uploadDir)) {
                        Files.createDirectories(uploadDir)
                    }

                    val targetPath = uploadDir.resolve(savedName)
                    file.transferTo(targetPath.toFile())

                    productFileRepository.save(
                            ProductFileEntity(
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
    fun productUpdate(
            @PathVariable id: Int,
            @RequestPart("form") form: ProductUpdateForm,
            @RequestPart("fileImage", required = false) files: List<MultipartFile>?
    ): Any? {
        val product = productRepository.findByIdx(id).copy(
                idx = id,
                language = form.language,
                name = form.name,
                price = form.price,
                updatedAt = LocalDateTime.now()
        )

        // 이미 등록되어있는 파일 삭제
        form.fileDeleteIndex?.forEachIndexed{ index, value ->
            val fileInfo = productFileRepository.findByIdx(value)

            val root = System.getProperty("user.dir")  // 예: /home/ubuntu/project
            val imagePath = Paths.get(root, "uploads", "product", "images", fileInfo?.name).toString()

            productFileRepository.decrementOrderGreaterThan(id ,fileInfo!!.order).let{
                deleteImageFile(imagePath).let{
                    productFileRepository.deleteByIdx(value)
                }
            }
        }

        productService.save(product).let{

            println("form.fileMultipartFileOrder : "+form.fileMultipartFileOrder)

            // 신규로 등록되는 실제 파일
            files?.filterNot { it.isEmpty }?.forEachIndexed  { index, file ->
                tikaAllowedImageFile(file)
                val originalName = file.originalFilename ?: "unknown.png"
                val extension = file.originalFilename?.substringAfterLast('.', "") ?: "png"
                val savedName = nowAsTimestamp().combine(".$extension")
                val root = System.getProperty("user.dir")
                val uploadDir = Paths.get(root, "uploads", "product", "images")
                val relativePath = uploadDir.toString().removePrefix(root).replace("\\", "/")
                val src = relativePath.combine("/"+savedName!!)
                val multipartFileOrder = form.fileMultipartFileOrder?.get(index)



                    // 신규 파일 저장
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir)
                }

                val targetPath = uploadDir.resolve(savedName)
                file.transferTo(targetPath.toFile())

                productFileRepository.save(
                        ProductFileEntity(
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

                val productInfo = productFileRepository.findByIdx(value)
                productFileRepository.save(
                        productInfo?.copy(
                                order = imageOrder
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
            Response.success("회원 삭제 성공")
        } catch (ex: Exception) {
            Response.fail("회원 삭제 실패: ${ex.message}")
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
