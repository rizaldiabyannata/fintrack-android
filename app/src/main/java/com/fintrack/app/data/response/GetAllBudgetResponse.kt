package com.fintrack.app.data.response

import com.google.gson.annotations.SerializedName

data class GetAllBudgetResponse(

	@field:SerializedName("GetAllBudgetResponse")
	val getAllBudgetResponse: List<GetAllBudgetResponseItem?>? = null
)