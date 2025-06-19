package com.fintrack.app.repository

import com.fintrack.app.data.TransactionPayload
import com.fintrack.app.data.TransactionResponse
import com.fintrack.app.service.TransactionApiService

class TransactionRepository(private val api: TransactionApiService) {

    suspend fun getAllTransactions(token: String): List<TransactionResponse> {
        val response = api.getAllTransactions("Bearer $token").execute()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to fetch transactions: ${response.code()}")
        }
    }

    suspend fun getTransactionById(token: String, id: String): TransactionResponse {
        val response = api.getTransactionById("Bearer $token", id).execute()
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Transaction not found")
        } else {
            throw Exception("Failed to get transaction: ${response.code()}")
        }
    }

    suspend fun createTransaction(token: String, payload: TransactionPayload): TransactionResponse {
        val response = api.createTransaction("Bearer $token", payload).execute()
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response")
        } else {
            throw Exception("Failed to create transaction: ${response.code()}")
        }
    }

    suspend fun updateTransaction(token: String, id: String, payload: TransactionPayload): TransactionResponse {
        val response = api.updateTransaction("Bearer $token", id, payload).execute()
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response")
        } else {
            throw Exception("Failed to update transaction: ${response.code()}")
        }
    }

    suspend fun deleteTransaction(token: String, id: String) {
        val response = api.deleteTransaction("Bearer $token", id).execute()
        if (!response.isSuccessful) {
            throw Exception("Failed to delete transaction: ${response.code()}")
        }
    }
}
