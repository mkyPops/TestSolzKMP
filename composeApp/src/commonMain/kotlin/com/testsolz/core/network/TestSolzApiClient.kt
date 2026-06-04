package com.testsolz.core.network

import com.testsolz.core.authentication.AuthSession
import com.testsolz.domain.models.Attendance
import com.testsolz.domain.models.LeaveRequest
import com.testsolz.domain.models.LeaveType
import com.testsolz.domain.models.Project
import com.testsolz.domain.models.TaskItem
import com.testsolz.domain.models.TaskPriority
import com.testsolz.domain.models.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

class TestSolzApiClient(
    private val baseUrl: String = backendBaseUrl,
    private val httpClient: HttpClient = defaultHttpClient(),
    private val tokenProvider: () -> String? = { AuthSession.token.value }
) {
    suspend fun healthCheck(): Boolean {
        val response = httpClient.get(baseUrl)
        return response.status == HttpStatusCode.OK
    }

    suspend fun login(email: String, password: String): LoginResponse {
        val response = httpClient.post("$baseUrl/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email = email, password = password))
        }

        if (response.status.value in 200..299) {
            return response.body()
        }

        throw ApiException(message = response.errorMessage(), statusCode = response.status.value)
    }

    suspend fun me(): User {
        return requestWithBody<UserResponse>(
            httpClient.get("$baseUrl/v1/auth/me") {
                bearerToken()
            }
        ).user
    }

    suspend fun myRequests(): List<LeaveRequest> {
        return requestWithBody<RequestsResponse>(
            httpClient.get("$baseUrl/v1/requests/me") {
                bearerToken()
            }
        ).requests
    }

    suspend fun createLeaveRequest(
        leaveType: LeaveType,
        startDate: String,
        endDate: String?,
        reason: String
    ): LeaveRequest {
        return requestWithBody<RequestResponse>(
            httpClient.post("$baseUrl/v1/requests") {
                bearerToken()
                contentType(ContentType.Application.Json)
                setBody(
                    CreateLeaveRequestBody(
                        type = "LEAVE",
                        leaveType = leaveType.name,
                        startDate = startDate,
                        endDate = endDate,
                        reason = reason
                    )
                )
            }
        ).request
    }

    suspend fun createLateArrivalRequest(
        startDate: String,
        expectedTime: String,
        reason: String
    ): LeaveRequest {
        return requestWithBody<RequestResponse>(
            httpClient.post("$baseUrl/v1/requests") {
                bearerToken()
                contentType(ContentType.Application.Json)
                setBody(
                    CreateLateArrivalRequestBody(
                        type = "LATE_ARRIVAL",
                        startDate = startDate,
                        expectedTime = expectedTime,
                        reason = reason
                    )
                )
            }
        ).request
    }

    suspend fun adminRequests(status: String): List<LeaveRequest> {
        return requestWithBody<RequestsResponse>(
            httpClient.get("$baseUrl/v1/requests/admin") {
                bearerToken()
                parameter("status", status)
            }
        ).requests
    }

    suspend fun approveRequest(requestId: String, adminComment: String? = null): LeaveRequest {
        return reviewRequest(path = "approve", requestId = requestId, adminComment = adminComment)
    }

    suspend fun rejectRequest(requestId: String, adminComment: String? = null): LeaveRequest {
        return reviewRequest(path = "reject", requestId = requestId, adminComment = adminComment)
    }

    suspend fun deleteMyRequest(requestId: String): Boolean {
        return requestWithBody<DeletedResponse>(
            httpClient.delete("$baseUrl/v1/requests/$requestId") {
                bearerToken()
            }
        ).deleted
    }

    suspend fun deleteAdminRequest(requestId: String): Boolean {
        return requestWithBody<DeletedResponse>(
            httpClient.delete("$baseUrl/v1/requests/admin/$requestId") {
                bearerToken()
            }
        ).deleted
    }

    suspend fun myAttendance(daysBack: Int = 30): List<Attendance> {
        return requestWithBody<AttendanceResponse>(
            httpClient.get("$baseUrl/v1/attendance/me") {
                bearerToken()
                parameter("daysBack", daysBack)
            }
        ).attendance.flatMap { it.toAttendanceItems() }
    }

    suspend fun todayAttendance(): AttendanceDailyResponse? {
        return requestWithBody<TodayAttendanceResponse>(
            httpClient.get("$baseUrl/v1/attendance/today") {
                bearerToken()
            }
        ).attendance
    }

    suspend fun manualCheckIn(reason: String, note: String? = null): AttendanceDailyResponse {
        return requestWithBody<AttendanceActionResponse>(
            httpClient.post("$baseUrl/v1/attendance/manual-checkin") {
                bearerToken()
                contentType(ContentType.Application.Json)
                setBody(ManualCheckInBody(reason = reason, note = note))
            }
        ).attendance
    }

    suspend fun checkout(): AttendanceDailyResponse {
        return requestWithBody<AttendanceActionResponse>(
            httpClient.post("$baseUrl/v1/attendance/checkout") {
                bearerToken()
                contentType(ContentType.Application.Json)
                setBody(CheckoutBody())
            }
        ).attendance
    }

    suspend fun todayAttendanceStatus(): TodayAttendanceStatusResponse {
        return requestWithBody(
            httpClient.get("$baseUrl/v1/attendance/admin/status") {
                bearerToken()
            }
        )
    }

    suspend fun adminDashboard(): AdminDashboardResponse {
        return requestWithBody(
            httpClient.get("$baseUrl/v1/attendance/admin/dashboard") {
                bearerToken()
            }
        )
    }

    suspend fun activeNotices(): List<NoticeResponse> {
        return requestWithBody<ActiveNoticesResponse>(
            httpClient.get("$baseUrl/v1/notices/active") {
                bearerToken()
            }
        ).notices
    }

    suspend fun createNotice(
        title: String,
        message: String,
        priority: String = "NORMAL",
        audienceType: String = "ALL_EMPLOYEES",
        selectedEmployeeIds: List<String> = emptyList(),
        department: String? = null,
        isActive: Boolean = true
    ): NoticeResponse {
        return requestWithBody<NoticeEnvelopeResponse>(
            httpClient.post("$baseUrl/v1/admin/notices") {
                bearerToken()
                contentType(ContentType.Application.Json)
                setBody(
                    NoticeBody(
                        title = title,
                        message = message,
                        audienceType = audienceType,
                        selectedEmployeeIds = selectedEmployeeIds,
                        department = department?.takeIf { audienceType == "DEPARTMENT" },
                        priority = priority,
                        isActive = isActive
                    )
                )
            }
        ).notice
    }

    suspend fun updateNotice(
        noticeId: String,
        title: String,
        message: String,
        priority: String = "NORMAL",
        audienceType: String = "ALL_EMPLOYEES",
        selectedEmployeeIds: List<String> = emptyList(),
        department: String? = null,
        isActive: Boolean = true
    ): NoticeResponse {
        return requestWithBody<NoticeEnvelopeResponse>(
            httpClient.patch("$baseUrl/v1/admin/notices/$noticeId") {
                bearerToken()
                contentType(ContentType.Application.Json)
                setBody(
                    NoticeBody(
                        title = title,
                        message = message,
                        audienceType = audienceType,
                        selectedEmployeeIds = selectedEmployeeIds,
                        department = department?.takeIf { audienceType == "DEPARTMENT" },
                        priority = priority,
                        isActive = isActive
                    )
                )
            }
        ).notice
    }

    suspend fun adminNotices(): List<NoticeResponse> {
        return requestWithBody<AdminNoticesResponse>(
            httpClient.get("$baseUrl/v1/admin/notices") {
                bearerToken()
                parameter("limit", 50)
            }
        ).items
    }

    suspend fun deleteNotice(noticeId: String): Boolean {
        return requestWithBody<DeletedResponse>(
            httpClient.delete("$baseUrl/v1/admin/notices/$noticeId") {
                bearerToken()
            }
        ).deleted
    }

    suspend fun pullFirebaseAttendance(path: String? = null): FirebasePullResponse {
        return requestWithBody(
            httpClient.post("$baseUrl/v1/attendance/admin/sync-firebase/pull") {
                bearerToken()
                contentType(ContentType.Application.Json)
                setBody(FirebasePullBody(path = path?.takeIf { it.isNotBlank() }))
            }
        )
    }

    suspend fun employees(page: Int = 1, limit: Int = 100): EmployeesResponse {
        return requestWithBody(
            httpClient.get("$baseUrl/v1/admin/employees") {
                bearerToken()
                parameter("page", page)
                parameter("limit", limit)
            }
        )
    }

    suspend fun createEmployee(
        name: String,
        email: String,
        department: String,
        password: String,
        cardUid: String? = null
    ): User {
        return requestWithBody<UserResponse>(
            httpClient.post("$baseUrl/v1/admin/employees") {
                bearerToken()
                contentType(ContentType.Application.Json)
                setBody(
                    CreateEmployeeBody(
                        name = name,
                        email = email,
                        department = department,
                        password = password,
                        cardUid = cardUid?.takeIf { it.isNotBlank() }
                    )
                )
            }
        ).user
    }

    suspend fun updateEmployee(
        id: String,
        name: String,
        department: String,
        password: String? = null,
        cardUid: String? = null
    ): User {
        return requestWithBody<UserResponse>(
            httpClient.patch("$baseUrl/v1/admin/employees/$id") {
                bearerToken()
                contentType(ContentType.Application.Json)
                setBody(
                    UpdateEmployeeBody(
                        name = name,
                        department = department,
                        password = password?.takeIf { it.isNotBlank() },
                        cardUid = cardUid?.takeIf { it.isNotBlank() }
                    )
                )
            }
        ).user
    }

    suspend fun deleteEmployee(id: String): Boolean {
        return requestWithBody<DeletedResponse>(
            httpClient.delete("$baseUrl/v1/admin/employees/$id") {
                bearerToken()
            }
        ).deleted
    }

    suspend fun updateMe(name: String, department: String?, password: String? = null): User {
        return requestWithBody<UserResponse>(
            httpClient.patch("$baseUrl/v1/auth/me") {
                bearerToken()
                contentType(ContentType.Application.Json)
                setBody(UpdateProfileBody(name = name, department = department, password = password))
            }
        ).user
    }

    suspend fun employeeAttendance(employeeId: String, daysBack: Int = 30): List<Attendance> {
        return requestWithBody<AttendanceResponse>(
            httpClient.get("$baseUrl/v1/admin/employees/$employeeId/attendance") {
                bearerToken()
                parameter("daysBack", daysBack)
            }
        ).attendance.flatMap { it.toAttendanceItems() }
    }

    suspend fun projects(): List<Project> {
        return requestWithBody<ProjectsResponse>(
            httpClient.get("$baseUrl/v1/projects") {
                bearerToken()
            }
        ).projects
    }

    suspend fun myTasks(): TasksBundle {
        val tasks = requestWithBody<TasksResponse>(
            httpClient.get("$baseUrl/v1/tasks/me") {
                bearerToken()
            }
        ).tasks

        return TasksBundle(
            tasks = tasks.map { it.toTaskItem() },
            subtasksByTaskId = tasks.associate { task ->
                task.id to task.subtasks.map { it.toSubtaskItem() }
            }
        )
    }

    suspend fun createMyTask(
        title: String,
        dueDate: String,
        description: String? = null,
        priority: TaskPriority = TaskPriority.MEDIUM
    ): TasksBundle {
        val task = requestWithBody<TaskResponse>(
            httpClient.post("$baseUrl/v1/tasks/me") {
                bearerToken()
                contentType(ContentType.Application.Json)
                setBody(
                    CreateMyTaskBody(
                        title = title,
                        description = description,
                        priority = priority.name,
                        dueDate = dueDate
                    )
                )
            }
        ).task
        return task.toTaskBundle()
    }

    suspend fun createAdminTask(
        employeeIds: List<String>,
        title: String,
        dueDate: String,
        description: String? = null,
        priority: TaskPriority = TaskPriority.MEDIUM
    ): TasksBundle {
        val task = requestWithBody<TaskResponse>(
            httpClient.post("$baseUrl/v1/admin/tasks") {
                bearerToken()
                contentType(ContentType.Application.Json)
                setBody(
                    CreateAdminTaskBody(
                        assignedEmployeeIds = employeeIds,
                        title = title,
                        description = description,
                        priority = priority.name,
                        dueDate = dueDate
                    )
                )
            }
        ).task
        return task.toTaskBundle()
    }

    suspend fun adminTasks(): List<ApiTask> {
        return requestWithBody<AdminTasksResponse>(
            httpClient.get("$baseUrl/v1/admin/tasks") {
                bearerToken()
                parameter("limit", 50)
            }
        ).items
    }

    suspend fun updateTaskCompletion(taskId: String, isCompleted: Boolean): TasksBundle {
        val task = requestWithBody<TaskResponse>(
            httpClient.patch("$baseUrl/v1/tasks/$taskId") {
                bearerToken()
                contentType(ContentType.Application.Json)
                setBody(UpdateTaskBody(isCompleted = isCompleted))
            }
        ).task
        return task.toTaskBundle()
    }

    suspend fun deleteTask(taskId: String): Boolean {
        return requestWithBody<DeletedResponse>(
            httpClient.delete("$baseUrl/v1/tasks/$taskId") {
                bearerToken()
            }
        ).deleted
    }

    suspend fun addSubtask(taskId: String, title: String): TasksBundle {
        val task = requestWithBody<TaskResponse>(
            httpClient.post("$baseUrl/v1/tasks/$taskId/subtasks") {
                bearerToken()
                contentType(ContentType.Application.Json)
                setBody(CreateSubtaskBody(title = title))
            }
        ).task
        return task.toTaskBundle()
    }

    suspend fun toggleSubtask(taskId: String, subtaskId: String): TasksBundle {
        val task = requestWithBody<TaskResponse>(
            httpClient.patch("$baseUrl/v1/tasks/$taskId/subtasks/$subtaskId/toggle") {
                bearerToken()
            }
        ).task
        return task.toTaskBundle()
    }

    suspend fun deleteSubtask(taskId: String, subtaskId: String): TasksBundle {
        val task = requestWithBody<TaskResponse>(
            httpClient.delete("$baseUrl/v1/tasks/$taskId/subtasks/$subtaskId") {
                bearerToken()
            }
        ).task
        return task.toTaskBundle()
    }

    fun close() {
        httpClient.close()
    }

    private suspend fun reviewRequest(path: String, requestId: String, adminComment: String?): LeaveRequest {
        return requestWithBody<RequestResponse>(
            httpClient.post("$baseUrl/v1/requests/admin/$requestId/$path") {
                bearerToken()
                contentType(ContentType.Application.Json)
                setBody(ReviewRequestBody(adminComment = adminComment))
            }
        ).request
    }

    private fun io.ktor.client.request.HttpRequestBuilder.bearerToken() {
        val token = tokenProvider()
            ?: throw ApiException(message = "Please log in again", statusCode = 401)
        header("Authorization", "Bearer $token")
    }
}

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val user: User
)

