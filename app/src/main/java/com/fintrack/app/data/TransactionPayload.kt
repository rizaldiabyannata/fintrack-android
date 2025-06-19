package com.fintrack.app.data

data class TransactionPayload(
    val category: String,
    val type: String, // "expense" atau "income"
    val amount: Int,
    val description: String? = null,
    val date: String? = null // format ISO: "2025-06-14T00:00:00Z"
)