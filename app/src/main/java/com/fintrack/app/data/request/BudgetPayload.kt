package com.fintrack.app.data.request

data class BudgetPayload(
    val category: String,      // Nama kategori
    val amountLimit: Double,       // Jumlah maksimal pengeluaran
)