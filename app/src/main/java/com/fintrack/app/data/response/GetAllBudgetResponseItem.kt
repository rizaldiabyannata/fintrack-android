package com.fintrack.app.data.response

import com.google.gson.annotations.SerializedName

data class GetAllBudgetResponseItem(

    @field:SerializedName("amountLimit")
    val amountLimit: Int? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("endDate")
    val endDate: String? = null,

    @field:SerializedName("__v")
    val v: Int? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    // CHANGE THE TYPE OF THE category PROPERTY
    @field:SerializedName("category")
    val category: Category? = null,

    @field:SerializedName("userId")
    val userId: String? = null,

    @field:SerializedName("startDate")
    val startDate: String? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null
)

// ADD THIS NESTED CLASS DEFINITION
data class Category(
    @SerializedName("_id")
    val id: String?,

    @SerializedName("name")
    val name: String?
)