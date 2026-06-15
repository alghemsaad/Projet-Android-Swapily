package com.swapily.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapily.app.data.model.MatchResult
import com.swapily.app.data.repository.AuthRepository
import com.swapily.app.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SmartMatchViewModel : ViewModel() {
    private val productRepository = ProductRepository()
    private val authRepository = AuthRepository()

    private val _matches = MutableStateFlow<List<MatchResult>>(emptyList())
    val matches = _matches.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

    init {
        loadMatches()
    }

    fun loadMatches() {
        val currentUserId = authRepository.getCurrentUserUid() ?: return
        viewModelScope.launch {
            _loading.value = true
            _matches.value = emptyList() // Clear previous results
            try {
                val results = productRepository.getSmartMatches(currentUserId)
                _matches.value = results
                android.util.Log.d("SmartMatch", "Found ${results.size} matches")
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load matches"
                android.util.Log.e("SmartMatch", "Error: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }
}
