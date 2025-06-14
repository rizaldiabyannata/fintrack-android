package com.fintrack.app.data.network

/**
 * Sebuah generic class yang membungkus response dari API beserta statusnya.
 * @param T Tipe data dari response yang diharapkan jika sukses.
 */
sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(val errorMessage: String) : ApiResponse<Nothing>()
    object Loading : ApiResponse<Nothing>()
}
