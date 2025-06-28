package com.fintrack.app.ui.manageBudget

import androidx.annotation.DrawableRes

data class BudgetItem(
    val id: String,
    val name: String,
    val amount: Double,
    val used: Double,
    @DrawableRes val iconResId: Int
)