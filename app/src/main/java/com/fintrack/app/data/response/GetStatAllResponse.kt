package com.fintrack.app.data.response

import com.google.gson.annotations.SerializedName

data class GetStatAllResponse(

	@field:SerializedName("summary")
	val summary: Summary? = null,

	@field:SerializedName("expense_breakdown")
	val expenseBreakdown: List<ExpenseBreakdownItem?>? = null,

	@field:SerializedName("income_breakdown")
	val incomeBreakdown: List<IncomeBreakdownItem?>? = null
)

data class Summary(

	@field:SerializedName("total_expense")
	val totalExpense: Int? = null,

	@field:SerializedName("total_income")
	val totalIncome: Int? = null,

	@field:SerializedName("net_balance")
	val netBalance: Int? = null
)


data class ExpenseBreakdownItem(

	@field:SerializedName("total_amount")
	val totalAmount: Int? = null,

	@field:SerializedName("percentage")
	val percentage: Int? = null,

	@field:SerializedName("details")
	val details: List<DetailsItemStat?>? = null,

	@field:SerializedName("type")
	val type: String? = null
)

data class IncomeBreakdownItem(

	@field:SerializedName("total_amount")
	val totalAmount: Int? = null,

	@field:SerializedName("percentage")
	val percentage: Int? = null,

	@field:SerializedName("details")
	val details: List<DetailsItemStat?>? = null,

	@field:SerializedName("type")
	val type: String? = null
)
