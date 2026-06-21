package com.swapily.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapily.app.data.model.Swap
import com.swapily.app.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminSwapsViewModel : ViewModel() {

    private val repository = AdminRepository()

    private val _swaps = MutableStateFlow<List<Swap>>(emptyList())
    val swaps = _swaps.asStateFlow()

    private val _selectedStatus = MutableStateFlow("ALL")
    val selectedStatus = _selectedStatus.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

    init {
        loadSwaps()
    }

    fun loadSwaps() {
        viewModelScope.launch {
            _loading.value = true
            repository.getAllSwapsAdmin().collect { swapList ->
                _swaps.value = swapList
                _loading.value = false
            }
        }
    }

    fun setStatusFilter(status: String) {
        _selectedStatus.value = status
    }
}
