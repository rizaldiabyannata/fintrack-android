package com.fintrack.app.data

import java.util.*

data class Transaction(
    val id: String,
    val userId: String,
    val amount: Int,
    val categoryId: Category,
    val type: String,
    val description: String,
    val createdAt: Date
)

// data dummy
object DummyTransactions {
    val list = listOf(
        Transaction(
            id = "t1",
            userId = "user123",
            amount = 5000000,
            categoryId = DummyCategories.list[0], // Gaji
            type = "income",
            description = "Gaji Bulan Juni",
            createdAt = Calendar.getInstance().apply {
                set(2025, Calendar.JUNE, 5)
            }.time
        ),
        Transaction(
            id = "t2",
            userId = "user123",
            amount = 1500000,
            categoryId = DummyCategories.list[1], // Bonus
            type = "income",
            description = "Bonus Project",
            createdAt = Calendar.getInstance().apply {
                set(2025, Calendar.MAY, 10)
            }.time
        ),
        Transaction(
            id = "t3",
            userId = "user123",
            amount = 1000000,
            categoryId = DummyCategories.list[2], // UMKM
            type = "income",
            description = "Pendapatan UMKM",
            createdAt = Calendar.getInstance().apply {
                set(2025, Calendar.JUNE, 15)
            }.time
        ),
        Transaction(
            id = "t4",
            userId = "user123",
            amount = 200000,
            categoryId = DummyCategories.list[3], // Makan
            type = "expense",
            description = "Makan siang",
            createdAt = Calendar.getInstance().apply {
                set(2025, Calendar.MAY, 7)
            }.time
        ),
        Transaction(
            id = "t5",
            userId = "user123",
            amount = 100000,
            categoryId = DummyCategories.list[4], // Transportasi
            type = "expense",
            description = "Naik ojek",
            createdAt = Calendar.getInstance().apply {
                set(2025, Calendar.JUNE, 9)
            }.time
        ),
        Transaction(
            id = "t6",
            userId = "user123",
            amount = 250000,
            categoryId = DummyCategories.list[5], // Hiburan
            type = "expense",
            description = "Nonton bioskop",
            createdAt = Calendar.getInstance().apply {
                set(2025, Calendar.JUNE, 18)
            }.time
        )
    )
}