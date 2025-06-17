package com.fintrack.app.data.response

import com.google.gson.annotations.SerializedName

/**
 * Model data untuk User.
 */
data class User(
    @SerializedName("uid")
    val uid: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("provider")
    val provider: String,

    @SerializedName("emailVerified")
    val isEmailVerified: Boolean
)