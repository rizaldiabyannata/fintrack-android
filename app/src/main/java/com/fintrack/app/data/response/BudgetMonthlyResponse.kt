package com.fintrack.app.data.response

data class BudgetMonthlyResponse(
    val totalBudget: Int,
    val totalExpense: Int,
    val remainingBudgetList: List<RemainingBudgetItem>
)

data class RemainingBudgetItem(
    val _id: String,
    val amountLimit: Int,
    val startDate: String,
    val endDate: String,
    val spentAmount: Int,
    val remainingAmount: Int,
    val percentage: Double
)