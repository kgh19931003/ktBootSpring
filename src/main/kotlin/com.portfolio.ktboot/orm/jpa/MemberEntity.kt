package com.portfolio.ktboot.orm.jpa

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "member", schema = "portfolio")
data class MemberEntity(
    @Id
    @Column(name = "idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 ID 생성 설정
    var idx: Int? = null,

    @Column(name = "language", nullable = true, length = 10)
    var language: String? = null,

    @Size(max = 50)
    @Column(name = "id", length = 50)
    var id: String? = null,

    @Size(max = 255)
    @Column(name = "password")
    var password: String? = null,

    @Size(max = 50)
    @Column(name = "name", length = 50)
    var name: String? = null,

    @Size(max = 1)
    @Column(name = "gender", length = 1)
    var gender: String? = null,

    @Size(max = 255)
    @Column(name = "profile", length = 255)
    var profile: String? = null,

    @Size(max = 30)
    @Column(name = "biz_reg_number", length = 30)
    var bizRegNumber: String? = null,

    @Size(max = 255)
    @Column(name = "biz_reg_cert", length = 255)
    var bizRegCert: String? = null,

    @Column(name = "level")
    var level: Int? = null,

    @Size(max = 50)
    @Column(name = "created_at", length = 50)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    var createdAt: LocalDateTime? = null,

    @Size(max = 50)
    @Column(name = "updated_at", length = 50)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    var updatedAt: LocalDateTime? = null,

    @Size(max = 50)
    @Column(name = "deleted_at", length = 50)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    var deletedAt: LocalDateTime? = null,

    @Size(max = 255)
    @Column(name = "access_token")
    var accessToken: String? = null,

    @Size(max = 255)
    @Column(name = "refresh_token")
    var refreshToken: String? = null

) {

}