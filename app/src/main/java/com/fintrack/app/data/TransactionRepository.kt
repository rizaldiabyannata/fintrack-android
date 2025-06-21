package com.fintrack.app.data

import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.data.response.DailySummaryResponse
import com.fintrack.app.data.response.GetAllTransactionsResponse
import com.fintrack.app.data.response.GetTransactionsResponseItem
import com.fintrack.app.service.TransactionApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository untuk menangani operasi data terkait transaksi.
 * Kelas ini mengambil data dari TransactionApiService dan menyediakan
 * data tersebut ke UI layer, sambil menangani logika error dan threading.
 */
@Singleton
class TransactionRepository @Inject constructor(
    private val apiService: TransactionApiService,
    private val sessionManager: SessionManager
) {

    private fun getAuthToken(): String {
        // Mengambil token dari session, jika null, gunakan token hardcoded tanpa prefix "Bearer".
        val token = sessionManager.getToken() ?: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY4NGY3YjA2OWM0MjRjODk5MjRmZmZlMyIsImVtYWlsIjoibWluZG5vdGZvdW5kNUBnbWFpbC5jb20iLCJpYXQiOjE3NTA1MDEyMzksImV4cCI6MTc1MDUwNDgzOX0.SXEldpZUN0gSiIvX_Jod1hyiLJXuyiGPuv45RDWU9po"
        return "Bearer $token"
    }

    fun getAllTransactions(month: Int, year: Int): Flow<ApiResponse<List<DailySummaryResponse>>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.getAllTransactions(getAuthToken(), month, year)
            if (response.isSuccessful && response.body() != null) {
                // response.body() sekarang adalah List<DailySummaryResponse>
                emit(ApiResponse.Success(response.body()!!))
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                emit(ApiResponse.Error(errorMessage))
            }
        } catch (e: HttpException) {
            emit(ApiResponse.Error("Gagal terhubung ke server. Kode: ${e.code()}"))
        } catch (e: IOException) {
            emit(ApiResponse.Error("Tidak ada koneksi internet. Periksa jaringan Anda."))
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.message ?: "Terjadi kesalahan tidak terduga."))
        }
    }

    /**
     * Helper function untuk mem-parse pesan error dari JSON response.
     * @param errorBody String JSON dari error response.
     * @param code Kode status HTTP.
     * @return String pesan error yang sudah diparse.
     */
    private fun parseErrorMessage(errorBody: String?, code: Int): String {
        return try {
            val jsonObj = JSONObject(errorBody ?: "")
            jsonObj.getString("message")
        } catch (e: Exception) {
            "Terjadi kesalahan (Kode: $code)"
        }
    }
}
