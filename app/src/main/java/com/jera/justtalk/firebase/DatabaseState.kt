package com.jera.justtalk.firebase

sealed class DatabaseState<out T> {
    data class Success<out T>(val value: T) : DatabaseState<T>()
    data class Error(val error: Throwable) : DatabaseState<Nothing>()
}
