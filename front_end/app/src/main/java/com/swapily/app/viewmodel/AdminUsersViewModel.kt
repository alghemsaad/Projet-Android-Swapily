package com.swapily.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapily.app.data.model.User
import com.swapily.app.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminUserDetail(
    val user: User,
    val productCount: Int = 0,
    val swapCount: Int = 0
)

class AdminUsersViewModel : ViewModel() {

    private val repository = AdminRepository()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

    private val _actionSuccess = MutableStateFlow(false)
    val actionSuccess = _actionSuccess.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _loading.value = true
            repository.getAllUsers().collect { userList ->
                _users.value = userList
                _loading.value = false
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun blockUser(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            repository.blockUser(userId).onSuccess {
                _actionSuccess.value = true
            }.onFailure {
                _error.value = it.message ?: "Failed to block user"
            }
            _loading.value = false
        }
    }

    fun unblockUser(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            repository.unblockUser(userId).onSuccess {
                _actionSuccess.value = true
            }.onFailure {
                _error.value = it.message ?: "Failed to unblock user"
            }
            _loading.value = false
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            repository.deleteUser(userId).onSuccess {
                _actionSuccess.value = true
            }.onFailure {
                _error.value = it.message ?: "Failed to delete user"
            }
            _loading.value = false
        }
    }

    suspend fun getUserDetail(user: User): AdminUserDetail {
        val productCount = repository.getUserProductCount(user.uid)
        val swapCount = repository.getUserSwapCount(user.uid)
        return AdminUserDetail(user, productCount, swapCount)
    }

    fun resetActionSuccess() {
        _actionSuccess.value = false
    }
}
