package com.fintrack.app.data

data class CategoryResponse(
    val _id: String,
    val name: String,
    val type: String,
    val icon: String?,
    val userId: String
)