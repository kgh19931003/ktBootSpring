package com.portfolio.ktboot.service

import com.portfolio.ktboot.orm.jpa.ImageIntegrateRepository
import extarctS3Path
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Value
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Service
class DynamicImageCleanupService(
        private val jdbcTemplate: JdbcTemplate,
        private val imageRepository: ImageIntegrateRepository,
        @Value("\${aws.s3.bucket}") private val bucketName: String,
        @Value("\${aws.s3.region}") private val region: String,
        @Value("\${aws.s3.access-key}") private val accessKey: String,
        @Value("\${aws.s3.secret-key}") private val secretKey: String
) {

    private val s3: S3Client = S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(
                    StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKey, secretKey)
                    )
            )
            .build()

    @Transactional
    fun cleanupUnusedImages(
            tableName: String,
            idColumn: String,
            contentColumn: String,
            rowId: Int
    ) {
        // 1. content 조회
        val sql = "SELECT $contentColumn FROM $tableName WHERE $idColumn = ?"
        val htmlContent: String? = jdbcTemplate.queryForObject(sql, String::class.java, rowId)
        if (htmlContent.isNullOrEmpty()) return

        // 2. content에서 이미지 URL 추출
        val usedUrls = extractImageUrls(htmlContent)

        // 3. 업로드된 이미지 DB에서 조회
        val savedImages = imageRepository.findByRefIdAndRefTable(rowId, tableName)

        println("usedUrlsusedUrls : "+usedUrls)
        println("savedImagessavedImages : "+savedImages)

        // 4. 사용되지 않는 이미지 삭제
        savedImages.forEach { img ->
            if (!usedUrls.contains(img.url)) {
                // S3 삭제
                s3.deleteObject { b ->
                    b.bucket(img.bucket)
                            .key(img.s3Key)
                }

                // DB 상태 업데이트
                img.status = "DELETED"
                imageRepository.save(img)
            }
        }
    }

    private fun extractImageUrls(htmlContent: String): List<String> {
        val doc = Jsoup.parse(htmlContent)
        return doc.select("img").map { extarctS3Path(it.attr("src")) }
    }
}
