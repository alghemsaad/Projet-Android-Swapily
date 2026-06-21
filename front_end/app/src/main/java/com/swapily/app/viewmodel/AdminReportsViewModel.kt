package com.swapily.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swapily.app.data.model.Report
import com.swapily.app.data.repository.ReportsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminReportsViewModel : ViewModel() {

    private val repository = ReportsRepository()

    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports = _reports.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

    private val _actionSuccess = MutableStateFlow(false)
    val actionSuccess = _actionSuccess.asStateFlow()

    init {
        loadReports()
    }

    fun loadReports() {
        viewModelScope.launch {
            _loading.value = true
            repository.getAllReports().collect { reportList ->
                _reports.value = reportList
                _loading.value = false
            }
        }
    }

    fun resolveReport(reportId: String) {
        viewModelScope.launch {
            _loading.value = true
            repository.resolveReport(reportId).onSuccess {
                _actionSuccess.value = true
            }.onFailure {
                _error.value = it.message ?: "Failed to resolve report"
            }
            _loading.value = false
        }
    }

    fun dismissReport(reportId: String) {
        viewModelScope.launch {
            _loading.value = true
            repository.dismissReport(reportId).onSuccess {
                _actionSuccess.value = true
            }.onFailure {
                _error.value = it.message ?: "Failed to dismiss report"
            }
            _loading.value = false
        }
    }

    fun deleteReportedProduct(productId: String) {
        viewModelScope.launch {
            _loading.value = true
            repository.deleteReportedProduct(productId).onSuccess {
                _actionSuccess.value = true
            }.onFailure {
                _error.value = it.message ?: "Failed to delete product"
            }
            _loading.value = false
        }
    }

    fun blockReportedUser(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            repository.blockReportedUser(userId).onSuccess {
                _actionSuccess.value = true
            }.onFailure {
                _error.value = it.message ?: "Failed to block user"
            }
            _loading.value = false
        }
    }

    fun resetActionSuccess() {
        _actionSuccess.value = false
    }
}
