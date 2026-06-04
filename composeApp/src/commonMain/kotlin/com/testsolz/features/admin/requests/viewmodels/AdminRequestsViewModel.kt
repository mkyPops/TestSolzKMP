package com.testsolz.features.admin.requests.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.testsolz.core.network.TestSolzApiClient
import com.testsolz.domain.models.LeaveRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Admin Requests ViewModel
 * Manages leave/late request approval/rejection
 */
class AdminRequestsViewModel : ViewModel() {
    private val apiClient = TestSolzApiClient()

    private val _requests = MutableStateFlow<List<LeaveRequest>>(emptyList())
    val requests: StateFlow<List<LeaveRequest>> = _requests.asStateFlow()

    private val _reviewedRequests = MutableStateFlow<List<LeaveRequest>>(emptyList())
    val reviewedRequests: StateFlow<List<LeaveRequest>> = _reviewedRequests.asStateFlow()

    private val _selectedRequest = MutableStateFlow<LeaveRequest?>(null)
    val selectedRequest: StateFlow<LeaveRequest?> = _selectedRequest.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isReviewing = MutableStateFlow(false)
    val isReviewing: StateFlow<Boolean> = _isReviewing.asStateFlow()

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
                val pending = apiClient.adminRequests("PENDING")
                val approved = apiClient.adminRequests("APPROVED")
                val rejected = apiClient.adminRequests("REJECTED")

                _requests.value = pending
                _reviewedRequests.value = approved + rejected
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to load requests"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectRequest(request: LeaveRequest) {
        _selectedRequest.value = request
    }

    fun clearSelectedRequest() {
        _selectedRequest.value = null
    }

    fun approveRequest(requestId: String) {
        reviewRequest(requestId = requestId, approve = true)
    }

    fun rejectRequest(requestId: String) {
        reviewRequest(requestId = requestId, approve = false)
    }

    private fun reviewRequest(requestId: String, approve: Boolean) {
        viewModelScope.launch {
            _isReviewing.value = true
            _errorMessage.value = null

            try {
                val updatedRequest = if (approve) {
                    apiClient.approveRequest(requestId, adminComment = "Approved by Admin")
                } else {
                    apiClient.rejectRequest(requestId, adminComment = "Rejected by Admin")
                }

                loadRequests()
                _selectedRequest.value = null
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to review request"
            } finally {
                _isReviewing.value = false
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
                if (apiClient.deleteAdminRequest(requestId)) {
                    loadRequests()
                    _selectedRequest.value = null
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