@Serializable
data class UserResponse(
    val user: User
)

@Serializable
data class RequestsResponse(
    val requests: List<LeaveRequest>
)

@Serializable
data class RequestResponse(
    val request: LeaveRequest
)

@Serializable
data class AttendanceResponse(
    val attendance: List<AttendanceDailyResponse>
)

@Serializable
data class TodayAttendanceResponse(
    val date: String,
    val timezone: String,
    val attendance: AttendanceDailyResponse? = null
)

@Serializable
data class AttendanceActionResponse(
    val attendance: AttendanceDailyResponse,
    val duplicate: Boolean = false
)

@Serializable
data class AttendanceDailyResponse(
    val id: String,
    val employeeId: String,
    val date: String,
    val sessions: List<AttendanceSessionResponse> = emptyList(),
    val totalWorkedMinutes: Int = 0,
    val status: String,
    val flags: List<String> = emptyList(),
    val reviewStatus: String = "none"
)

@Serializable
data class AttendanceSessionResponse(
    val id: String? = null,
    val checkInAt: String,
    val checkOutAt: String? = null,
    val checkInSource: String,
    val checkOutSource: String? = null,
    val manualReason: String? = null,
    val manualNote: String? = null,
    val durationMinutes: Int = 0,
    val status: String
)

@Serializable
data class EmployeesResponse(
    val items: List<User>,
    val page: Int,
    val limit: Int,
    val total: Int
)

