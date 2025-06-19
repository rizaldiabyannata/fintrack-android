package com.fintrack.app.data.request

data class CategoryPayload(
    val name: String,
    val type: String, // "expense" atau "income"
    val icon: String? = null
)