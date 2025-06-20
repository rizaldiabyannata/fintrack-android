package com.fintrack.app.data

data class BudgetPayload(
    val category: String,      // Nama kategori
    val amountLimit: Int       // Jumlah maksimal pengeluaran
)