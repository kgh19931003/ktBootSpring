package com.godtech.ktboot.controller

import com.godtech.ktboot.form.payment.NaverPayRequest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate

@RestController
@RequestMapping("/naver-pay")
class NaverPayController(
        private val restTemplate: RestTemplate
) {

    // 결제 준비(테스트)
    @PostMapping("/ready")
    fun ready(@RequestBody req: NaverPayRequest): ResponseEntity<Map<*, *>> {
        val headers = HttpHeaders().apply {
            add("X-Naver-Client-Id", "VVsPV9wHJX0iNL4sBxlR")
            add("X-Naver-Client-Secret", "xLLgAoyWoR")
            add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
        }

        val params = LinkedMultiValueMap<String, String>().apply {
            add("productName", req.itemName)
            add("amount", req.amount.toString())
            add("orderId", req.orderId)
            add("userId", req.userId)
            add("returnUrl", "http://localhost:3000/naverpay/success")
            add("cancelUrl", "http://localhost:3000/naverpay/cancel")
            add("failUrl", "http://localhost:3000/naverpay/fail")
        }

        val httpEntity = HttpEntity(params, headers)
        val response = restTemplate.postForEntity(
                "https://sandbox-api.pay.naver.com/v1/payments/prepare", // 테스트용 Sandbox URL
                httpEntity,
                Map::class.java
        )

        return ResponseEntity.ok(response.body as Map<*, *>)
    }

    // 결제 승인
    @PostMapping("/approve")
    fun approve(@RequestParam("paymentKey") paymentKey: String, @RequestParam("orderId") orderId: String): ResponseEntity<Map<*, *>> {
        val headers = HttpHeaders().apply {
            add("X-Naver-Client-Id", "VVsPV9wHJX0iNL4sBxlR")
            add("X-Naver-Client-Secret", "xLLgAoyWoR")
            add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
        }

        val params = LinkedMultiValueMap<String, String>().apply {
            add("paymentKey", paymentKey)
            add("orderId", orderId)
        }

        val httpEntity = HttpEntity(params, headers)
        val response = restTemplate.postForEntity(
                "https://sandbox-api.pay.naver.com/v1/payments/approve", // 테스트용 Sandbox URL
                httpEntity,
                Map::class.java
        )

        return ResponseEntity.ok(response.body as Map<*, *>)
    }
}
