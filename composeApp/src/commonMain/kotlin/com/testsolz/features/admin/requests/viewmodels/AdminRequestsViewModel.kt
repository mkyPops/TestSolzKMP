package com.testsolz.features.admin.requests.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    
    private val _requests = MutableStateFlow<List<LeaveRequest>>(emptyList())
    val requests: StateFlow<List<LeaveRequest>> = _requests.asStateFlow()
    
    init {
        loadRequests()
    }
    
    private fun loadRequests() {
        viewModelScope.launch {
            // Load all requests
            _requests.value = LeaveRequest.mockRequests
        }
    }
}
