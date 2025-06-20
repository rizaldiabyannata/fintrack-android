package com.fintrack.app.ui.transaction

data class BudgetCategory(
    val name: String,
    val amount: Double,
    val iconResId: Int // Resource ID untuk ikon (misal: R.drawable.ic_restaurant)
)