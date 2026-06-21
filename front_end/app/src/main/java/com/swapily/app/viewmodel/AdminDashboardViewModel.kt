package com.swapily.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapily.app.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardStats(
    val totalUsers: Int = 0,
    val totalProducts: Int = 0,
    val totalSwaps: Int = 0,
    val pendingSwaps: Int = 0,
    val totalReports: Int = 0
)

class AdminDashboardViewModel : ViewModel() {

    private val repository = AdminRepository()

    private val _stats = MutableStateFlow(DashboardStats())
    val stats = _stats.asStateFlow()

    private val _recentActivities = MutableStateFlow<List<AdminRepository.RecentActivity>>(emptyList())
    val recentActivities = _recentActivities.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val users = repository.getTotalUsersCount()
                val products = repository.getTotalProductsCount()
                val swaps = repository.getTotalSwapsCount()
                val pending = repository.getPendingSwapsCount()
                val reports = repository.getTotalReportsCount()
                val activities = repository.getRecentActivities()

                _stats.value = DashboardStats(users, products, swaps, pending, reports)
                _recentActivities.value = activities
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load stats"
            } finally {
                _loading.value = false
            }
        }
    }
}
