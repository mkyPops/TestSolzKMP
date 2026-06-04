package com.testsolz.features.employee.employeehome.views

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.testsolz.core.network.NoticeResponse
import com.testsolz.designsystem.theme.*
import com.testsolz.domain.models.TaskPriority
import com.testsolz.domain.models.formatted
import com.testsolz.features.employee.employeehome.viewmodels.EmployeeHomeViewModel
import com.testsolz.features.employee.employeehome.viewmodels.TaskDay
import com.testsolz.shared.components.buttons.PrimaryButton
import com.testsolz.shared.components.buttons.SecondaryButton
import com.testsolz.shared.components.cards.BaseCard
import com.testsolz.shared.components.cards.TaskCard
import com.testsolz.shared.components.input.CustomTextField
import kotlinx.coroutines.delay
import kotlinx.datetime.toLocalDateTime

/**
 * Employee Home View
 * Main employee screen with check-in and tasks
 */
@Composable
fun EmployeeHomeView(
    sessionKey: String,
    viewModel: EmployeeHomeViewModel = viewModel(key = "employee-home-$sessionKey")
) {
    val todayAttendance by viewModel.todayAttendance.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val notices by viewModel.notices.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val subtasksByTaskId by viewModel.subtasksByTaskId.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val selectedTaskDay by viewModel.selectedTaskDay.collectAsState()
    val visibleTasks = viewModel.tasksForSelectedDay()

    var selectedTaskId by remember { mutableStateOf<String?>(null) }
    var showAddTask by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskDescription by remember { mutableStateOf("") }
    var newTaskPriority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var manualReason by remember { mutableStateOf("Forgot card") }
    var manualNote by remember { mutableStateOf("") }
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
            .background(ColorPalette.backgroundSecondary)
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

        item {
            NoticeCarousel(
                notices = notices,
                onRefresh = { viewModel.loadData() }
            )
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
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                            Text(
                                text = "Manual phone check-in requires a reason",
                                style = AppTypography.bodyMedium,
                                color = ColorPalette.textSecondary
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                                listOf("Forgot card", "Card damaged", "Card not working").forEach { reason ->
                                    ReasonChip(
                                        text = reason,
                                        selected = manualReason == reason,
                                        onClick = { manualReason = reason },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            CustomTextField(
                                value = manualNote,
                                onValueChange = { manualNote = it },
                                label = "Note",
                                placeholder = "Optional note for admin"
                            )
                            PrimaryButton(
                                text = "Manual Check In",
                                onClick = {
                                    viewModel.checkIn(
                                        reason = manualReason,
                                        note = manualNote.takeIf { it.isNotBlank() }
                                    )
                                }
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

        item {
            WorkBoardSummary(tasks = tasks)

            Spacer(modifier = Modifier.height(Spacing.md))

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Work Board",
                            style = AppTypography.headlineMedium
                        )
                        Text(
                            text = "${tasks.count { !it.isCompleted }} open / ${tasks.size} total",
                            style = AppTypography.bodySmall,
                            color = ColorPalette.textSecondary
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        SecondaryButton(
                            text = "Refresh",
                            onClick = { viewModel.loadData() },
                            modifier = Modifier.width(104.dp)
                        )
                        PrimaryButton(
                            text = if (showAddTask) "Close" else "Add Task",
                            onClick = { showAddTask = !showAddTask },
                            modifier = Modifier.width(112.dp)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    TaskDay.entries.forEach { day ->
                        TaskDayChip(
                            day = day,
                            count = tasks.count { task ->
                                (task.dueDate ?: task.createdAt.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date) == day.date()
                            },
                            selected = selectedTaskDay == day,
                            onClick = { viewModel.selectTaskDay(day) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        if (showAddTask) {
            item {
                BaseCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        Text(text = "Add ${selectedTaskDay.label} Task", style = AppTypography.titleMedium)

                        CustomTextField(
                            value = newTaskTitle,
                            onValueChange = { newTaskTitle = it },
                            label = "Task title",
                            placeholder = "What needs to be done?"
                        )

                        CustomTextField(
                            value = newTaskDescription,
                            onValueChange = { newTaskDescription = it },
                            label = "Details",
                            placeholder = "Optional context"
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                            TaskPriority.entries.forEach { priority ->
                                PriorityChip(
                                    priority = priority,
                                    selected = newTaskPriority == priority,
                                    onClick = { newTaskPriority = priority },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        PrimaryButton(
                            text = "Create Task",
                            enabled = newTaskTitle.isNotBlank(),
                            onClick = {
                                viewModel.addTask(
                                    title = newTaskTitle,
                                    description = newTaskDescription,
                                    priority = newTaskPriority,
                                    day = selectedTaskDay
                                )
                                newTaskTitle = ""
                                newTaskDescription = ""
                                newTaskPriority = TaskPriority.MEDIUM
                                showAddTask = false
                            }
                        )
                    }
                }
            }
        }

        if (errorMessage != null) {
            item {
                BaseCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = errorMessage!!,
                        style = AppTypography.bodyMedium,
                        color = ColorPalette.error
                    )
                }
            }
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator(color = ColorPalette.primary)
                }
            }
        } else if (visibleTasks.isEmpty()) {
            item {
                BaseCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Text(
                            text = "No ${selectedTaskDay.label.lowercase()} tasks",
                            style = AppTypography.bodyMedium,
                            color = ColorPalette.textSecondary
                        )
                    }
                }
            }
        } else {
            val projectsById = projects.associateBy { it.id }
            val groupedTasks = visibleTasks.groupBy { it.projectId }
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
                        onClick = { selectedTaskId = task.id },
                        onDelete = { viewModel.deleteTask(task.id) }
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

@Composable
private fun WorkBoardSummary(tasks: List<com.testsolz.domain.models.TaskItem>) {
    val pending = tasks.count { !it.isCompleted }
    val completed = tasks.count { it.isCompleted }
    val highPriority = tasks.count { !it.isCompleted && it.priority == TaskPriority.HIGH }

    BaseCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
            Text(text = "Work Focus", style = AppTypography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                WorkStat("Open", pending.toString(), ColorPalette.primary, Modifier.weight(1f))
                WorkStat("Done", completed.toString(), ColorPalette.success, Modifier.weight(1f))
                WorkStat("High", highPriority.toString(), ColorPalette.warning, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun WorkStat(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(Shapes.card)
            .background(color.copy(alpha = 0.10f))
            .padding(Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs)
    ) {
        Text(text = label, style = AppTypography.labelSmall, color = ColorPalette.textSecondary)
        Text(text = value, style = AppTypography.titleLarge, color = color)
    }
}

@Composable
private fun NoticeCarousel(
    notices: List<NoticeResponse>,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    var index by remember(notices) { mutableStateOf(0) }

    LaunchedEffect(notices) {
        if (notices.size <= 1) return@LaunchedEffect
        while (true) {
            delay(3500)
            index = (index + 1) % notices.size
        }
    }

    if (notices.isEmpty()) {
        BaseCard(modifier = modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = "No active notices",
                    style = AppTypography.titleSmall,
                    color = ColorPalette.textPrimary
                )
                Text(
                    text = "Company updates will appear here.",
                    style = AppTypography.bodySmall,
                    color = ColorPalette.textSecondary
                )
            }
        }
        return
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            SecondaryButton(
                text = "Refresh",
                onClick = onRefresh,
                modifier = Modifier.weight(1f)
            )
            SecondaryButton(
                text = "Prev",
                onClick = { index = if (index == 0) notices.lastIndex else index - 1 },
                modifier = Modifier.weight(1f)
            )
            SecondaryButton(
                text = "Next",
                onClick = { index = (index + 1) % notices.size },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            notices.forEachIndexed { noticeIndex, notice ->
                NoticeCard(
                    notice = notice,
                    selected = noticeIndex == index,
                    modifier = Modifier.width(300.dp)
                )
            }
        }
    }
}

@Composable
private fun NoticeCard(
    notice: NoticeResponse,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    BaseCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = notice.title,
                    style = AppTypography.titleMedium,
                    color = ColorPalette.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = notice.priority.lowercase().replaceFirstChar { it.uppercase() },
                    style = AppTypography.labelSmall,
                    color = notice.priorityColor()
                )
            }
            Text(
                text = notice.message,
                style = AppTypography.bodyMedium,
                color = ColorPalette.textSecondary
            )
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .fillMaxWidth()
                    .clip(Shapes.badge)
                    .background(if (selected) ColorPalette.primary else ColorPalette.border)
            )
        }
    }
}

@Composable
private fun NoticeResponse.priorityColor() = when (priority) {
    "URGENT" -> ColorPalette.error
    "IMPORTANT" -> ColorPalette.warning
    else -> ColorPalette.primary
}

@Composable
private fun ReasonChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BaseCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Text(
            text = text,
            style = AppTypography.labelSmall,
            color = if (selected) ColorPalette.primary else ColorPalette.textSecondary
        )
    }
}

@Composable
private fun TaskDayChip(
    day: TaskDay,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BaseCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxxs)) {
            Text(
                text = day.label,
                style = AppTypography.labelSmall,
                color = if (selected) ColorPalette.primary else ColorPalette.textSecondary
            )
            Text(
                text = count.toString(),
                style = AppTypography.titleMedium,
                color = if (selected) ColorPalette.primary else ColorPalette.textPrimary
            )
        }
    }
}

@Composable
private fun PriorityChip(
    priority: TaskPriority,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BaseCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Text(
            text = priority.displayName,
            style = AppTypography.labelSmall,
            color = if (selected) priority.color else ColorPalette.textSecondary
        )
    }
}
