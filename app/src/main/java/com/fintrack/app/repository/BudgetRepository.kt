package com.fintrack.app.repository

import com.fintrack.app.data.BudgetMonthlyResponse
import com.fintrack.app.data.BudgetPayload
import com.fintrack.app.data.BudgetResponse
import com.fintrack.app.service.BudgetApiService

class BudgetRepository(private val api: BudgetApiService) {

    suspend fun getAllBudgets(token: String): List<BudgetResponse> {
        val response = api.getAllBudgets("Bearer $token").execute()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to fetch budgets: ${response.code()}")
        }
    }

    suspend fun getBudgetMonthly(token: String, month: String): BudgetMonthlyResponse {
        val response = api.getBudgetMonthly("Bearer $token", month).execute()
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response")
        } else {
            throw Exception("Failed to fetch monthly budget: ${response.code()}")
        }
    }

    suspend fun getBudgetById(token: String, id: String): BudgetResponse {
        val response = api.getBudgetById("Bearer $token", id).execute()
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Budget not found")
        } else {
            throw Exception("Failed to get budget: ${response.code()}")
        }
    }

    suspend fun createBudget(token: String, payload: BudgetPayload) {
        val response = api.createBudget("Bearer $token", payload).execute()
        if (!response.isSuccessful) {
            throw Exception("Failed to create budget: ${response.code()}")
        }
    }

    suspend fun updateBudget(token: String, id: String, payload: BudgetPayload) {
        val response = api.updateBudget("Bearer $token", id, payload).execute()
        if (!response.isSuccessful) {
            throw Exception("Failed to update budget: ${response.code()}")
        }
    }

    suspend fun deleteBudget(token: String, id: String) {
        val response = api.deleteBudget("Bearer $token", id).execute()
        if (!response.isSuccessful) {
            throw Exception("Failed to delete budget: ${response.code()}")
        }
    }
}
