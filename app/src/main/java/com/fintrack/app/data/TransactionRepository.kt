package com.fintrack.app.data

import android.util.Log
import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.data.request.CreateTransactionRequest
import com.fintrack.app.data.request.TransactionPayload
import com.fintrack.app.data.response.BaseResponse
import com.fintrack.app.data.response.DailySummaryResponse
import com.fintrack.app.data.response.GetTransactionsResponseItem
import com.fintrack.app.service.TransactionApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val apiService: TransactionApiService,
    private val sessionManager: SessionManager
) {

    private fun getAuthToken(): String {
        val token = sessionManager.getAccessToken()
        return "Bearer $token"
    }

    fun getAllTransactions(month: Int, year: Int): Flow<ApiResponse<List<DailySummaryResponse>>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.getAllTransactions(getAuthToken(), month, year)
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

    fun postCreateTransaction(payload: CreateTransactionRequest): Flow<ApiResponse<BaseResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            Log.d("TransactionRepository", "Payload: $payload")
            val response = apiService.createTransaction(getAuthToken(), payload)
            if (response.isSuccessful && response.body() != null) {
                emit(ApiResponse.Success(response.body()!!))
            } else {
                emit(ApiResponse.Error(parseErrorMessage(response.errorBody()?.string(), response.code())))
            }
        } catch (e: HttpException) {
            val errorMessage = parseErrorMessage(e.response()?.errorBody()?.string(), e.code())
            emit(ApiResponse.Error(errorMessage))
        } catch (e: IOException) {
            emit(ApiResponse.Error("Tidak ada koneksi internet. Periksa jaringan Anda."))
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.message ?: "Terjadi kesalahan tidak terduga."))
        }
    }

    fun getTransactionById(id: String): Flow<ApiResponse<GetTransactionsResponseItem>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.getTransactionById(getAuthToken(), id)
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

    fun putUpdateTransaction(id: String, payload: TransactionPayload): Flow<ApiResponse<BaseResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.updateTransaction(getAuthToken(), id, payload)
            if (response.isSuccessful && response.body() != null) {
                emit(ApiResponse.Success(response.body()!!))
            } else {
                emit(ApiResponse.Error(parseErrorMessage(response.errorBody()?.string(), response.code())))
            }
        } catch (e: HttpException) {
            val errorMessage = parseErrorMessage(e.response()?.errorBody()?.string(), e.code())
            emit(ApiResponse.Error(errorMessage))
        } catch (e: IOException) {
            emit(ApiResponse.Error("Tidak ada koneksi internet. Periksa jaringan Anda."))
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.message ?: "Terjadi kesalahan tidak terduga."))
        }
    }

    /**
     * FUNGSI BARU: Menghapus transaksi berdasarkan ID.
     */
    fun deleteTransactionById(id: String): Flow<ApiResponse<BaseResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.deleteTransaction(getAuthToken(), id)
            if (response.isSuccessful && response.body() != null) {
                emit(ApiResponse.Success(response.body()!!))
            } else {
                emit(ApiResponse.Error(parseErrorMessage(response.errorBody()?.string(), response.code())))
            }
        } catch (e: HttpException) {
            val errorMessage = parseErrorMessage(e.response()?.errorBody()?.string(), e.code())
            emit(ApiResponse.Error(errorMessage))
        } catch (e: IOException) {
            emit(ApiResponse.Error("Tidak ada koneksi internet. Periksa jaringan Anda."))
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.message ?: "Terjadi kesalahan saat menghapus data."))
        }
    }

    fun exportTransactions(): Flow<ApiResponse<BaseResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.exportTransactions(getAuthToken())
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
            emit(ApiResponse.Error(e.message ?: "Gagal melakukan export."))
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
