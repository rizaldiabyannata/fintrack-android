package com.fintrack.app.data.request

data class BudgetPayload(
    val category: String,      // Nama kategori
    val amountLimit: Int       // Jumlah maksimal pengeluaran
)