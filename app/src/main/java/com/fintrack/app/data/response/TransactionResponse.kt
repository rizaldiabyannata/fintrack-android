package com.fintrack.app.data.response

data class TransactionResponse(
    val _id: String,
    val amount: Int,
    val type: String,
    val description: String?,
    val date: String,
    val userId: String,
    val categoryId: CategoryEmbedded
)

data class CategoryEmbedded(
    val _id: String,
    val name: String,
    val type: String,
    val icon: String?
)
