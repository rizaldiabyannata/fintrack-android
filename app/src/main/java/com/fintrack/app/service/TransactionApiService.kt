package com.fintrack.app.service

import com.fintrack.app.data.request.TransactionPayload
import com.fintrack.app.data.response.BaseResponse
import com.fintrack.app.data.response.DailySummaryResponse
import com.fintrack.app.data.response.GetAllTransactionsResponse
import com.fintrack.app.data.response.GetTransactionsResponseItem
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface TransactionApiService {

    @POST("api/transaction")
    fun createTransaction(
        @Header("Authorization") authHeader: String,
        @Body payload: TransactionPayload
    ): Response<BaseResponse>

    @GET("api/transaction")
    suspend fun getAllTransactions(
        @Header("Authorization") token: String,
        @Query("month") month: Int, // Parameter untuk bulan
        @Query("year") year: Int    // Parameter untuk tahun
    ):  Response<List<DailySummaryResponse>>


    @GET("api/transaction/{id}")
    fun getTransactionById(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Response<GetTransactionsResponseItem>

    @PUT("api/transaction/{id}")
    fun updateTransaction(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String,
        @Body payload: TransactionPayload
    ): Response<BaseResponse>

    @DELETE("api/transaction/{id}")
    fun deleteTransaction(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Response<BaseResponse>
}
