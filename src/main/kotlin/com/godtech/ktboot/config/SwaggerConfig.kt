package com.godtech.ktboot.config

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement as SwaggerSecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme as SwaggerSecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
                .info(
                        Info()
                                .title("API 문서")
                                .version("v1.0.0")
                                .description("Spring Boot + Swagger + JWT 인증 예제")
                )
                .addSecurityItem(SwaggerSecurityRequirement().addList("bearerAuth"))
                .components(
                        io.swagger.v3.oas.models.Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        SwaggerSecurityScheme()
                                                .type(SwaggerSecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                )
    }
}
