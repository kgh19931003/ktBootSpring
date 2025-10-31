package com.portfolio.ktboot.orm.jpa

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime


@Entity
@Table(name = "product_file", schema = "portfolio", catalog = "")
data class ProductFileEntity (

    @Id
    @Column(name = "idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 ID 생성 설정
    var idx: Int? = null,

    @Column(name = "language", nullable = true, length = 10)
    var language: String? = null,

    @Column(name = "parent_idx", nullable = true)
    var parentIdx: Int? = null,

    @Column(name = "origin_name", nullable = true)
    var originName: String? = null,

    @Column(name = "name", nullable = true)
    var name: String? = null,

    @Column(name = "dir", nullable = true)
    var dir: String? = null,

    @Column(name = "src", nullable = true)
    var src: String? = null,

    @Column(name = "size", nullable = true)
    var size: Double? = null,

    @Column(name = "content_type", nullable = true, length = 50)
    var contentType: String? = null,

    @Column(name = "`order`", nullable = true)
    var order: Int? = null,

    @Column(name = "uuid", nullable = true, length = 255)
    var uuid: String? = null,

    @Column(name = "created_at", nullable = true, length = 50)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    var createdAt: LocalDateTime? = null,

    @Column(name = "updated_at", nullable = true, length = 50)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    var updatedAt: LocalDateTime? = null,

    @Column(name = "deleted_at", nullable = true, length = 50)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    var deletedAt: LocalDateTime? = null


)
{}

