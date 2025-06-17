package com.fintrack.app.data

import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.data.request.LoginRequest
import com.fintrack.app.data.request.RegisterRequest
import com.fintrack.app.data.request.UserPayload
import com.fintrack.app.data.response.BaseResponse
import com.fintrack.app.data.response.LoginResponse
import com.fintrack.app.data.response.VerifyOtpResponse
import com.fintrack.app.service.AuthApiService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: AuthApiService,
    private val firebaseAuth: FirebaseAuth // FirebaseAuth tetap di-inject untuk fungsi lain
) {

    /**
     * FIX: Fungsi registrasi sekarang HANYA memanggil backend.
     * Backend bertanggung jawab penuh untuk membuat user di Firebase dan database.
     */
    fun register(registerRequest: RegisterRequest): Flow<ApiResponse<BaseResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            // Langsung panggil API backend untuk registrasi
            val response = apiService.register(registerRequest)
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
     * Fungsi login sekarang langsung memanggil backend tanpa melalui Firebase.
     */
    fun login(loginRequest: LoginRequest): Flow<ApiResponse<LoginResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.login(loginRequest)
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

    fun loginWithGoogle(token: String, payload: UserPayload): Flow<ApiResponse<LoginResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.loginWithGoogle("Bearer $token", payload)
            if (response.isSuccessful && response.body() != null) {
                emit(ApiResponse.Success(response.body()!!))
            } else {
                emit(ApiResponse.Error(parseErrorMessage(response.errorBody()?.string(), response.code())))
            }
        } catch (e: HttpException) {
            emit(ApiResponse.Error("Gagal terhubung ke server. Kode: ${e.code()}"))
        } catch (e: IOException) {
            emit(ApiResponse.Error("Tidak ada koneksi internet. Periksa jaringan Anda."))
        }
    }

    fun requestPasswordReset(email: String): Flow<ApiResponse<BaseResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.requestPasswordReset(mapOf("email" to email))
            if (response.isSuccessful && response.body() != null) {
                emit(ApiResponse.Success(response.body()!!))
            } else {
                emit(ApiResponse.Error(parseErrorMessage(response.errorBody()?.string(), response.code())))
            }
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.message ?: "Gagal mengirim email reset password."))
        }
    }

    fun verifyEmailOtp(email: String, otp: String): Flow<ApiResponse<LoginResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.verifyEmailOtp(mapOf("email" to email, "otp" to otp))
            if (response.isSuccessful && response.body() != null) {
                emit(ApiResponse.Success(response.body()!!))
            } else {
                emit(ApiResponse.Error(parseErrorMessage(response.errorBody()?.string(), response.code())))
            }
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.message ?: "Gagal memverifikasi OTP."))
        }
    }

    fun resendOtp(email: String, purpose: String): Flow<ApiResponse<BaseResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.resendVerificationOtp(mapOf("email" to email, "purpose" to purpose))
            if (response.isSuccessful && response.body() != null) {
                emit(ApiResponse.Success(response.body()!!))
            } else {
                emit(ApiResponse.Error(parseErrorMessage(response.errorBody()?.string(), response.code())))
            }
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.message ?: "Gagal mengirim ulang OTP."))
        }
    }

    fun setNewPassword(email: String, newPassword: String): Flow<ApiResponse<BaseResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.setNewPassword(mapOf("email" to email, "new_password" to newPassword))
            if (response.isSuccessful && response.body() != null) {
                emit(ApiResponse.Success(response.body()!!))
            } else {
                emit(ApiResponse.Error(parseErrorMessage(response.errorBody()?.string(), response.code())))
            }
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.message ?: "Gagal mengatur password baru."))
        }
    }

    fun verifyResetPasswordOtp(email: String, otp: String): Flow<ApiResponse<VerifyOtpResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.verifyResetPasswordOtp(mapOf("email" to email, "otp" to otp))
            if (response.isSuccessful && response.body() != null) {
                emit(ApiResponse.Success(response.body()!!))
            } else {
                emit(ApiResponse.Error(parseErrorMessage(response.errorBody()?.string(), response.code())))
            }
        } catch (e: HttpException) {
            emit(ApiResponse.Error("Gagal terhubung ke server. Kode: ${e.code()}"))
        } catch (e: IOException) {
            emit(ApiResponse.Error("Tidak ada koneksi internet. Periksa jaringan Anda."))
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
