package com.swapily.app.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapily.app.data.model.Product
import com.swapily.app.data.model.User
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

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

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

    fun toggleFavorite(productId: String, user: User?, onUpdate: (User) -> Unit) {
        val currentUser = user ?: return
        val currentFavs = currentUser.favorites.toMutableList()
        
        if (currentFavs.contains(productId)) {
            currentFavs.remove(productId)
        } else {
            currentFavs.add(productId)
        }
        
        val updatedUser = currentUser.copy(favorites = currentFavs)
        
        viewModelScope.launch {
            // Update local state immediately for UI responsiveness
            _favorites.value = currentFavs.toSet()
            
            // Persist to Firestore via Repository
            val result = authRepository.updateUserProfile(updatedUser)
            if (result.isSuccess) {
                onUpdate(updatedUser)
            } else {
                _error.value = "Failed to update favorites"
            }
        }
    }

    fun syncFavorites(user: User?) {
        _favorites.value = user?.favorites?.toSet() ?: emptySet()
    }

    fun addProduct(
        title: String,
        description: String,
        category: String,
        lookingFor: String,
        location: String,
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
                    location = location
                )
                
                val dbResult = repository.addProduct(newProduct)
                dbResult.onSuccess {
                    _addProductSuccess.value = true
                    _loading.value = false
                }.onFailure {
                    _error.value = it.message ?: "Upload error"
                    _loading.value = false
                }
            }.onFailure {
                _error.value = it.message ?: "Upload error"
                _loading.value = false
            }
        }
    }

    fun updateProduct(
        productId: String,
        title: String,
        description: String,
        category: String,
        lookingFor: String,
        location: String,
        existingImages: List<String>,
        newImageUris: List<Uri>
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = ""

            // 1. Upload new images if any
            val uploadResult = if (newImageUris.isNotEmpty()) {
                repository.uploadProductImages(newImageUris)
            } else {
                Result.success(emptyList())
            }

            uploadResult.onSuccess { newUrls ->
                val finalImages = existingImages + newUrls
                val updatedProduct = Product(
                    id = productId,
                    userId = authRepository.getCurrentUserUid() ?: "",
                    title = title,
                    description = description,
                    category = category,
                    lookingFor = lookingFor,
                    images = finalImages,
                    condition = "LIKE NEW",
                    location = location
                )

                val result = repository.updateProduct(updatedProduct)
                result.onSuccess {
                    _addProductSuccess.value = true // Reuse success state
                    _loading.value = false
                }.onFailure {
                    _error.value = it.message ?: "Update error"
                    _loading.value = false
                }
            }.onFailure {
                _error.value = it.message ?: "Upload error"
                _loading.value = false
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.deleteProduct(productId)
            result.onSuccess {
                _addProductSuccess.value = true // Reuse success state for navigation back
                _loading.value = false
            }.onFailure {
                _error.value = it.message ?: "Delete error"
                _loading.value = false
            }
        }
    }

    fun resetAddProductSuccess() {
        _addProductSuccess.value = false
    }
}
