package com.fintrack.app.ui.budget

import androidx.annotation.DrawableRes

data class BudgetItem(
    val name: String,
    val amount: Int,
    val used: Double,
    @DrawableRes val iconResId: Int
)