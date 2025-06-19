package com.fintrack.app.viewmodel

import androidx.lifecycle.*
import com.fintrack.app.data.*
import com.fintrack.app.repository.BudgetRepository
import kotlinx.coroutines.launch

class BudgetViewModel(private val repository: BudgetRepository) : ViewModel() {

    private val _budgets = MutableLiveData<List<BudgetResponse>>()
    val budgets: LiveData<List<BudgetResponse>> = _budgets

    private val _monthly = MutableLiveData<BudgetMonthlyResponse>()
    val monthly: LiveData<BudgetMonthlyResponse> = _monthly

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadBudgets(token: String) {
        viewModelScope.launch {
            try {
                _budgets.value = repository.getAllBudgets(token)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun loadMonthlyBudget(token: String, month: String) {
        viewModelScope.launch {
            try {
                _monthly.value = repository.getBudgetMonthly(token, month)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
