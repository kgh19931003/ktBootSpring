package com.portfolio.ktboot

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor

fun main() {
    val encryptor = StandardPBEStringEncryptor()
    encryptor.setAlgorithm("PBEWithMD5AndDES")
    encryptor.setPassword("bP9@kD52mN2@vL7*wQ4&hJ6") // application.yml의 암호와 동일

    val encrypted = encryptor.encrypt("portfolio0425!")
    println("ENC($encrypted)")
}
