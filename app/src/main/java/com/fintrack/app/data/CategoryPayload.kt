package com.fintrack.app.data

data class CategoryPayload(
    val name: String,
    val type: String, // "expense" atau "income"
    val icon: String? = null
)