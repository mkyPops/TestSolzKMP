package com.testsolz.features.employee.employeehome.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.testsolz.domain.models.Attendance
import com.testsolz.domain.models.Project
import com.testsolz.domain.models.TaskItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

/**
 * Employee Home ViewModel
 */
class EmployeeHomeViewModel : ViewModel() {

    private val _todayAttendance = MutableStateFlow<Attendance?>(null)
    val todayAttendance: StateFlow<Attendance?> = _todayAttendance.asStateFlow()

    private val _tasks = MutableStateFlow<List<TaskItem>>(emptyList())
    val tasks: StateFlow<List<TaskItem>> = _tasks.asStateFlow()

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _subtasksByTaskId = MutableStateFlow<Map<String, List<SubtaskItem>>>(emptyMap())
    val subtasksByTaskId: StateFlow<Map<String, List<SubtaskItem>>> = _subtasksByTaskId.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _tasks.value = TaskItem.mockTasks
            _projects.value = Project.mockProjects
            _subtasksByTaskId.value = TaskItem.mockTasks.associate { task ->
                task.id to listOf(
                    SubtaskItem(id = "${task.id}-1", title = "Review requirements", isCompleted = false),
                    SubtaskItem(id = "${task.id}-2", title = "Send update to team", isCompleted = false)
                )
            }
        }
    }

    fun checkIn() {
        viewModelScope.launch {
            val now = Clock.System.now()
            _todayAttendance.value = Attendance(
                id = generateId(),
                userId = "1",
                checkInTime = now,
                checkOutTime = null,
                date = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
            )
        }
    }

    fun checkOut() {
        viewModelScope.launch {
            val current = _todayAttendance.value
            if (current != null) {
                _todayAttendance.value = current.copy(
                    checkOutTime = Clock.System.now()
                )
            }
        }
    }

    fun toggleTask(taskId: String) {
        viewModelScope.launch {
            _tasks.value = _tasks.value.map { task ->
                if (task.id == taskId) {
                    task.copy(
                        isCompleted = !task.isCompleted,
                        completedAt = if (!task.isCompleted) Clock.System.now() else null
                    )
                } else task
            }
        }
    }

    fun markTaskDone(taskId: String) {
        viewModelScope.launch {
            _tasks.value = _tasks.value.map { task ->
                if (task.id == taskId && !task.isCompleted) {
                    task.copy(
                        isCompleted = true,
                        completedAt = Clock.System.now()
                    )
                } else task
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            _tasks.value = _tasks.value.filterNot { it.id == taskId }
            _subtasksByTaskId.value = _subtasksByTaskId.value - taskId
        }
    }

    fun addSubtask(taskId: String, title: String) {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return

        viewModelScope.launch {
            val current = _subtasksByTaskId.value[taskId].orEmpty()
            val newSubtask = SubtaskItem(
                id = generateId(),
                title = trimmed,
                isCompleted = false
            )
            _subtasksByTaskId.value = _subtasksByTaskId.value + (taskId to (current + newSubtask))
        }
    }

    fun toggleSubtask(taskId: String, subtaskId: String) {
        viewModelScope.launch {
            val updated = _subtasksByTaskId.value[taskId]
                ?.map { st -> if (st.id == subtaskId) st.copy(isCompleted = !st.isCompleted) else st }
                ?: return@launch

            _subtasksByTaskId.value = _subtasksByTaskId.value + (taskId to updated)
        }
    }

    fun deleteSubtask(taskId: String, subtaskId: String) {
        viewModelScope.launch {
            val updated = _subtasksByTaskId.value[taskId]?.filterNot { it.id == subtaskId } ?: return@launch
            _subtasksByTaskId.value = _subtasksByTaskId.value + (taskId to updated)
        }
    }
}

data class SubtaskItem(
    val id: String,
    val title: String,
    val isCompleted: Boolean
)

/**
 * KMP-safe ID generator
 */
private fun generateId(): String =
    Random.nextLong().toString()