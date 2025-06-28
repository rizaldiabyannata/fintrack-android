package com.fintrack.app.service

import com.fintrack.app.data.request.BudgetPayload
import com.fintrack.app.data.response.BaseResponse
import com.fintrack.app.data.response.GetAllBudgetResponse
import com.fintrack.app.data.response.GetByIdBudgetResponse
import com.fintrack.app.data.response.GetMonthlyBudgetResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface BudgetApiService {

    // Ambil semua budget user
    @GET("api/budget")
    suspend fun getAllBudgets(
        @Header("Authorization") authHeader: String
    ): Response<List<GetAllBudgetResponse>>

    // Ambil budget spesifik bulan
    @GET("api/budget/monthly")
    suspend fun getBudgetMonthly(
        @Header("Authorization") authHeader: String,
        @Query("month") month: String // Format: "2025-06-01"
    ): Response<GetMonthlyBudgetResponse>

    // Ambil satu budget berdasarkan ID
    @GET("api/budget/{id}")
    suspend fun getBudgetById(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Response<GetByIdBudgetResponse>

    // Tambah budget
    @POST("api/budget")
    suspend fun createBudget(
        @Header("Authorization") authHeader: String,
        @Body payload: BudgetPayload
    ): Response<BaseResponse>

    // Update budget
    @PUT("api/budget/{id}")
    suspend fun updateBudget(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String,
        @Body payload: BudgetPayload
    ): Response<BaseResponse>

    // Hapus budget
    @DELETE("api/budget/{id}")
    suspend fun deleteBudget(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Response<BaseResponse>
}
