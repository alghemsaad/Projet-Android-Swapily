package com.swapily.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapily.app.data.model.Product
import com.swapily.app.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminProductsViewModel : ViewModel() {

    private val repository = AdminRepository()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

    private val _actionSuccess = MutableStateFlow(false)
    val actionSuccess = _actionSuccess.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _loading.value = true
            repository.getAllProductsAdmin().collect { productList ->
                _products.value = productList
                _loading.value = false
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            _loading.value = true
            repository.deleteProductAdmin(productId).onSuccess {
                _actionSuccess.value = true
            }.onFailure {
                _error.value = it.message ?: "Failed to delete product"
            }
            _loading.value = false
        }
    }

    fun archiveProduct(productId: String) {
        viewModelScope.launch {
            _loading.value = true
            repository.archiveProduct(productId).onSuccess {
                _actionSuccess.value = true
            }.onFailure {
                _error.value = it.message ?: "Failed to archive product"
            }
            _loading.value = false
        }
    }

    fun resetActionSuccess() {
        _actionSuccess.value = false
    }
}
