package com.testsolz.features.employee.employeehome.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.testsolz.core.network.NoticeResponse
import com.testsolz.core.network.TasksBundle
import com.testsolz.core.network.TestSolzApiClient
import com.testsolz.core.network.toLatestAttendanceItem
import com.testsolz.domain.models.Attendance
import com.testsolz.domain.models.Project
import com.testsolz.domain.models.TaskItem
import com.testsolz.domain.models.TaskPriority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

/**
 * Employee Home ViewModel
 */
class EmployeeHomeViewModel : ViewModel() {
    private val apiClient = TestSolzApiClient()

    private val _todayAttendance = MutableStateFlow<Attendance?>(null)
    val todayAttendance: StateFlow<Attendance?> = _todayAttendance.asStateFlow()

    private val _tasks = MutableStateFlow<List<TaskItem>>(emptyList())
    val tasks: StateFlow<List<TaskItem>> = _tasks.asStateFlow()

    private val _selectedTaskDay = MutableStateFlow(TaskDay.TODAY)
    val selectedTaskDay: StateFlow<TaskDay> = _selectedTaskDay.asStateFlow()

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _notices = MutableStateFlow<List<NoticeResponse>>(emptyList())
    val notices: StateFlow<List<NoticeResponse>> = _notices.asStateFlow()

    private val _subtasksByTaskId = MutableStateFlow<Map<String, List<SubtaskItem>>>(emptyMap())
    val subtasksByTaskId: StateFlow<Map<String, List<SubtaskItem>>> = _subtasksByTaskId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                _todayAttendance.value = apiClient.todayAttendance()?.toLatestAttendanceItem()
                _notices.value = apiClient.activeNotices()
                _projects.value = apiClient.projects()
                applyTasksBundle(apiClient.myTasks())
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to load tasks"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectTaskDay(day: TaskDay) {
        _selectedTaskDay.value = day
    }

    fun tasksForSelectedDay(): List<TaskItem> {
        val targetDate = _selectedTaskDay.value.date()
        return _tasks.value.filter { task ->
            (task.dueDate ?: task.createdAt.toLocalDateTime(TimeZone.currentSystemDefault()).date) == targetDate
        }
    }

    fun addTask(title: String, description: String?, priority: TaskPriority, day: TaskDay) {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return

        viewModelScope.launch {
            _errorMessage.value = null
            try {
                apiClient.createMyTask(
                    title = trimmed,
                    description = description?.trim()?.takeIf { it.isNotBlank() },
                    priority = priority,
                    dueDate = day.date().toString()
                )
                applyTasksBundle(apiClient.myTasks())
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to add task"
            }
        }
    }

    fun checkIn(reason: String, note: String? = null) {
        viewModelScope.launch {
            _errorMessage.value = null
            try {
                _todayAttendance.value = apiClient.manualCheckIn(reason = reason, note = note).toLatestAttendanceItem()
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to check in"
            }
        }
    }

    fun checkOut() {
        viewModelScope.launch {
            _errorMessage.value = null
            try {
                _todayAttendance.value = apiClient.checkout().toLatestAttendanceItem()
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to check out"
            }
        }
    }

    fun toggleTask(taskId: String) {
        viewModelScope.launch {
            val task = _tasks.value.firstOrNull { it.id == taskId } ?: return@launch
            try {
                apiClient.updateTaskCompletion(taskId, !task.isCompleted)
                applyTasksBundle(apiClient.myTasks())
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to update task"
            }
        }
    }

    fun markTaskDone(taskId: String) {
        viewModelScope.launch {
            try {
                apiClient.updateTaskCompletion(taskId, true)
                applyTasksBundle(apiClient.myTasks())
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to update task"
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                if (apiClient.deleteTask(taskId)) applyTasksBundle(apiClient.myTasks())
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to delete task"
            }
        }
    }

    fun addSubtask(taskId: String, title: String) {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return

        viewModelScope.launch {
            try {
                apiClient.addSubtask(taskId, trimmed)
                applyTasksBundle(apiClient.myTasks())
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to add subtask"
            }
        }
    }

    fun toggleSubtask(taskId: String, subtaskId: String) {
        viewModelScope.launch {
            try {
                apiClient.toggleSubtask(taskId, subtaskId)
                applyTasksBundle(apiClient.myTasks())
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to update subtask"
            }
        }
    }

    fun deleteSubtask(taskId: String, subtaskId: String) {
        viewModelScope.launch {
            try {
                apiClient.deleteSubtask(taskId, subtaskId)
                applyTasksBundle(apiClient.myTasks())
            } catch (error: Exception) {
                _errorMessage.value = error.message ?: "Failed to delete subtask"
            }
        }
    }

    private fun applyTasksBundle(bundle: TasksBundle) {
        _tasks.value = bundle.tasks
        _subtasksByTaskId.value = bundle.subtasksByTaskId.mapValues { (_, subtasks) ->
            subtasks.map { SubtaskItem(id = it.id, title = it.title, isCompleted = it.isCompleted) }
        }
    }

    private fun mergeTasksBundle(bundle: TasksBundle) {
        val updatedTask = bundle.tasks.firstOrNull() ?: return
        _tasks.value = _tasks.value.map { if (it.id == updatedTask.id) updatedTask else it }
        _subtasksByTaskId.value = _subtasksByTaskId.value + bundle.subtasksByTaskId.mapValues { (_, subtasks) ->
            subtasks.map { SubtaskItem(id = it.id, title = it.title, isCompleted = it.isCompleted) }
        }
    }

    override fun onCleared() {
        apiClient.close()
        super.onCleared()
    }
}

data class SubtaskItem(
    val id: String,
    val title: String,
    val isCompleted: Boolean
)

enum class TaskDay(val label: String) {
    YESTERDAY("Yesterday"),
    TODAY("Today"),
    NEXT_DAY("Next Day");

    fun date(): LocalDate {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        return when (this) {
            YESTERDAY -> today.minus(DatePeriod(days = 1))
            TODAY -> today
            NEXT_DAY -> today.plus(DatePeriod(days = 1))
        }
    }
}