@Serializable
data class TodayAttendanceStatusResponse(
    val date: String,
    val employees: List<TodayAttendanceEmployee>
)

@Serializable
data class TodayAttendanceEmployee(
    val userId: String,
    val name: String,
    val email: String,
    val role: String,
    val department: String? = null,
    val status: String,
    val totalWorkedMinutes: Int = 0,
    val flags: List<String> = emptyList(),
    val checkInTime: String? = null,
    val checkOutTime: String? = null,
    val sessions: List<AttendanceSessionResponse> = emptyList()
)

@Serializable
data class AdminDashboardResponse(
    val date: String,
    val timezone: String,
    val metrics: AdminDashboardMetricsResponse
)

@Serializable
data class AdminDashboardMetricsResponse(
    val totalEmployees: Int = 0,
    val currentlyCheckedIn: Int = 0,
    val checkedOut: Int = 0,
    val manualPhoneCheckIns: Int = 0,
    val unknownCardEvents: Int = 0,
    val missingCheckoutAlerts: Int = 0,
    val todayAttendanceCount: Int = 0,
    val pendingReviewItems: Int = 0,
    val lateEmployees: Int = 0,
    val absentEmployees: Int = 0,
    val employeesOnLeave: Int = 0,
    val todayTotalWorkedMinutes: Int = 0,
    val activeNotices: Int = 0,
    val pendingTasks: Int = 0,
    val overdueTasks: Int = 0
)

