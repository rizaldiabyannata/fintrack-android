package com.fintrack.app.data.response

data class CategoryResponse(
    val _id: String,
    val name: String,
    val type: String,
    val icon: String?,
    val userId: String
)