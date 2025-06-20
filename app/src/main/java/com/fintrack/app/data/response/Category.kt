package com.fintrack.app.data.response

import com.google.gson.annotations.SerializedName

data class Category(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("_id")
    val id: String? = null
)