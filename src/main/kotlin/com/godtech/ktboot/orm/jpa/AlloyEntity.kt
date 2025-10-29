package com.godtech.ktboot.orm.jpa

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDateTime


@Entity
@Table(name = "alloy", schema = "godtech", catalog = "")
data class AlloyEntity(

        @Id
        @Column(name = "idx")
        @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 ID 생성 설정
        var idx: Int? = null,

        @Column(name = "language", nullable = true, length = 10)
        var language: String? = null,

        @Column(name = "category", nullable = true, length = 50)
        var category: String,

        @Column(name = "type", nullable = true, length = 255)
        var type: String? = null,

        @Column(name = "title", nullable = true, length = 255)
        var title: String? = null,

        @Column(name = "subtitle", nullable = true, columnDefinition = "LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
        @Lob
        var subtitle: String? = null,

        @Column(name = "thumbnail", nullable = true)
        var thumbnail: String? = null,

        @Column(name = "content", nullable = true, columnDefinition = "LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
        @Lob
        var content: String? = null,


        @Column(name = "created_at", nullable = true, length = 50)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        var createdAt: LocalDateTime? = null,

        @Column(name = "updated_at", nullable = true, length = 50)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        var updatedAt: LocalDateTime? = null,

        @Column(name = "deleted_at", nullable = true, length = 50)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        var deletedAt: LocalDateTime? = null,

        ){
}

