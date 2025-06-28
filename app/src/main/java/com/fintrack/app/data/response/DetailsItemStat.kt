package com.fintrack.app.data.response

import com.google.gson.annotations.SerializedName

data class DetailsItemStat(
    @field:SerializedName("category_name")
    val categoryName: String? = null,

    @field:SerializedName("total_amount")
    val totalAmount: Int? = null,

    @field:SerializedName("percentage")
    val percentage: Double? = null
)
