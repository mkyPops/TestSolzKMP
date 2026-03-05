package com.testsolz.features.employee.requests.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.testsolz.domain.models.LeaveRequest
import com.testsolz.domain.models.LeaveType
import com.testsolz.domain.models.RequestStatus
import com.testsolz.domain.models.RequestType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

/**
 * Requests ViewModel
 * Manages employee requests state
 */
class RequestsViewModel : ViewModel() {
    
    private val _requests = MutableStateFlow<List<LeaveRequest>>(emptyList())
    val requests: StateFlow<List<LeaveRequest>> = _requests.asStateFlow()
    
    init {
        loadRequests()
    }
    
    private fun loadRequests() {
        viewModelScope.launch {
            // Load mock requests (filtered by current user)
            _requests.value = LeaveRequest.mockRequests.take(2)
        }
    }

    fun submitLeaveRequest(
        leaveType: LeaveType,
        startDate: LocalDate,
        endDate: LocalDate?,
        reasonLabel: String
    ) {
        viewModelScope.launch {
            val request = LeaveRequest(
                id = generateId(),
                userId = "1",
                userName = "Mashaal Khan",
                userDepartment = "Engineering",
                type = RequestType.LEAVE,
                leaveType = leaveType,
                startDate = startDate,
                endDate = endDate,
                expectedTime = null,
                reason = reasonLabel,
                status = RequestStatus.PENDING,
                adminComment = null,
                createdAt = Clock.System.now(),
                reviewedAt = null,
                reviewedBy = null
            )

            _requests.value = listOf(request) + _requests.value
        }
    }

    fun submitLateArrival(
        expectedTime: String,
        note: String
    ) {
        viewModelScope.launch {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

            val request = LeaveRequest(
                id = generateId(),
                userId = "1",
                userName = "Mashaal Khan",
                userDepartment = "Engineering",
                type = RequestType.LATE_ARRIVAL,
                leaveType = null,
                startDate = today,
                endDate = null,
                expectedTime = expectedTime,
                reason = note.ifBlank { "Late arrival" },
                status = RequestStatus.PENDING,
                adminComment = null,
                createdAt = Clock.System.now(),
                reviewedAt = null,
                reviewedBy = null
            )

            _requests.value = listOf(request) + _requests.value
        }
    }
}

private fun generateId(): String = Random.nextLong().toString()
