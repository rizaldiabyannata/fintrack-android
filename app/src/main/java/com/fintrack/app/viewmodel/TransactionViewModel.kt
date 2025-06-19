package com.fintrack.app.viewmodel

import androidx.lifecycle.*
import com.fintrack.app.data.*
import com.fintrack.app.repository.TransactionRepository
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    private val _transactions = MutableLiveData<List<TransactionResponse>>()
    val transactions: LiveData<List<TransactionResponse>> = _transactions

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadTransactions(token: String) {
        viewModelScope.launch {
            try {
                _transactions.value = repository.getAllTransactions(token)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun addTransaction(token: String, payload: TransactionPayload) {
        viewModelScope.launch {
            try {
                repository.createTransaction(token, payload)
                loadTransactions(token) // refresh
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun deleteTransaction(token: String, id: String) {
        viewModelScope.launch {
            try {
                repository.deleteTransaction(token, id)
                loadTransactions(token)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
