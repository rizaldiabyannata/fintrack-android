package com.fintrack.app.service

import com.fintrack.app.data.response.GetAllBudgetResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.GET


interface BudgetApiService {
    @GET("api/budget")
    suspend fun getAllBudget(
        @Header("Authorization") token: String = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY4NGY3YjA2OWM0MjRjODk5MjRmZmZlMyIsImVtYWlsIjoibWluZG5vdGZvdW5kNUBnbWFpbC5jb20iLCJpYXQiOjE3NTAyNDY5ODEsImV4cCI6MTc1MDI1MDU4MX0.OOCjkRhwFZNoIBhQF0ZQQ_oHhn9MpKkPDXgZKhxMMrg"
    ): Response<GetAllBudgetResponse>
}