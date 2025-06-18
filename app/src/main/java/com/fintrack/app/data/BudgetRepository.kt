package com.fintrack.app.data

import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.data.response.GetAllBudgetResponse
import com.fintrack.app.service.BudgetApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // Memastikan hanya ada satu instance dari repository ini.
class BudgetRepository @Inject constructor(
    private val apiService: BudgetApiService,
) {
    fun getAllBudget(): Flow<ApiResponse<GetAllBudgetResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.getAllBudget()

            if (response.isSuccessful && response.body() != null) {
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
     * Fungsi helper untuk mem-parse pesan error dari JSON.
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
