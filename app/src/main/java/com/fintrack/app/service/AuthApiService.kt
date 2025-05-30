package com.fintrack.app.service

import com.fintrack.app.data.UserPayload
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {

    // Google Sign-In / Register
    @POST("api/auth/google")
    fun postUserData(
        @Header("Authorization") authHeader: String,
        @Body user: UserPayload
    ): Call<ResponseBody>

    // Email/Password Registration
    @POST("api/auth/register")
    fun registerUser(
        @Body user: UserPayload
    ): Call<ResponseBody>

    // Email/Password Login
    @POST("api/auth/login")
    fun loginUser(
        @Body loginData: Map<String, String>
    ): Call<ResponseBody>

    // Password Reset Request (send OTP)
    @POST("api/auth/reset-password")
    fun resetPassword(
        @Body emailData: Map<String, String>
    ): Call<ResponseBody>

    // Verify Password Reset OTP
    @POST("api/auth/verify-reset-password-otp")
    fun verifyResetPasswordOTP(
        @Body otpData: Map<String, String>
    ): Call<ResponseBody>

    // Set New Password (after OTP verification)
    @POST("api/auth/set-new-password")
    fun setNewPassword(
        @Body passwordData: Map<String, String>
    ): Call<ResponseBody>

    // Resend Email Verification OTP
    @POST("api/auth/resend-verification")
    fun resendEmailVerification(
        @Body emailData: Map<String, String>
    ): Call<ResponseBody>

    // Verify Email OTP
    @POST("api/auth/verify-email-otp")
    fun verifyEmailOTP(
        @Body otpData: Map<String, String>
    ): Call<ResponseBody>

    // Test endpoint (Optional)
    @POST("api/auth/test")
    fun testEndpoint(): Call<ResponseBody>
}
