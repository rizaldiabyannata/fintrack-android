package com.fintrack.app.data

import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.data.response.GetStatAllResponse
import com.fintrack.app.data.response.GetStatWithTypeResponse
import com.fintrack.app.data.response.UnifiedStatisticResponse
import com.fintrack.app.service.StatisticApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticRepository @Inject constructor(
    private val apiService: StatisticApiService,
    private val sessionManager: SessionManager
) {

    private fun getAuthToken(): String {
        val token = sessionManager.getAccessToken()
        return "Bearer $token"
    }

    fun getMonthlyStats(year: Int, month: Int, type: String?): Flow<ApiResponse<Any>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.getMonthlyStats(getAuthToken(), year, month, type)
            if (response.isSuccessful && response.body() != null) {
                val unifiedResponse = response.body()!!
                val specificResponse = convertToSpecificResponse(unifiedResponse)
                emit(ApiResponse.Success(specificResponse))
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

    fun getYearlyStats(year: Int, type: String?): Flow<ApiResponse<Any>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.getYearlyStats(getAuthToken(), year, type)
            if (response.isSuccessful && response.body() != null) {
                val unifiedResponse = response.body()!!
                val specificResponse = convertToSpecificResponse(unifiedResponse)
                emit(ApiResponse.Success(specificResponse))
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

    private fun convertToSpecificResponse(unified: UnifiedStatisticResponse): Any {
        return if (unified.summary != null) {
            GetStatAllResponse(
                summary = unified.summary,
                expenseBreakdown = unified.expenseBreakdown,
                incomeBreakdown = unified.incomeBreakdown
            )
        } else {
            GetStatWithTypeResponse(
                data = unified.data
            )
        }
    }

    private fun parseErrorMessage(errorBody: String?, code: Int): String {
        return try {
            val jsonObj = JSONObject(errorBody ?: "")
            jsonObj.getString("message")
        } catch (e: Exception) {
            "Terjadi kesalahan (Kode: $code)"
        }
    }
}
