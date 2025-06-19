package com.fintrack.app.service

import com.fintrack.app.data.request.TransactionPayload
import com.fintrack.app.data.response.TransactionResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface TransactionApiService {

    @POST("api/transaction")
    fun createTransaction(
        @Header("Authorization") authHeader: String,
        @Body payload: TransactionPayload
    ): Call<TransactionResponse>

    @GET("api/transaction")
    fun getAllTransactions(
        @Header("Authorization") authHeader: String
    ): Call<List<TransactionResponse>>

    @GET("api/transaction/{id}")
    fun getTransactionById(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Call<TransactionResponse>

    @PUT("api/transaction/{id}")
    fun updateTransaction(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String,
        @Body payload: TransactionPayload
    ): Call<TransactionResponse>

    @DELETE("api/transaction/{id}")
    fun deleteTransaction(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Call<ResponseBody>
}
