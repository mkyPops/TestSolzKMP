package com.testsolz.features.employee.attendancehistory.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.testsolz.core.network.TestSolzApiClient
import com.testsolz.domain.models.Attendance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Attendance History ViewModel
 * Manages attendance history state
 */
class AttendanceHistoryViewModel : ViewModel() {
    private val apiClient = TestSolzApiClient()
    
    private val _attendanceHistory = MutableStateFlow<List<Attendance>>(emptyList())
    val attendanceHistory: StateFlow<List<Attendance>> = _attendanceHistory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadHistory()
    }
    
    fun loadHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                _attendanceHistory.value = apiClient.myAttendance(daysBack = 30)
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to load attendance history"
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        apiClient.close()
        super.onCleared()
    }
}
