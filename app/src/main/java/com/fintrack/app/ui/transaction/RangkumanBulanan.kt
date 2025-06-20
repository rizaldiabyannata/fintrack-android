package com.fintrack.app.ui.transaction

data class RangkumanBulanan(
    val bulan: String,  // ex: "May"
    val tahun: String,  // ex: "2025"
    val totalPendapatan: String,
    val totalPengeluaran: String,
    val detail: List<DataBulanan> // <-- tabel ini
)