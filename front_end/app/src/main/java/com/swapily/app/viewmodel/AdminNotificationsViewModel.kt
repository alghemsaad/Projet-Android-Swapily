package com.swapily.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapily.app.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminNotificationsViewModel : ViewModel() {

    private val repository = AdminRepository()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

    private val _sendSuccess = MutableStateFlow(false)
    val sendSuccess = _sendSuccess.asStateFlow()

    fun sendToAll(title: String, message: String) {
        viewModelScope.launch {
            _loading.value = true
            repository.sendNotificationToAll(title, message).onSuccess {
                _sendSuccess.value = true
            }.onFailure {
                _error.value = it.message ?: "Failed to send notification"
            }
            _loading.value = false
        }
    }

    fun sendToUser(userId: String, title: String, message: String) {
        viewModelScope.launch {
            _loading.value = true
            repository.sendNotificationToUser(userId, title, message).onSuccess {
                _sendSuccess.value = true
            }.onFailure {
                _error.value = it.message ?: "Failed to send notification"
            }
            _loading.value = false
        }
    }

    fun resetSendSuccess() {
        _sendSuccess.value = false
    }
}
