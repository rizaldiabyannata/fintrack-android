package com.fintrack.app.data.response

import com.google.gson.annotations.SerializedName

data class GetStatWithTypeResponse(

	@field:SerializedName("data")
	val data: Data? = null
)

data class Data(

	@field:SerializedName("total_expense")
	val totalExpense: Int? = null,

	@field:SerializedName("breakdown_by_type")
	val breakdownByType: List<BreakdownByTypeItem?>? = null
)

data class BreakdownByTypeItem(

	@field:SerializedName("total_amount")
	val totalAmount: Int? = null,

	@field:SerializedName("percentage")
	val percentage: Int? = null,

	@field:SerializedName("details")
	val details: List<DetailsItemStat?>? = null,

	@field:SerializedName("type")
	val type: String? = null
)
