package com.fintrack.app.service

import com.fintrack.app.data.request.LoginRequest
import com.fintrack.app.data.request.RegisterRequest
import com.fintrack.app.data.request.UserPayload
import com.fintrack.app.data.response.BaseResponse
import com.fintrack.app.data.response.LoginResponse
import com.fintrack.app.data.response.VerifyOtpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): Response<BaseResponse>

    @POST("api/auth/login")
    suspend fun login(
//        @Header("Authorization") token: String,
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @POST("api/auth/google")
    suspend fun loginWithGoogle(
        @Header("Authorization") token: String,
        @Body userPayload: UserPayload
    ): Response<LoginResponse>

    @POST("api/auth/reset-password")
    suspend fun requestPasswordReset(
        @Body emailData: Map<String, String>
    ): Response<BaseResponse>

    @POST("api/auth/verify-reset-password-otp")
    suspend fun verifyResetPasswordOtp(
        @Body otpData: Map<String, String>
    ): Response<VerifyOtpResponse>

    @POST("api/auth/set-new-password")
    suspend fun setNewPassword(
        @Body passwordData: Map<String, String>
    ): Response<BaseResponse>

    @POST("api/auth/verify-email-otp")
    suspend fun verifyEmailOtp(
        @Body otpData: Map<String, String>
    ): Response<LoginResponse>

    @POST("api/auth/resend-verification")
    suspend fun resendVerificationOtp(
        @Body emailData: Map<String, String>
    ): Response<BaseResponse>

    @POST("api/auth/logout")
    suspend fun logout(
        @Body payload: Map<String, String>
    ): Response<BaseResponse>
}
