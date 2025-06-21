package com.fintrack.app.data.response

import com.google.gson.annotations.SerializedName

data class GetAllTransactionsResponse(

	@field:SerializedName("GetAllTransactionsResponse")
	val getAllTransactionsResponse: List<GetTransactionsResponseItem?>? = null
)


