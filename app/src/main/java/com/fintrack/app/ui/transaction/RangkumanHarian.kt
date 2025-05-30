package com.fintrack.app.ui.transaction

data class RangkumanHarian(
    val hari: String,              // ex: "Wed"
    val tanggal: String,           // ex: "16/05/25"
    val totalPendapatan: String,        // ex: "Rp. 123.456,00"
    val totalPengeluaran: String,        // ex: "Rp. 50.000,00"
    val detail: List<DataHarian> // <-- tabel ini
)
