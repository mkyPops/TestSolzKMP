package com.testsolz.features.employee.requests.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.testsolz.core.network.TestSolzApiClient
import com.testsolz.domain.models.LeaveRequest
import com.testsolz.domain.models.LeaveType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Requests ViewModel
 * Manages employee requests state
 */
class RequestsViewModel : ViewModel() {
    private val apiClient = TestSolzApiClient()
    
    private val _requests = MutableStateFlow<List<LeaveRequest>>(emptyList())
    val requests: StateFlow<List<LeaveRequest>> = _requests.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadRequests()
    }
    
    fun loadRequests() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                _requests.value = apiClient.myRequests()
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to load requests"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitLeaveRequest(
        leaveType: LeaveType,
        startDate: LocalDate,
        endDate: LocalDate?,
        reasonLabel: String
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            _errorMessage.value = null

            try {
                val request = apiClient.createLeaveRequest(
                    leaveType = leaveType,
                    startDate = startDate.toString(),
                    endDate = endDate?.toString(),
                    reason = reasonLabel
                )
                loadRequests()
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to submit request"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun submitLateArrival(
        expectedTime: String,
        note: String
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            _errorMessage.value = null

            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

            try {
                val request = apiClient.createLateArrivalRequest(
                    startDate = today.toString(),
                    expectedTime = expectedTime,
                    reason = note.ifBlank { "Late arrival" }
                )
                loadRequests()
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to submit request"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun deleteRequest(requestId: String) {
        viewModelScope.launch {
            _errorMessage.value = null
            try {
                if (apiClient.deleteMyRequest(requestId)) {
                    loadRequests()
                }
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to delete request"
            }
        }
    }

    override fun onCleared() {
        apiClient.close()
        super.onCleared()
    }
}
