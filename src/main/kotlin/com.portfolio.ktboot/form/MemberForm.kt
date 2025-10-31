package com.portfolio.ktboot.form

import java.math.BigDecimal
import java.time.LocalDateTime


data class MemberCreateForm(
    var language: String? = null,
    var id: String? = null,
    var password: String? = null,
    var name: String? = null,
    var gender: String? = null
)


data class MemberList(
    val idx: Int?,
    val id: String? = null,
    val name: String? = null,
    val gender: String? = null,
    val token: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)