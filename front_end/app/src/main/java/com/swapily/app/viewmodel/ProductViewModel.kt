package com.swapily.app.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapily.app.data.model.Product
import com.swapily.app.data.repository.AuthRepository
import com.swapily.app.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {
    private val repository = ProductRepository()
    private val authRepository = AuthRepository()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

    private val _addProductSuccess = MutableStateFlow(false)
    val addProductSuccess = _addProductSuccess.asStateFlow()

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            repository.getAllProducts().collect {
                _products.value = it
            }
        }
    }

    fun addProduct(
        title: String,
        description: String,
        category: String,
        lookingFor: String,
        imageUris: List<Uri>
    ) {
        val uid = authRepository.getCurrentUserUid() ?: return
        
        viewModelScope.launch {
            _loading.value = true
            _error.value = ""
            
            val uploadResult = repository.uploadProductImages(imageUris)
            uploadResult.onSuccess { urls ->
                val newProduct = Product(
                    userId = uid,
                    title = title,
                    description = description,
                    category = category,
                    lookingFor = lookingFor,
                    images = urls,
                    condition = "LIKE NEW", // Par défaut ou à ajouter dans l'UI
                    location = "Detecting..." // Idem
                )
                
                val dbResult = repository.addProduct(newProduct)
                dbResult.onSuccess {
                    _addProductSuccess.value = true
                    _loading.value = false
                }.onFailure {
                    _error.value = it.message ?: "Database error"
                    _loading.value = false
                }
            }.onFailure {
                _error.value = it.message ?: "Upload error"
                _loading.value = false
            }
        }
    }

    fun resetAddProductSuccess() {
        _addProductSuccess.value = false
    }
}
