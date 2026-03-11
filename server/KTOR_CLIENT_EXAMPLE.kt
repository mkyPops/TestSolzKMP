// Sample Ktor HTTP Client Setup for KMP Frontend
// Use this in your KMP shared code or platform-specific code

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable

@Serializable
data class Employee(
    val id: String,
    val name: String,
    val email: String,
    val department: String,
    val salary: Double
)

@Serializable
data class AttendanceRecord(
    val id: String,
    val employeeId: String,
    val date: String,
    val checkinTime: String?,
    val checkoutTime: String?,
    val status: String
)

@Serializable
data class LeaveRequest(
    val id: String,
    val employeeId: String,
    val startDate: String,
    val endDate: String,
    val reason: String,
    val type: String,
    val status: String
)

// Initialize HTTP Client
val httpClient = HttpClient {
    install(ContentNegotiation) {
        json()
    }
}

const val BASE_URL = "http://localhost:3000/api"

// Employee API calls
suspend fun getEmployees(): List<Employee> {
    return httpClient.get("$BASE_URL/employees").body()
}

suspend fun createEmployee(employee: Employee): Employee {
    return httpClient.post("$BASE_URL/employees") {
        setBody(employee)
    }.body()
}

// Attendance API calls
suspend fun getAttendance(employeeId: String): List<AttendanceRecord> {
    return httpClient.get("$BASE_URL/attendance/$employeeId").body()
}

suspend fun checkin(employeeId: String): AttendanceRecord {
    return httpClient.post("$BASE_URL/attendance/checkin") {
        setBody(mapOf("employeeId" to employeeId))
    }.body()
}

suspend fun checkout(employeeId: String): AttendanceRecord {
    return httpClient.post("$BASE_URL/attendance/checkout") {
        setBody(mapOf("employeeId" to employeeId))
    }.body()
}

// Leave API calls
suspend fun getLeaves(): List<LeaveRequest> {
    return httpClient.get("$BASE_URL/leaves").body()
}

suspend fun submitLeave(leave: LeaveRequest): LeaveRequest {
    return httpClient.post("$BASE_URL/leaves") {
        setBody(leave)
    }.body()
}

suspend fun updateLeaveStatus(leaveId: String, status: String): LeaveRequest {
    return httpClient.patch("$BASE_URL/leaves/$leaveId") {
        setBody(mapOf("status" to status))
    }.body()
}
