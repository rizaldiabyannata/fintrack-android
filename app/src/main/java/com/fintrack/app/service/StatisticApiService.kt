package com.fintrack.app.service

import com.fintrack.app.data.response.UnifiedStatisticResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface StatisticApiService {

    @GET("api/statistics/monthly")
    suspend fun getMonthlyStats(
        @Header("Authorization") token: String,
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("type") type: String? = null
    ): Response<UnifiedStatisticResponse>

    @GET("api/statistics/yearly")
    suspend fun getYearlyStats(
        @Header("Authorization") token: String,
        @Query("year") year: Int,
        @Query("type") type: String? = null
    ): Response<UnifiedStatisticResponse>
}
