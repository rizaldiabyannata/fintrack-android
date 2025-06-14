package com.fintrack.app.data.response

import com.google.gson.annotations.SerializedName

/**
 * Response dasar yang hanya berisi pesan.
 */
data class BaseResponse(
    @SerializedName("message")
    val message: String
)