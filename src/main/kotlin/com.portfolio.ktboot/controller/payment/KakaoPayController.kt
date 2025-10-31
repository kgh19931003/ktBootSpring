package com.portfolio.ktboot.controller.payment

import com.portfolio.ktboot.form.payment.PayRequest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate

@RestController
@RequestMapping("/kakao-pay")
class KakaoPayController(
        private val restTemplate: RestTemplate
) {

    @PostMapping("/ready")
    fun ready(@RequestBody req: PayRequest): ResponseEntity<Map<*, *>> {
        val headers = HttpHeaders().apply {
            add("Authorization", "KakaoAK 4b1480621699777453062a82a562b1ce")
            add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
        }

        val params = LinkedMultiValueMap<String, String>().apply {
            add("cid", "TC0ONETIME") // 테스트 CID
            add("partner_order_id", "order_1234")
            add("partner_user_id", "user_1234")
            add("item_name", req.itemName)
            add("quantity", "1")
            add("total_amount", req.amount.toString())
            add("vat_amount", "0")
            add("tax_free_amount", "0")
            add("approval_url", "http://localhost:3000/kakao/success")
            add("cancel_url", "http://localhost:3000/kakao/cancel")
            add("fail_url", "http://localhost:3000/kakao/fail")
        }

        val httpEntity = HttpEntity(params, headers)
        val response = restTemplate.postForEntity(
                "https://kapi.kakao.com/v1/payment/ready",
                httpEntity,
                Map::class.java
        )

        // ⚠️ response.body 안에 tid 저장(DB 또는 세션)
        return ResponseEntity.ok(response.body as Map<*, *>)
    }

    @PostMapping("/approve")
    fun approve(@RequestParam("pg_token") pgToken: String): ResponseEntity<Map<String, Any>> {
        val tid = "저장해둔_tid" // ready() 호출 시 DB/세션에 저장했던 tid

        val headers = HttpHeaders().apply {
            add("Authorization", "KakaoAK ${System.getenv("KAKAO_ADMIN_KEY")}")
            add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
        }

        val params = LinkedMultiValueMap<String, String>().apply {
            add("cid", "TC0ONETIME")
            add("tid", tid)
            add("partner_order_id", "order_1234")
            add("partner_user_id", "user_1234")
            add("pg_token", pgToken)
        }

        val httpEntity = HttpEntity(params, headers)
        val response = restTemplate.postForEntity(
                "https://kapi.kakao.com/v1/payment/approve",
                httpEntity,
                Map::class.java
        )

        return ResponseEntity.ok(response.body as Map<String, Any>)
    }

}

