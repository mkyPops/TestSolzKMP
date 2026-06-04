package com.testsolz.features.admin.dashboard.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.testsolz.core.network.ApiTask
import com.testsolz.core.network.NoticeResponse
import com.testsolz.core.network.TestSolzApiClient
import com.testsolz.core.network.TodayAttendanceEmployee
import com.testsolz.domain.models.LeaveRequest
import com.testsolz.domain.models.TaskPriority
import com.testsolz.domain.models.formatted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

data class EmployeeInfo(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val department: String,
    val status: String,
    val checkInTime: String?,
    val checkOutTime: String?,
    val totalWorkedMinutes: Int,
    val isLate: Boolean,
    val isOnLeave: Boolean
)

enum class StatCategory(val title: String) {
    PRESENT_TODAY("Present Today"),
    ON_LEAVE("On Leave"),
    LATE_TODAY("Late Today"),
    PENDING_REQUESTS("Pending Requests")
}

data class PendingRequestInfo(
    val id: String,
    val employeeName: String,
    val department: String,
    val requestType: String,
    val reason: String,
    val date: String
)

class AdminDashboardViewModel : ViewModel() {
    private val apiClient = TestSolzApiClient()

    private val _metrics = MutableStateFlow(DashboardMetrics())
    val metrics: StateFlow<DashboardMetrics> = _metrics.asStateFlow()

    private val _selectedCategory = MutableStateFlow<StatCategory?>(null)
    val selectedCategory: StateFlow<StatCategory?> = _selectedCategory.asStateFlow()

    private val _allEmployees = MutableStateFlow<List<EmployeeInfo>>(emptyList())
    val allEmployees: StateFlow<List<EmployeeInfo>> = _allEmployees.asStateFlow()

    private val _pendingRequestInfos = MutableStateFlow<List<PendingRequestInfo>>(emptyList())
    val pendingRequestInfos: StateFlow<List<PendingRequestInfo>> = _pendingRequestInfos.asStateFlow()

    private val _assignedTasks = MutableStateFlow<List<ApiTask>>(emptyList())
    val assignedTasks: StateFlow<List<ApiTask>> = _assignedTasks.asStateFlow()

    private val _adminNotices = MutableStateFlow<List<NoticeResponse>>(emptyList())
    val adminNotices: StateFlow<List<NoticeResponse>> = _adminNotices.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val employeesResponse = apiClient.employees()
                val dashboard = apiClient.adminDashboard().metrics
                val attendanceEmployees = apiClient.todayAttendanceStatus().employees
                val pendingRequests = apiClient.adminRequests("PENDING")
                val approvedRequests = apiClient.adminRequests("APPROVED")
                val adminTasks = apiClient.adminTasks()
                val notices = apiClient.adminNotices()

                val onLeaveUserIds = approvedRequests
                    .filter { it.type.name == "LEAVE" }
                    .map { it.userId }
                    .toSet()

                val employees = attendanceEmployees.map { attendance ->
                    val employee = attendance.toEmployeeInfo()
                    if (employee.id in onLeaveUserIds) {
                        employee.copy(status = "On Leave", checkInTime = null, checkOutTime = null, isOnLeave = true)
                    } else {
                        employee
                    }
                }

                _allEmployees.value = employees
                _pendingRequestInfos.value = pendingRequests.map { it.toPendingRequestInfo() }
                _assignedTasks.value = adminTasks
                _adminNotices.value = notices

