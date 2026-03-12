package com.testsolz.network

import com.testsolz.network.models.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class ApiRepository {
    private val client = HttpClientFactory.client

    // ─── Auth ─────────────────────────────────────────
    suspend fun login(email: String, password: String): LoginResponse {
        return client.post(ApiConfig.LOGIN) {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, password))
        }.body()
    }

    suspend fun getMe(token: String): UserData {
        return client.get(ApiConfig.ME) {
            bearerAuth(token)
        }.body()
    }

    // ─── Employees ────────────────────────────────────
    suspend fun getEmployees(
        token: String,
        page: Int = 1,
        limit: Int = 20,
        query: String? = null,
        department: String? = null
    ): EmployeeListResponse {
        return client.get(ApiConfig.EMPLOYEES) {
            bearerAuth(token)
            parameter("page", page)
            parameter("limit", limit)
            query?.let { parameter("q", it) }
            department?.let { parameter("department", it) }
        }.body()
    }

    suspend fun getEmployeeById(token: String, id: String): Employee {
        return client.get("${ApiConfig.EMPLOYEES}/$id") {
            bearerAuth(token)
        }.body()
    }

    suspend fun createEmployee(token: String, request: CreateEmployeeRequest): Employee {
        return client.post(ApiConfig.EMPLOYEES) {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
            setBody(request)
        }.body()
    }

    suspend fun updateEmployee(token: String, id: String, request: CreateEmployeeRequest): Employee {
        return client.patch("${ApiConfig.EMPLOYEES}/$id") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
            setBody(request)
        }.body()
    }

    // ─── Attendance ───────────────────────────────────
    suspend fun getMyAttendance(token: String, daysBack: Int = 30): AttendanceHistoryResponse {
        return client.get(ApiConfig.ATTENDANCE_ME) {
            bearerAuth(token)
            parameter("daysBack", daysBack)
        }.body()
    }

    suspend fun getTodayAttendanceStatus(token: String): AttendanceStatusResponse {
        return client.get(ApiConfig.ATTENDANCE_STATUS) {
            bearerAuth(token)
        }.body()
    }

    // ─── Requests ─────────────────────────────────────
    suspend fun submitLeaveRequest(token: String, request: LeaveRequest): Request {
        return client.post(ApiConfig.REQUESTS) {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
            setBody(request)
        }.body()
    }

    suspend fun submitLateRequest(token: String, request: LateArrivalRequest): Request {
        return client.post(ApiConfig.REQUESTS) {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
            setBody(request)
        }.body()
    }

    suspend fun getMyRequests(token: String): RequestListResponse {
        return client.get(ApiConfig.REQUESTS_ME) {
            bearerAuth(token)
        }.body()
    }

    suspend fun getAllRequests(token: String, status: String = "PENDING"): RequestListResponse {
        return client.get(ApiConfig.REQUESTS_ADMIN) {
            bearerAuth(token)
            parameter("status", status)
        }.body()
    }

    suspend fun approveRequest(token: String, id: String, comment: String): Request {
        return client.post("${ApiConfig.REQUESTS_ADMIN}/$id/approve") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
            setBody(AdminCommentRequest(comment))
        }.body()
    }

    suspend fun rejectRequest(token: String, id: String, comment: String): Request {
        return client.post("${ApiConfig.REQUESTS_ADMIN}/$id/reject") {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
            setBody(AdminCommentRequest(comment))
        }.body()
    }
}
