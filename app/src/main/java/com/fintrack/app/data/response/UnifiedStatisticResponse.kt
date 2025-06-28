package com.fintrack.app.data.response

import com.google.gson.annotations.SerializedName

data class UnifiedStatisticResponse(
    @field:SerializedName("summary")
    val summary: Summary? = null,

    @field:SerializedName("expense_breakdown")
    val expenseBreakdown: List<ExpenseBreakdownItem?>? = null,

    @field:SerializedName("income_breakdown")
    val incomeBreakdown: List<IncomeBreakdownItem?>? = null,

    // Properti dari GetStatWithTypeResponse
    @field:SerializedName("data")
    val data: Data? = null
)
