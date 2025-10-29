package com.godtech.ktboot.orm.jpa

import jakarta.persistence.*

@Entity
@Table(name = "image_integrate", schema = "godtech", catalog = "")
data class ImageIntegrateEntity(
        @Id
        @Column(name = "idx")
        @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 ID 생성 설정
        val idx: Int = 0,

        @Column(name = "ref_id", nullable = true)
        val refId: Int,          // row id

        @Column(name = "ref_table", nullable = true)
        val refTable: String,     // table name

        @Column(name = "url", nullable = true)
        val url: String,

        @Column(name = "s3_key", nullable = true)
        val s3Key: String,

        @Column(name = "bucket", nullable = true)
        val bucket: String,

        @Column(name = "status", nullable = true)
        var status: String = "PENDING"
)
