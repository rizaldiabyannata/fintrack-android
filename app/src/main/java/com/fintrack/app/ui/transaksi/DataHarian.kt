package com.fintrack.app.ui.transaksi

data class DataHarian(
    val hari: String,              // ex: "Wed"
    val tanggal: String,           // ex: "16/05/25"
    val pendapatan: String,        // ex: "Rp. 123.456,00"
    val jenisTransaksi: String,    // ex: "Makan Siang"
    val mediaTransaksi: String,    // ex: "ShopeePay"
    val pengeluaran: String        // ex: "Rp. 50.000,00"
)
