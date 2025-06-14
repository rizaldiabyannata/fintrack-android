package com.fintrack.app.data.response

import com.google.gson.annotations.SerializedName

/**
 * Response untuk login yang berhasil.
 */
data class LoginResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("user")
    val user: User
)