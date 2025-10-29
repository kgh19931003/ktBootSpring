package com.godtech.ktboot.config

import com.godtech.ktboot.filter.JwtTokenFilter
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.time.LocalDateTime

@EnableWebSecurity
@Configuration
class WebSecurityConfig {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:3000", "http://localhost:3001")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        configuration.allowedHeaders = listOf("*")  // 모든 header 허용
        configuration.allowCredentials = true
        configuration.exposedHeaders = listOf("Authorization")
        configuration.maxAge = 3600L

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun filterChain(http: HttpSecurity, jwtTokenFilter: JwtTokenFilter): SecurityFilterChain {

        http.csrf { it.disable() }
        http.logout { it.disable() }
        http.cors { it.configurationSource(corsConfigurationSource()) }  // CORS 설정 적용
        http.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        // 예외 처리 추가
        http.exceptionHandling {
            it.authenticationEntryPoint { request, response, authException ->
                response.contentType = "application/json"
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write(
                    ObjectMapper().writeValueAsString(
                        mapOf(
                            "timestamp" to LocalDateTime.now().toString(),
                            "status" to HttpStatus.UNAUTHORIZED.value(),
                            "error" to "Unauthorized",
                            "message" to "Invalid token or token expired",
                            "path" to request.requestURI
                        )
                    )
                )
            }
        }
        http.authorizeHttpRequests {
            it.requestMatchers(
                //"/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/swagger-resources/**",
                "/v3/api-docs/**",
                "/v3/api-docs/swagger-config",
                "/webjars/**",
                "/auth/**",
                "/auth/login",
                //"/member/**",
                //"/product/**",
                "/dashboard/**",
                "/blog/**",
                //"/admin/blog/**",
                "/inquiry/**",
                "/policy/**",
                "/product/**",
                "/performance/**",
                "/alloy/**",
                "/img/**",
                //"/api/**",
                "/kakao-pay/**",
                "/naver-pay/**",
                "/nice-pay/**",
                "/login/**",
                "/edit/**",
                "/kotlin/**",
                "/php/**",
                "/resources/**",
                "/api/upload/**",
                "/upload/**",
                "/uploads/**",
                "/error"
            )
                .permitAll()
                .anyRequest().authenticated()
        }
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    fun PasswordEncoder(): PasswordEncoder = BCryptPasswordEncoder(14)

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager


}