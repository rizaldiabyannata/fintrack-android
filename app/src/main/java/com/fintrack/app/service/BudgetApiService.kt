package com.fintrack.app.service

import com.fintrack.app.data.BudgetPayload
import com.fintrack.app.data.BudgetMonthlyResponse
import com.fintrack.app.data.BudgetResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface BudgetApiService {

    // Ambil semua budget user
    @GET("api/budget")
    fun getAllBudgets(
        @Header("Authorization") authHeader: String
    ): Call<List<BudgetResponse>>

    // Ambil budget spesifik bulan
    @GET("api/budget/monthly")
    fun getBudgetMonthly(
        @Header("Authorization") authHeader: String,
        @Query("month") month: String // Format: "2025-06-01"
    ): Call<BudgetMonthlyResponse>

    // Ambil satu budget berdasarkan ID
    @GET("api/budget/{id}")
    fun getBudgetById(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Call<BudgetResponse>

    // Tambah budget
    @POST("api/budget")
    fun createBudget(
        @Header("Authorization") authHeader: String,
        @Body payload: BudgetPayload
    ): Call<ResponseBody>

    // Update budget
    @PUT("api/budget/{id}")
    fun updateBudget(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String,
        @Body payload: BudgetPayload
    ): Call<ResponseBody>

    // Hapus budget
    @DELETE("api/budget/{id}")
    fun deleteBudget(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Call<ResponseBody>
}
