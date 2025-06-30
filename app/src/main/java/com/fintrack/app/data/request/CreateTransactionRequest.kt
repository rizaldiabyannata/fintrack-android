package com.fintrack.app.data.request

import java.util.Date

data class CreateTransactionRequest(
    val type: String,

    val category: String,

    val amount: Double,

    val description: String,
    )