package com.godtech.ktboot.proto


fun String.combine(str: String): String? {
    return this+str;
}



fun String?.removeBearerPrefix(): String? {
    return when {
        this == null -> null
        this.startsWith("Bearer ", ignoreCase = true) -> this.substring(7).trim()
        else -> this.trim()
    }
}