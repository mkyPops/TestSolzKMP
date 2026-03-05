package com.testsolz.features.employee.attendancehistory.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    
    private val _attendanceHistory = MutableStateFlow<List<Attendance>>(emptyList())
    val attendanceHistory: StateFlow<List<Attendance>> = _attendanceHistory.asStateFlow()
    
    init {
        loadHistory()
    }
    
    private fun loadHistory() {
        viewModelScope.launch {
            // Load mock history
            _attendanceHistory.value = Attendance.mockHistory(userId = "1", daysBack = 30)
        }
    }
}
