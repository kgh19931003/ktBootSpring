package com.godtech.ktboot.utils


import org.jasypt.encryption.StringEncryptor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class JasyptUtils(@Qualifier("jasyptStringEncryptor") private val encryptor: StringEncryptor) {

    fun encrypt(value: String): String = encryptor.encrypt(value)

    fun decrypt(encryptedValue: String): String = encryptor.decrypt(encryptedValue)
}