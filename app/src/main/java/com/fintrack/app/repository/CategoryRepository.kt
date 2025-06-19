package com.fintrack.app.repository

import com.fintrack.app.data.CategoryPayload
import com.fintrack.app.data.CategoryResponse
import com.fintrack.app.service.CategoryApiService

class CategoryRepository(private val api: CategoryApiService) {

    suspend fun getAllCategories(token: String): List<CategoryResponse> {
        val response = api.getAllCategories("Bearer $token").execute()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to fetch categories: ${response.code()}")
        }
    }

    suspend fun getCategoryById(token: String, id: String): CategoryResponse {
        val response = api.getCategoryById("Bearer $token", id).execute()
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Category not found")
        } else {
            throw Exception("Failed to get category: ${response.code()}")
        }
    }

    suspend fun createCategory(token: String, payload: CategoryPayload): CategoryResponse {
        val response = api.createCategory("Bearer $token", payload).execute()
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response")
        } else {
            throw Exception("Failed to create category: ${response.code()}")
        }
    }

    suspend fun updateCategory(token: String, id: String, payload: CategoryPayload): CategoryResponse {
        val response = api.updateCategory("Bearer $token", id, payload).execute()
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response")
        } else {
            throw Exception("Failed to update category: ${response.code()}")
        }
    }

    suspend fun deleteCategory(token: String, id: String) {
        val response = api.deleteCategory("Bearer $token", id).execute()
        if (!response.isSuccessful) {
            throw Exception("Failed to delete category: ${response.code()}")
        }
    }
}
