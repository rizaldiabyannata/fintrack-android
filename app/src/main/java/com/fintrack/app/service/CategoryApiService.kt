package com.fintrack.app.service

import com.fintrack.app.data.request.CategoryPayload
import com.fintrack.app.data.response.CategoryResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface CategoryApiService {

    @GET("api/category")
    fun getAllCategories(
        @Header("Authorization") authHeader: String
    ): Call<List<CategoryResponse>>

    @GET("api/category/{id}")
    fun getCategoryById(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Call<CategoryResponse>

    @POST("api/category")
    fun createCategory(
        @Header("Authorization") authHeader: String,
        @Body category: CategoryPayload
    ): Call<CategoryResponse>

    @PUT("api/category/{id}")
    fun updateCategory(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String,
        @Body category: CategoryPayload
    ): Call<CategoryResponse>

    @DELETE("api/category/{id}")
    fun deleteCategory(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Call<ResponseBody>
}
