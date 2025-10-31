package com.portfolio.ktboot.form.payment

data class NaverPayRequest(
        val itemName: String = "",
        val amount: Int = 0,
        val orderId: String = "",
        val userId: String = ""
)
