package com.portfolio.ktboot.controller.payment

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.util.*

@RestController
@RequestMapping("/nice-pay")
class NicePayController(
        private val restTemplate: RestTemplate = RestTemplate()
) {

    @PostMapping("/test")
    fun testPayment(@RequestBody req: Map<String, Any>): ResponseEntity<Map<String, Any>> {
        val clientId = "S2_aa8dd2561f844d458c252571e05821e5" // 테스트용 상점ID
        val clientSecret = "3fa247ceb3c7404abc0c4e9317f74d77" // 테스트용 상점키

        val orderId = req["orderId"] as String
        val amount = req["amount"] as Int
        val goodsName = req["goodsName"] as String
        val returnUrl = req["returnUrl"] as String

        // ✅ Authorization 헤더 (Basic Auth)
        val rawAuth = "$clientId:$clientSecret"
        val auth = Base64.getEncoder().encodeToString(rawAuth.toByteArray(Charsets.UTF_8))
        println("auth : $auth")

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            accept = listOf(MediaType.APPLICATION_JSON) // 👈 JSON 강제 요청
            set("Authorization", "Basic $auth")
        }

        // ✅ 요청 바디
        val payload = mapOf(
                "clientId" to clientId,
                "orderId" to orderId,
                "amount" to amount,
                "goodsName" to goodsName,
                "returnUrl" to returnUrl
        )

        val entity = HttpEntity(payload, headers)

        // ✅ 응답을 String 으로 받기
        val response = restTemplate.exchange(
                "https://pay.nicepay.co.kr/v1/js/",
                HttpMethod.POST,
                entity,
                String::class.java
        )

        val bodyAsString = response.body ?: ""
        println("Raw Response: $bodyAsString")

        return ResponseEntity.ok(mapOf("raw" to bodyAsString))
    }
}
