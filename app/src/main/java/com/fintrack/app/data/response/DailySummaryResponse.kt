package com.fintrack.app.data.response

import com.google.gson.annotations.SerializedName

/**
 * Data class ini merepresentasikan satu item dalam array JSON yang dikirim oleh API.
 * Setiap item berisi rangkuman transaksi untuk satu hari.
 */
data class DailySummaryResponse(
    @SerializedName("date")
    val date: String? = null,

    @SerializedName("transactions")
    val transactions: List<GetTransactionsResponseItem>? = null,

    @SerializedName("income")
    val income: Int? = null,

    @SerializedName("expense")
    val expense: Int? = null
)