@Serializable
data class ActiveNoticesResponse(
    val notices: List<NoticeResponse>
)

@Serializable
data class NoticeEnvelopeResponse(
    val notice: NoticeResponse
)

@Serializable
data class AdminNoticesResponse(
    val items: List<NoticeResponse>,
    val page: Int,
    val limit: Int,
    val total: Int
)

@Serializable
data class FirebasePullResponse(
    val path: String,
    val fetched: Int,
    val processed: Int,
    val attendance: Int = 0,
    val unknown: Int = 0,
    val duplicates: Int = 0,
    val skipped: Int = 0,
    val failed: Int = 0
)

@Serializable
data class NoticeResponse(
    val id: String,
    val title: String,
    val message: String,
    val audienceType: String,
    val priority: String,
    val isActive: Boolean,
    val startAt: String,
    val endAt: String? = null
)

data class TasksBundle(
    val tasks: List<TaskItem>,
    val subtasksByTaskId: Map<String, List<NetworkSubtaskItem>>
)

@Serializable
data class NetworkSubtaskItem(
    val id: String,
    val title: String,
    val isCompleted: Boolean
)

@Serializable
data class ProjectsResponse(
    val projects: List<Project>
)

@Serializable
data class TasksResponse(
    val tasks: List<ApiTask>
)

