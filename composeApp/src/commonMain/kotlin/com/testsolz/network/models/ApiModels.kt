package com.testsolz.network.models

import kotlinx.serialization.Serializable

// ─── Enums ───────────────────────────────────────────
enum class UserRole { ADMIN, EMPLOYEE }
enum class RequestType { LEAVE, LATE_ARRIVAL }
enum class LeaveType { SICK, VACATION, PERSONAL, EMERGENCY }
enum class RequestStatus { PENDING, APPROVED, REJECTED }
enum class AttendanceStatus { ABSENT, PRESENT, CHECKED_OUT }

// ─── Auth ─────────────────────────────────────────────
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val user: UserData
)

@Serializable
data class UserData(
    val id: String,
    val email: String,
    val name: String,
    val role: String,
    val department: String = "",
    val profileImageURL: String? = null
)

// ─── Error ────────────────────────────────────────────
@Serializable
data class ApiError(
    val error: ErrorDetail? = null
)

@Serializable
data class ErrorDetail(
    val code: String,
    val message: String
)

// ─── Employees ────────────────────────────────────────
@Serializable
data class Employee(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val role: String = "EMPLOYEE",
    val department: String = "",
    val profileImageURL: String? = null
)

@Serializable
data class CreateEmployeeRequest(
    val email: String,
    val name: String,
    val department: String,
    val password: String,
    val profileImageURL: String? = null
)

@Serializable
data class EmployeeListResponse(
    val items: List<Employee> = emptyList(),
    val page: Int = 1,
    val limit: Int = 20,
    val total: Int = 0
)

// ─── Attendance ───────────────────────────────────────
@Serializable
data class AttendanceRecord(
    val id: String = "",
    val userId: String = "",
    val date: String = "",
    val checkInTime: String? = null,
    val checkOutTime: String? = null
)

@Serializable
data class AttendanceHistoryResponse(
    val attendance: List<AttendanceRecord> = emptyList()
)

@Serializable
data class EmployeeAttendanceStatus(
    val userId: String = "",
    val name: String = "",
    val role: String = "",
    val department: String = "",
    val status: String = "",
    val checkInTime: String? = null,
    val checkOutTime: String? = null
)

@Serializable
data class AttendanceStatusResponse(
    val date: String = "",
    val employees: List<EmployeeAttendanceStatus> = emptyList()
)

// ─── Requests (Leave & Late) ──────────────────────────
@Serializable
data class LeaveRequest(
    val type: String = "LEAVE",
    val leaveType: String,
    val startDate: String,
    val endDate: String,
    val reason: String
)

@Serializable
data class LateArrivalRequest(
    val type: String = "LATE_ARRIVAL",
    val startDate: String,
    val expectedTime: String,
    val reason: String
)

@Serializable
data class Request(
    val id: String = "",
    val type: String = "",
    val status: String = "PENDING",
    val reason: String = "",
    val startDate: String = "",
    val endDate: String? = null,
    val leaveType: String? = null,
    val expectedTime: String? = null
)

@Serializable
data class RequestListResponse(
    val requests: List<Request> = emptyList()
)

@Serializable
data class AdminCommentRequest(
    val adminComment: String
)
