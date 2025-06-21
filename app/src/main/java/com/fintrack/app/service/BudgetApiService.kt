package com.fintrack.app.service

import com.fintrack.app.data.request.BudgetPayload
import com.fintrack.app.data.response.BaseResponse
import com.fintrack.app.data.response.BudgetMonthlyResponse
import com.fintrack.app.data.response.GetAllBudgetResponse
import com.fintrack.app.data.response.GetByIdBudgetResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface BudgetApiService {

    // Ambil semua budget user
    @GET("api/budget")
    fun getAllBudgets(
        @Header("Authorization") authHeader: String
    ): Response<List<GetAllBudgetResponse>>

    // Ambil budget spesifik bulan
    @GET("api/budget/monthly")
    fun getBudgetMonthly(
        @Header("Authorization") authHeader: String,
        @Query("month") month: String // Format: "2025-06-01"
    ): Response<BudgetMonthlyResponse>

    // Ambil satu budget berdasarkan ID
    @GET("api/budget/{id}")
    fun getBudgetById(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Response<GetByIdBudgetResponse>

    // Tambah budget
    @POST("api/budget")
    fun createBudget(
        @Header("Authorization") authHeader: String,
        @Body payload: BudgetPayload
    ): Response<BaseResponse>

    // Update budget
    @PUT("api/budget/{id}")
    fun updateBudget(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String,
        @Body payload: BudgetPayload
    ): Response<BaseResponse>

    // Hapus budget
    @DELETE("api/budget/{id}")
    fun deleteBudget(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Response<BaseResponse>
}
