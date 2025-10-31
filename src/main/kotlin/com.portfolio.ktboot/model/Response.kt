package com.portfolio.ktboot.model


data class Response<T>(
    val message: String = "success",
    val result: Boolean = false,
    val data: T? = null,
) {
    companion object {
        fun <T> success(
            data: T? = null,
            message: String = "success",
        ): Response<T> {
            return Response(message = message, result = true, data = data)
        }

        fun <T> fail(
            message: String = "fail",
            data: T? = null,
        ): Response<T> {
            return Response(message = message, result = false, data = data)
        }
    }
}
