package com.fintrack.app.ui.transaksi

data class DataHarian(
    val waktu: String,              // ex: "20:30"
    val jenis: String,    // ex: "Makan Siang"
    val media: String,    // ex: "ShopeePay"
    val pendapatan: String,        // ex: "Rp. 123.456,00"
    val pengeluaran: String        // ex: "Rp. 50.000,00"
)
