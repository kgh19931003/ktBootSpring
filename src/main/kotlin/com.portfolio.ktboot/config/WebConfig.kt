package com.portfolio.ktboot.config

import com.portfolio.ktboot.interceptor.JwtInterceptor
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
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/app/uploads/")
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/{path:^(?!api)(?!uploads)(?!.*\\.).*$}")
                .setViewName("forward:/index.html")

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