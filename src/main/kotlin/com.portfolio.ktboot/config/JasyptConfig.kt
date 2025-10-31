package com.portfolio.ktboot.config

import org.jasypt.encryption.StringEncryptor
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JasyptConfig {

    @Value("\${jasypt.encryptor.password}")
    private val password: String? = null

    @Bean("jasyptStringEncryptor")
    fun stringEncryptor(): StringEncryptor {
        val encryptor = PooledPBEStringEncryptor()
        val config = SimpleStringPBEConfig().apply {
            password = this@JasyptConfig.password
            algorithm = "PBEWithMD5AndDES"
            setKeyObtentionIterations("1000")
            setPoolSize("1")
            providerName = "SunJCE"
            setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator")
            setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator")
            stringOutputType = "base64"
        }
        encryptor.setConfig(config)
        return encryptor
    }



}