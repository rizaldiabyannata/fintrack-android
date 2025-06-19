package com.fintrack.app.viewmodel

import androidx.lifecycle.*
import com.fintrack.app.data.*
import com.fintrack.app.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoryViewModel(private val repository: CategoryRepository) : ViewModel() {

    private val _categories = MutableLiveData<List<CategoryResponse>>()
    val categories: LiveData<List<CategoryResponse>> = _categories

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadCategories(token: String) {
        viewModelScope.launch {
            try {
                _categories.value = repository.getAllCategories(token)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun addCategory(token: String, payload: CategoryPayload) {
        viewModelScope.launch {
            try {
                repository.createCategory(token, payload)
                loadCategories(token) // refresh
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun deleteCategory(token: String, id: String) {
        viewModelScope.launch {
            try {
                repository.deleteCategory(token, id)
                loadCategories(token) // refresh
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
