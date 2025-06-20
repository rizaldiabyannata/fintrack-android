package com.fintrack.app.data.response

import com.google.gson.annotations.SerializedName

data class GetMonthlyBudgetResponse(

	@field:SerializedName("totalBudget")
	val totalBudget: Int? = null,

	@field:SerializedName("remainingBudgetList")
	val remainingBudgetList: List<RemainingBudgetListItem?>? = null,

	@field:SerializedName("totalExpense")
	val totalExpense: Int? = null
)

data class RemainingBudgetListItem(

	@field:SerializedName("amountLimit")
	val amountLimit: Int? = null,

	@field:SerializedName("endDate")
	val endDate: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("categoryName")
	val categoryName: Any? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("remainingAmount")
	val remainingAmount: Int? = null,

	@field:SerializedName("spentAmount")
	val spentAmount: Int? = null,

	@field:SerializedName("__v")
	val v: Int? = null,

	@field:SerializedName("percentage")
	val percentage: Int? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("category")
	val category: Any? = null,

	@field:SerializedName("startDate")
	val startDate: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
