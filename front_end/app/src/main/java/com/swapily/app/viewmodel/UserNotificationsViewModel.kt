package com.swapily.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapily.app.data.model.AppNotification
import com.swapily.app.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserNotificationsViewModel : ViewModel() {

    private val repository = AdminRepository()

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount = _unreadCount.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun loadNotifications(userId: String) {
        if (userId.isEmpty()) {
            _loading.value = false
            return
        }
        viewModelScope.launch {
            _loading.value = true
            try {
                repository.getUserNotifications(userId).collect { list ->
                    _notifications.value = list
                    _unreadCount.value = list.count { !it.read }
                    _loading.value = false
                }
            } catch (e: Exception) {
                _loading.value = false
                _notifications.value = emptyList()
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(notificationId)
        }
    }

    fun markAllAsRead(userId: String) {
        viewModelScope.launch {
            _notifications.value.filter { !it.read }.forEach {
                repository.markNotificationAsRead(it.id)
            }
        }
    }
}
