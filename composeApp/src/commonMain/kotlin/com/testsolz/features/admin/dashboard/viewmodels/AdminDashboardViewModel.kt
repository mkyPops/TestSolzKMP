package com.testsolz.features.admin.dashboard.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Admin Dashboard ViewModel
 * Manages dashboard metrics and stats
 */
class AdminDashboardViewModel : ViewModel() {
    
    private val _metrics = MutableStateFlow(DashboardMetrics())
    val metrics: StateFlow<DashboardMetrics> = _metrics.asStateFlow()
    
    init {
        loadMetrics()
    }
    
    private fun loadMetrics() {
        // Mock metrics
        _metrics.value = DashboardMetrics(
            totalEmployees = 45,
            presentToday = 38,
            onLeave = 4,
            lateToday = 3,
            pendingRequests = 7
        )
    }
}

data class DashboardMetrics(
    val totalEmployees: Int = 0,
    val presentToday: Int = 0,
    val onLeave: Int = 0,
    val lateToday: Int = 0,
    val pendingRequests: Int = 0
)
