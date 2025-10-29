package com.godtech.ktboot.config

import com.godtech.ktboot.interceptor.JwtInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.file.Paths
import org.springframework.web.client.RestTemplate

@Configuration
class WebConfig(
        private val jwtInterceptor: JwtInterceptor
) : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val uploadPath = Paths.get(System.getProperty("user.dir"), "uploads").toUri().toString()

        registry.addResourceHandler("/uploads/**") // URL 패턴
                .addResourceLocations(uploadPath)      // 실제 로컬 폴더
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        // /api/** 경로는 Spring Boot에서 처리되도록 제외
        registry.addViewController("/{path:^(?!api)(?!.*\\.).*$}")
            .setViewName("forward:/index.html")

        // /edit/{id}/{other} 형태의 경로 처리 (2개 이상의 세그먼트 포함)
        registry.addViewController("/edit/{other:.*}")
            .setViewName("forward:/index.html")

    }


    override fun addInterceptors(registry: InterceptorRegistry) {
        //registry.addInterceptor(jwtInterceptor).addPathPatterns("/**")
    }

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}