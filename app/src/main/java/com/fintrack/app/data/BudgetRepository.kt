package com.fintrack.app.data

import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.data.request.BudgetPayload
import com.fintrack.app.data.response.BaseResponse
import com.fintrack.app.data.response.BudgetMonthlyResponse
import com.fintrack.app.data.response.GetAllBudgetResponse
import com.fintrack.app.data.response.GetByIdBudgetResponse
import com.fintrack.app.service.BudgetApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor(
    private val apiService: BudgetApiService,
    private val sessionManager: SessionManager // Inject SessionManager untuk mengambil token
) {

    // Helper untuk mendapatkan token dengan prefix "Bearer "
    private fun getAuthToken(): String {
        val token = sessionManager.getToken() ?: "Bearer "
        return "Bearer $token"
    }

    /**
     * Mengambil semua data budget dari API.
     * Menggunakan Flow untuk memancarkan state Loading, Success, atau Error.
     */
    fun getAllBudgets(): Flow<ApiResponse<List<GetAllBudgetResponse>>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.getAllBudgets(getAuthToken())
            if (response.isSuccessful && response.body() != null) {
                emit(ApiResponse.Success(response.body()!!))
            } else {
                emit(ApiResponse.Error(parseErrorMessage(response.errorBody()?.string(), response.code())))
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
     * Membuat budget baru melalui API.
     */
    fun createBudget(payload: BudgetPayload): Flow<ApiResponse<BaseResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.createBudget(getAuthToken(), payload)
            if (response.isSuccessful && response.body() != null) {
                emit(ApiResponse.Success(response.body()!!))
            } else {
                emit(ApiResponse.Error(parseErrorMessage(response.errorBody()?.string(), response.code())))
            }
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.message ?: "Gagal membuat budget."))
        }
    }

    /**
     * Mengambil budget bulanan dari API.
     * @param month String dengan format "YYYY-MM-DD"
     */
    fun getBudgetMonthly(month: String): Flow<ApiResponse<BudgetMonthlyResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.getBudgetMonthly(getAuthToken(), month)
            if (response.isSuccessful && response.body() != null) {
                emit(ApiResponse.Success(response.body()!!))
            } else {
                emit(ApiResponse.Error(parseErrorMessage(response.errorBody()?.string(), response.code())))
            }
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.message ?: "Gagal mengambil data budget bulanan."))
        }
    }

    /**
     * Mengambil budget berdasarkan ID.
     */
    fun getBudgetById(id: String): Flow<ApiResponse<GetByIdBudgetResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.getBudgetById(getAuthToken(), id)
            if (response.isSuccessful && response.body() != null) {
                emit(ApiResponse.Success(response.body()!!))
            } else {
                emit(ApiResponse.Error(parseErrorMessage(response.errorBody()?.string(), response.code())))
            }
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.message ?: "Gagal mengambil detail budget."))
        }
    }

    /**
     * Mengupdate budget berdasarkan ID.
     */
    fun updateBudget(id: String, payload: BudgetPayload): Flow<ApiResponse<BaseResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.updateBudget(getAuthToken(), id, payload)
            if (response.isSuccessful && response.body() != null) {
                emit(ApiResponse.Success(response.body()!!))
            } else {
                emit(ApiResponse.Error(parseErrorMessage(response.errorBody()?.string(), response.code())))
            }
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.message ?: "Gagal memperbarui budget."))
        }
    }

    /**
     * Menghapus budget berdasarkan ID.
     */
    fun deleteBudget(id: String): Flow<ApiResponse<BaseResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.deleteBudget(getAuthToken(), id)
            if (response.isSuccessful && response.body() != null) {
                emit(ApiResponse.Success(response.body()!!))
            } else {
                emit(ApiResponse.Error(parseErrorMessage(response.errorBody()?.string(), response.code())))
            }
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.message ?: "Gagal menghapus budget."))
        }
    }

    /**
     * Helper function untuk mem-parse pesan error dari response API.
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