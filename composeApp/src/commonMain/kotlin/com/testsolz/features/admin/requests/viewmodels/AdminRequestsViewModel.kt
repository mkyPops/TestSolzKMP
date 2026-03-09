package com.testsolz.features.admin.requests.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.testsolz.domain.models.LeaveRequest
import com.testsolz.domain.models.RequestStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone

/**
 * Admin Requests ViewModel
 * Manages leave/late request approval/rejection
 */
class AdminRequestsViewModel : ViewModel() {

    private val _requests = MutableStateFlow<List<LeaveRequest>>(emptyList())
    val requests: StateFlow<List<LeaveRequest>> = _requests.asStateFlow()

    private val _reviewedRequests = MutableStateFlow<List<LeaveRequest>>(emptyList())
    val reviewedRequests: StateFlow<List<LeaveRequest>> = _reviewedRequests.asStateFlow()

    private val _selectedRequest = MutableStateFlow<LeaveRequest?>(null)
    val selectedRequest: StateFlow<LeaveRequest?> = _selectedRequest.asStateFlow()

    init {
        loadRequests()
    }

    private fun loadRequests() {
        viewModelScope.launch {
            // Load all requests, separate pending from already reviewed
            val allRequests = LeaveRequest.mockRequests
            _requests.value = allRequests.filter { it.status == RequestStatus.PENDING }
            _reviewedRequests.value = allRequests.filter { it.status != RequestStatus.PENDING }
        }
    }

    fun selectRequest(request: LeaveRequest) {
        _selectedRequest.value = request
    }

    fun clearSelectedRequest() {
        _selectedRequest.value = null
    }

    fun approveRequest(requestId: String) {
        val request = _requests.value.find { it.id == requestId } ?: return
        val updatedRequest = request.copy(
            status = RequestStatus.APPROVED,
            adminComment = "Approved by Admin",
            reviewedAt = Clock.System.now(),
            reviewedBy = "Admin"
        )
        // Remove from pending
        _requests.value = _requests.value.filter { it.id != requestId }
        // Add to reviewed
        _reviewedRequests.value = _reviewedRequests.value + updatedRequest
        // Clear selection
        _selectedRequest.value = null
    }

    fun rejectRequest(requestId: String) {
        val request = _requests.value.find { it.id == requestId } ?: return
        val updatedRequest = request.copy(
            status = RequestStatus.REJECTED,
            adminComment = "Rejected by Admin",
            reviewedAt = Clock.System.now(),
            reviewedBy = "Admin"
        )
        // Remove from pending
        _requests.value = _requests.value.filter { it.id != requestId }
        // Add to reviewed
        _reviewedRequests.value = _reviewedRequests.value + updatedRequest
        // Clear selection
        _selectedRequest.value = null
    }
}
