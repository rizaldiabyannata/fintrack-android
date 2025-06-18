package com.fintrack.app.service

import com.fintrack.app.data.response.GetAllBudgetResponseItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.GET


interface BudgetApiService {
    @GET("api/budget")
    suspend fun getAllBudget(
        @Header("Authorization") token: String = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY4NGY3YjA2OWM0MjRjODk5MjRmZmZlMyIsImVtYWlsIjoibWluZG5vdGZvdW5kNUBnbWFpbC5jb20iLCJpYXQiOjE3NTAyNTA4MjksImV4cCI6MTc1MDI1NDQyOX0.amWsMGNpGR-z5-Vox9VSu29xaKZu1gKFnhI3njz2_TQ"
    ): Response<List<GetAllBudgetResponseItem>>
}