@Serializable
data class TaskResponse(
    val task: ApiTask
)

@Serializable
data class AdminTasksResponse(
    val items: List<ApiTask>,
    val page: Int,
    val limit: Int,
    val total: Int
)

@Serializable
data class ApiTask(
    val id: String,
    val userId: String,
    val assignedEmployeeIds: List<String> = emptyList(),
    val projectId: String,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean,
    val status: String = if (isCompleted) "completed" else "pending",
    val createdAt: String,
    val completedAt: String? = null,
    val priority: TaskPriority,
    val dueDate: String? = null,
    val subtasks: List<NetworkSubtaskItem> = emptyList()
)

@Serializable
data class DeletedResponse(
    val deleted: Boolean,
    val id: String? = null
)

@Serializable
private data class CreateLeaveRequestBody(
    val type: String,
    val leaveType: String,
    val startDate: String,
    val endDate: String? = null,
    val reason: String
)

@Serializable
private data class CreateLateArrivalRequestBody(
    val type: String,
    val startDate: String,
    val expectedTime: String,
    val reason: String
)

@Serializable
private data class ManualCheckInBody(
    val reason: String,
    val note: String? = null
)

@Serializable
private data class CheckoutBody(
    val source: String = "phone_manual"
)

@Serializable
private data class ReviewRequestBody(
    val adminComment: String? = null
)

