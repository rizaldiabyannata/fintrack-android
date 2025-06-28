package com.fintrack.app.data.response

import com.google.gson.annotations.SerializedName

data class User(

    @field:SerializedName("lastLogin")
    val lastLogin: String? = null,

    @field:SerializedName("role")
    val role: String? = null,

    @field:SerializedName("isActive")
    val isActive: Boolean? = null,

    @field:SerializedName("accessToken")
    val accessToken: String? = null,

    @field:SerializedName("uid")
    val uid: String? = null,

    @field:SerializedName("photoURL")
    val photoURL: String? = null,

    @field:SerializedName("emailVerified")
    val emailVerified: Boolean? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("provider")
    val provider: String? = null,

    @field:SerializedName("__v")
    val v: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null,

    @field:SerializedName("refreshToken")
    val refreshToken: String? = null
)