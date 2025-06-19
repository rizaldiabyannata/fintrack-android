package com.fintrack.app.data.response

data class BudgetResponse(
    val _id: String,
    val amountLimit: Int,
    val startDate: String,
    val endDate: String,
    val category: String, // Bisa ID atau objek tergantung backend populate
    val userId: String
)