@Serializable
private data class CreateEmployeeBody(
    val name: String,
    val email: String,
    val department: String,
    val password: String,
    val cardUid: String? = null
)

@Serializable
private data class UpdateEmployeeBody(
    val name: String,
    val department: String,
    val password: String? = null,
    val cardUid: String? = null
)

@Serializable
private data class UpdateProfileBody(
    val name: String,
    val department: String? = null,
    val password: String? = null
)

@Serializable
private data class UpdateTaskBody(
    val isCompleted: Boolean
)

@Serializable
private data class CreateSubtaskBody(
    val title: String
)

@Serializable
private data class CreateMyTaskBody(
    val title: String,
    val description: String? = null,
    val priority: String,
    val dueDate: String
)

@Serializable
private data class CreateAdminTaskBody(
    val assignedEmployeeIds: List<String>,
    val title: String,
    val description: String? = null,
    val priority: String,
    val dueDate: String
)

@Serializable
private data class NoticeBody(
    val title: String,
    val message: String,
    val audienceType: String = "ALL_EMPLOYEES",
    val selectedEmployeeIds: List<String> = emptyList(),
    val department: String? = null,
    val priority: String = "NORMAL",
    val isActive: Boolean = true
)

@Serializable
private data class FirebasePullBody(
    val path: String? = null
)

class ApiException(
    override val message: String,
    val statusCode: Int
) : Exception(message)

@Serializable
private data class ApiErrorEnvelope(
    val error: ApiError? = null
)

@Serializable
private data class ApiError(
    val code: String? = null,
    val message: String? = null,
    val details: JsonObject? = null
)

private fun defaultHttpClient(): HttpClient = HttpClient {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
            }
        )
    }
}

private suspend fun HttpResponse.errorMessage(): String {
    return try {
        body<ApiErrorEnvelope>().error?.message ?: "Request failed with status ${status.value}"
    } catch (_: Exception) {
        "Request failed with status ${status.value}"
    }
}

private suspend inline fun <reified T> requestWithBody(response: HttpResponse): T {
    if (response.status.value in 200..299) {
        return response.body()
    }

    throw ApiException(message = response.errorMessage(), statusCode = response.status.value)
}

private fun ApiTask.toTaskItem(): TaskItem {
    return TaskItem(
        id = id,
        userId = userId,
        projectId = projectId,
        title = title,
        description = description,
        isCompleted = isCompleted,
        createdAt = Instant.parse(createdAt),
        completedAt = completedAt?.let { Instant.parse(it) },
        priority = priority,
        dueDate = dueDate?.let { LocalDate.parse(it) }
    )
}

private fun ApiTask.toTaskBundle(): TasksBundle {
    return TasksBundle(
        tasks = listOf(toTaskItem()),
        subtasksByTaskId = mapOf(id to subtasks.map { it.toSubtaskItem() })
    )
}

private fun NetworkSubtaskItem.toSubtaskItem(): NetworkSubtaskItem = this

fun AttendanceDailyResponse.toLatestAttendanceItem(): Attendance? {
    val session = sessions.lastOrNull() ?: return null
    return Attendance(
        id = session.id ?: id,
        userId = employeeId,
        checkInTime = Instant.parse(session.checkInAt),
        checkOutTime = session.checkOutAt?.let { Instant.parse(it) },
        date = LocalDate.parse(date)
    )
}

private fun AttendanceDailyResponse.toAttendanceItems(): List<Attendance> {
    return sessions.mapNotNull { session ->
        runCatching {
            Attendance(
                id = session.id ?: id,
                userId = employeeId,
                checkInTime = Instant.parse(session.checkInAt),
                checkOutTime = session.checkOutAt?.let { Instant.parse(it) },
                date = LocalDate.parse(date)
            )
        }.getOrNull()
    }
}
