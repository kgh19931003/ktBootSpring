package com.portfolio.ktboot.orm.jpa

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "inquiry", schema = "portfolio", catalog = "")
data class InquiryEntity (
    @Id
    @Column(name = "idx", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 ID 생성 설정
    var idx: Int? = null,

    @Column(name = "language", nullable = true, length = 10)
    var language: String? = null,

    @Column(name = "category", nullable = true, length = 50)
    var category: String? = null,

    @Column(name = "company_name", nullable = true, length = 255)
    var companyName: String? = null,

    @Column(name = "manager", nullable = true, length = 255)
    var manager: String? = null,

    @Column(name = "tel", nullable = true)
    var tel: String? = null,

    @Column(name = "email", nullable = true)
    var email: String? = null,

    @Column(name = "content", nullable = true, columnDefinition = "LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    @Lob
    var content: String? = null,

    @Column(name = "image_url", nullable = true)
    var imageUrl: String? = null,

    @Column(name = "private_agree", nullable = true)
    var privateAgree: String? = null,

    @Column(name = "created_at", nullable = true, length = 50)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    var createdAt: LocalDateTime? = null,

    @Column(name = "deleted_at", nullable = true, length = 50)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    var deletedAt: LocalDateTime? = null,

    @Column(name = "updated_at", nullable = true, length = 50)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    var updatedAt: LocalDateTime? = null,

    ){}