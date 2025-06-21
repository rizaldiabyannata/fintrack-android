package com.fintrack.app.data.response

import com.google.gson.annotations.SerializedName

data class GetTransactionsResponseItem(

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("amount")
    val amount: Int? = null,

    @field:SerializedName("__v")
    val v: Int? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("updateAt")
    val updateAt: String? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("category")
    val category: Any? = null,

    @field:SerializedName("userId")
    val userId: String? = null
)
