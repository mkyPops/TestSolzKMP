package com.testsolz.domain.repository

import com.testsolz.domain.models.Attendance
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Attendance Repository
 * Handles attendance data for both Employee and Admin
 */
class AttendanceRepository(private val client: HttpClient) {

    /**
     * Employee: Check-in for today
     */
    suspend fun checkIn(userId: String): Result<Attendance> = runCatching {
        client.post("attendance/check-in") {
            setBody(mapOf("userId" to userId))
        }.body()
    }

    /**
     * Employee: Check-out for today
     */
    suspend fun checkOut(attendanceId: String): Result<Attendance> = runCatching {
        client.post("attendance/check-out/$attendanceId").body()
    }

    /**
     * Admin: Fetch all employees' current status
     */
    fun getAllEmployeeStatus(): Flow<List<Attendance>> = flow {
        // In a real app, this might use a WebSocket or frequent polling
        val response: List<Attendance> = client.get("admin/attendance/status").body()
        emit(response)
    }
    
    /**
     * Admin: Fetch history for a specific employee
     */
    suspend fun getEmployeeHistory(employeeId: String): Result<List<Attendance>> = runCatching {
        client.get("admin/attendance/history/$employeeId").body()
    }
}