                _metrics.value = DashboardMetrics(
                    totalEmployees = dashboard.totalEmployees.takeIf { it > 0 } ?: employeesResponse.total,
                    presentToday = dashboard.todayAttendanceCount,
                    onLeave = dashboard.employeesOnLeave,
                    lateToday = dashboard.lateEmployees,
                    pendingRequests = pendingRequests.size,
                    absentToday = dashboard.absentEmployees,
                    checkedIn = dashboard.currentlyCheckedIn,
                    checkedOut = dashboard.checkedOut,
                    missingCheckoutAlerts = dashboard.missingCheckoutAlerts,
                    manualCheckIns = dashboard.manualPhoneCheckIns,
                    unknownCardEvents = dashboard.unknownCardEvents,
                    todayTotalWorkedMinutes = dashboard.todayTotalWorkedMinutes,
                    activeNotices = dashboard.activeNotices,
                    pendingTasks = dashboard.pendingTasks,
                    overdueTasks = dashboard.overdueTasks
                )
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to load dashboard"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getEmployeesForCategory(category: StatCategory): List<EmployeeInfo> {
        return when (category) {
            StatCategory.PRESENT_TODAY -> _allEmployees.value.filter { it.status == "Present" }
            StatCategory.ON_LEAVE -> _allEmployees.value.filter { it.status == "On Leave" }
            StatCategory.LATE_TODAY -> _allEmployees.value.filter { it.status == "Late" }
            StatCategory.PENDING_REQUESTS -> emptyList()
        }
    }

    fun selectCategory(category: StatCategory) {
        _selectedCategory.value = category
    }

    fun clearCategory() {
        _selectedCategory.value = null
    }

    fun saveNotice(
        noticeId: String?,
        title: String,
        message: String,
        priority: String,
        audienceType: String,
        department: String?,
        isActive: Boolean,
        onSuccess: () -> Unit = {}
    ) {
        if (title.isBlank() || message.isBlank()) return
        viewModelScope.launch {
            _errorMessage.value = null
            _successMessage.value = null
            try {
                if (noticeId == null) {
                    apiClient.createNotice(
                        title = title.trim(),
                        message = message.trim(),
                        priority = priority,
                        audienceType = audienceType,
                        department = department,
                        isActive = isActive
                    )
                    _successMessage.value = if (isActive) "Notice published" else "Draft saved"
                } else {
                    apiClient.updateNotice(
                        noticeId = noticeId,
                        title = title.trim(),
                        message = message.trim(),
                        priority = priority,
                        audienceType = audienceType,
                        department = department,
                        isActive = isActive
                    )
                    _successMessage.value = "Notice updated"
                }
                loadDashboard()
                onSuccess()
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to save notice"
            }
        }
    }

    fun deleteNotice(noticeId: String) {
        viewModelScope.launch {
            _errorMessage.value = null
            _successMessage.value = null
            try {
                if (apiClient.deleteNotice(noticeId)) {
                    _successMessage.value = "Notice deleted"
                    loadDashboard()
                }
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to delete notice"
            }
        }
    }

    fun syncFirebaseAttendance() {
        viewModelScope.launch {
            _errorMessage.value = null
            _successMessage.value = null
            try {
                val result = apiClient.pullFirebaseAttendance()
                _successMessage.value = "Firebase sync: ${result.attendance} attendance, ${result.unknown} unknown, ${result.duplicates} duplicates, ${result.failed} failed"
                loadDashboard()
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to sync Firebase attendance"
            }
        }
    }

    fun assignTask(employeeIds: List<String>, title: String, description: String?, dueDate: String, priority: TaskPriority, onSuccess: () -> Unit = {}) {
        if (employeeIds.isEmpty() || title.isBlank()) return
        viewModelScope.launch {
            _errorMessage.value = null
            _successMessage.value = null
            try {
                apiClient.createAdminTask(
                    employeeIds = employeeIds,
                    title = title.trim(),
                    description = description?.trim()?.takeIf { it.isNotBlank() },
                    dueDate = dueDate,
                    priority = priority
                )
                _successMessage.value = "Task assigned"
                loadDashboard()
                onSuccess()
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to assign task"
            }
        }
    }

    fun defaultTaskDueDate(): String {
        return Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
            .plus(DatePeriod(days = 1))
            .toString()
    }

    override fun onCleared() {
        apiClient.close()
        super.onCleared()
    }
}

data class DashboardMetrics(
    val totalEmployees: Int = 0,
    val presentToday: Int = 0,
    val onLeave: Int = 0,
    val lateToday: Int = 0,
    val pendingRequests: Int = 0,
    val absentToday: Int = 0,
    val checkedIn: Int = 0,
    val checkedOut: Int = 0,
    val missingCheckoutAlerts: Int = 0,
    val manualCheckIns: Int = 0,
    val unknownCardEvents: Int = 0,
    val todayTotalWorkedMinutes: Int = 0,
    val activeNotices: Int = 0,
    val pendingTasks: Int = 0,
    val overdueTasks: Int = 0
)

private fun TodayAttendanceEmployee.toEmployeeInfo(): EmployeeInfo {
    val latestSession = sessions.lastOrNull()
    val parsedCheckIn = (checkInTime ?: latestSession?.checkInAt)?.let { runCatching { Instant.parse(it) }.getOrNull() }
    val parsedCheckOut = (checkOutTime ?: latestSession?.checkOutAt)?.let { runCatching { Instant.parse(it) }.getOrNull() }
    val formattedCheckIn = parsedCheckIn?.formatted("time")
    val formattedCheckOut = parsedCheckOut?.formatted("time")
    val isLate = parsedCheckIn?.isLateCheckIn() == true || flags.any { it.equals("late", ignoreCase = true) }
    val normalizedStatus = when {
        status.equals("absent", ignoreCase = true) -> "Absent"
        isLate -> "Late"
        status.equals("present", ignoreCase = true) || status.equals("checked_out", ignoreCase = true) -> "Present"
        else -> "Absent"
    }

    return EmployeeInfo(
        id = userId,
        name = name,
        email = email,
        role = role,
        department = department ?: "N/A",
        status = normalizedStatus,
        checkInTime = formattedCheckIn,
        checkOutTime = formattedCheckOut,
        totalWorkedMinutes = totalWorkedMinutes,
        isLate = isLate,
        isOnLeave = false
    )
}

private fun LeaveRequest.toPendingRequestInfo(): PendingRequestInfo {
    val typeLabel = if (leaveType != null) {
        "Leave - ${leaveType.displayName}"
    } else {
        type.displayName
    }

    return PendingRequestInfo(
        id = id,
        employeeName = userName,
        department = userDepartment ?: "N/A",
        requestType = typeLabel,
        reason = reason,
        date = formattedDateRange
    )
}

private fun Instant.isLateCheckIn(): Boolean {
    val localTime = toLocalDateTime(TimeZone.currentSystemDefault()).time
    val minutes = localTime.hour * 60 + localTime.minute
    return minutes > 555
}
