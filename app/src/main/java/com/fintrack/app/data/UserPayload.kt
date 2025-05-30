package com.fintrack.app.data

data class UserPayload(
    val uid: String? = null,
    val email: String? = null,
    val name: String? = null,
    val provider: String? = null,
    val password: String? = null,
    val idToken: String? = null
)