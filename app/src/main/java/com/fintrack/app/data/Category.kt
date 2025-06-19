package com.fintrack.app.data

data class Category(
    val id: String,
    val name: String,
    val type: String,
    val icon: String? = null,
    val userId: String? = null
)

// data dummy
object DummyCategories {
    val list = listOf(
        Category(
            id = "1",
            name = "Gaji",
            type = "income",
            icon = "salary",
            userId = "user123"
        ),
        Category(
            id = "2",
            name = "Bonus",
            type = "income",
            icon = "bonus",
            userId = "user123"
        ),
        Category(
            id = "3",
            name = "UMKM",
            type = "income",
            icon = "business",
            userId = "user123"
        ),
        Category(
            id = "4",
            name = "Makan",
            type = "expense",
            icon = "food",
            userId = "user123"
        ),
        Category(
            id = "5",
            name = "Transportasi",
            type = "expense",
            icon = "transport",
            userId = "user123"
        ),
        Category(
            id = "6",
            name = "Hiburan",
            type = "expense",
            icon = "entertainment",
            userId = "user123"
        )
    )
}