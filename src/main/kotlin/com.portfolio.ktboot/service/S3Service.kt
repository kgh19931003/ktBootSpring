package com.portfolio.ktboot.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest
import software.amazon.awssdk.services.s3.model.ObjectIdentifier
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File
import java.net.URL
import java.nio.file.Paths

@Service
class S3Service(
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

    fun uploadFile(file: File, key: String): URL {
        val request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                //.acl("public-read")
                .build()

        s3.putObject(request, Paths.get(file.absolutePath))

        return s3.utilities().getUrl { b ->
            b.bucket(bucketName).key(key)
        }
    }

    fun deleteFiles(keys: List<String>) {
        val objectsToDelete = keys.map { ObjectIdentifier.builder().key(it).build() }
        val deleteRequest = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete { d -> d.objects(objectsToDelete) }
                .build()
        s3.deleteObjects(deleteRequest)
    }
}
