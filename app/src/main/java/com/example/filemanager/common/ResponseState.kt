package com.example.filemanager.common

sealed class  ResponseState<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T?) : ResponseState<T>(data)
    class Failure<T>(data: T? = null, message: String?) : ResponseState<T>(data, message)
    class Loading<T> : ResponseState<T>()
}
