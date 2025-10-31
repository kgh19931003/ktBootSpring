package com.portfolio.ktboot.proto

import kotlin.reflect.KClass


fun Any?.isNotNull(): Boolean = isNull().not()

fun Any?.isNull(): Boolean = (this == null)


fun Any?.doPrint(message: String = "") {
    println("$message : $this")
}

fun <T : Any, V> Map<String, V>.intoClass(clazz: KClass<T>): T {
    val constructor = clazz.constructors.first()
    val arguments = constructor.parameters
        .map { it to this[it.name] }
        .toMap()

    return constructor.callBy(arguments)
}
