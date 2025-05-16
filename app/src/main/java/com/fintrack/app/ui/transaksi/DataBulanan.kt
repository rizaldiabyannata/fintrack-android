package com.fintrack.app.ui.transaksi

data class DataBulanan(
    val bulan: String,              // ex: "May"
    val tanggal: String,           // ex: "16/05/25"
    val pendapatan: String,        // ex: "Rp. 123.456,00"
    val pengeluaran: String        // ex: "Rp. 50.000,00"
)
