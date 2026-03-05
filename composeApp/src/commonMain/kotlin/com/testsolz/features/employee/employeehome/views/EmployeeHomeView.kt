package com.testsolz.features.employee.employeehome.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.testsolz.designsystem.theme.*
import com.testsolz.domain.models.formatted
import com.testsolz.features.employee.employeehome.viewmodels.EmployeeHomeViewModel
import com.testsolz.shared.components.buttons.PrimaryButton
import com.testsolz.shared.components.buttons.SecondaryButton
import com.testsolz.shared.components.cards.BaseCard
import com.testsolz.shared.components.cards.TaskCard

/**
 * Employee Home View
 * Main employee screen with check-in and tasks
 */
@Composable
fun EmployeeHomeView(
    viewModel: EmployeeHomeViewModel = viewModel()
) {
    val todayAttendance by viewModel.todayAttendance.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val subtasksByTaskId by viewModel.subtasksByTaskId.collectAsState()

    var selectedTaskId by remember { mutableStateOf<String?>(null) }
    val selectedTask = selectedTaskId?.let { id -> tasks.firstOrNull { it.id == id } }

    LaunchedEffect(selectedTaskId, tasks) {
        if (selectedTaskId != null && selectedTask == null) {
            selectedTaskId = null
        }
    }

    if (selectedTask != null) {
        val project = projects.firstOrNull { it.id == selectedTask.projectId }
        TaskDetailView(
            task = selectedTask,
            project = project,
            subtasks = subtasksByTaskId[selectedTask.id].orEmpty(),
            onBack = { selectedTaskId = null },
            onToggleSubtask = { subtaskId -> viewModel.toggleSubtask(selectedTask.id, subtaskId) },
            onDeleteSubtask = { subtaskId -> viewModel.deleteSubtask(selectedTask.id, subtaskId) },
            onAddSubtask = { title -> viewModel.addSubtask(selectedTask.id, title) },
            onMarkTaskDone = { viewModel.markTaskDone(selectedTask.id) },
            onDeleteTask = {
                viewModel.deleteTask(selectedTask.id)
                selectedTaskId = null
            }
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.background)
            .padding(PaddingPresets.screen),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        // Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = "Welcome Back!",
                    style = AppTypography.headlineLarge
                )
                Text(
                    text = "Track your attendance and manage tasks",
                    style = AppTypography.bodyMedium,
                    color = ColorPalette.textSecondary
                )
            }
        }

        // Check-in/Check-out Section
        item {
            BaseCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Text(
                        text = "Today's Attendance",
                        style = AppTypography.titleMedium
                    )

                    if (todayAttendance == null) {
                        // Not checked in
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                            Text(
                                text = "You haven't checked in yet",
                                style = AppTypography.bodyMedium,
                                color = ColorPalette.textSecondary
                            )
                            PrimaryButton(
                                text = "Check In",
                                onClick = { viewModel.checkIn() }
                            )
                        }
                    } else {
                        // Checked in
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Check-in Time",
                                        style = AppTypography.labelSmall,
                                        color = ColorPalette.textSecondary
                                    )
                                    Text(
                                        text = todayAttendance!!.checkInTime.formatted("time"),
                                        style = AppTypography.titleMedium,
                                        color = ColorPalette.success
                                    )
                                }

                                if (todayAttendance!!.checkOutTime != null) {
                                    Column {
                                        Text(
                                            text = "Check-out Time",
                                            style = AppTypography.labelSmall,
                                            color = ColorPalette.textSecondary
                                        )
                                        Text(
                                            text = todayAttendance!!.checkOutTime!!.formatted("time"),
                                            style = AppTypography.titleMedium
                                        )
                                    }
                                }
                            }

                            if (todayAttendance!!.checkOutTime == null) {
                                SecondaryButton(
                                    text = "Check Out",
                                    onClick = { viewModel.checkOut() }
                                )
                            } else {
                                // Show hours worked
                                val hoursWorked = todayAttendance!!.hoursWorked ?: 0.0
                                val roundedHours = (hoursWorked * 10).toInt() / 10.0
                                Text(
                                    text = "Hours Worked: $roundedHours hrs",
                                    style = AppTypography.titleSmall,
                                    color = ColorPalette.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Today's Tasks Section
        item {
            Text(
                text = "Today's Tasks",
                style = AppTypography.headlineMedium
            )
        }

        if (tasks.isEmpty()) {
            item {
                BaseCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Text(
                            text = "No tasks yet",
                            style = AppTypography.bodyMedium,
                            color = ColorPalette.textSecondary
                        )
                    }
                }
            }
        } else {
            val projectsById = projects.associateBy { it.id }
            val groupedTasks = tasks.groupBy { it.projectId }
            val orderedProjectIds = (projects.map { it.id } + groupedTasks.keys)
                .distinct()

            orderedProjectIds.forEach { projectId ->
                val projectTasks = groupedTasks[projectId] ?: return@forEach
                val project = projectsById[projectId]

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        if (project != null) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(project.displayColor)
                            )
                        }
                        Text(
                            text = project?.name ?: "Project",
                            style = AppTypography.labelMedium,
                            color = ColorPalette.textSecondary
                        )
                    }
                }

                items(projectTasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onToggle = { viewModel.toggleTask(task.id) },
                        onClick = { selectedTaskId = task.id }
                    )
                }
            }
        }

        // Bottom spacing for tab bar
        item {
            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}
