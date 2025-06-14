package com.fintrack.app.data.response

import com.google.gson.annotations.SerializedName

/**
 * Response untuk verifikasi OTP reset password.
 */
data class VerifyOtpResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("reset_token")
    val resetToken: String
)