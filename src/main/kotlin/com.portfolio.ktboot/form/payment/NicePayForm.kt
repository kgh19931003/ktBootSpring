package com.portfolio.ktboot.form.payment

data class PaymentRequest(
        val amount: Int? = 0,
        val orderId: String? = null,
        val goodsName: String? = null,
        val buyerName: String? = null,
        val returnUrl: String? = null
)

data class PaymentResponse(
        val success: Boolean,
        val message: String? = null,
        val transactionId: String? = null,
        val paymentUrl: String? = null
)