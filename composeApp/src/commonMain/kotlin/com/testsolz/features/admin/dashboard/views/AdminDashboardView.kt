package com.testsolz.features.admin.dashboard.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.testsolz.core.network.ApiTask
import com.testsolz.core.network.NoticeResponse
import com.testsolz.designsystem.theme.*
import com.testsolz.domain.models.TaskPriority
import com.testsolz.features.admin.dashboard.viewmodels.AdminDashboardViewModel
import com.testsolz.features.admin.dashboard.viewmodels.DashboardMetrics
import com.testsolz.features.admin.dashboard.viewmodels.EmployeeInfo
import com.testsolz.shared.components.buttons.PrimaryButton
import com.testsolz.shared.components.buttons.SecondaryButton
import com.testsolz.shared.components.cards.BaseCard
import com.testsolz.shared.components.input.CustomTextField
import kotlinx.datetime.toLocalDateTime

private enum class AdminPanelPage { DASHBOARD, NOTICES, NOTICE_FORM, ATTENDANCE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardView(
    sessionKey: String,
    viewModel: AdminDashboardViewModel = viewModel(key = "admin-dashboard-$sessionKey")
) {
    val metrics by viewModel.metrics.collectAsState()
    val employees by viewModel.allEmployees.collectAsState()
    val assignedTasks by viewModel.assignedTasks.collectAsState()
    val notices by viewModel.adminNotices.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var page by remember { mutableStateOf(AdminPanelPage.DASHBOARD) }
    var editingNotice by remember { mutableStateOf<NoticeResponse?>(null) }
    var showAssignSheet by remember { mutableStateOf(false) }
    var showTaskStatusSheet by remember { mutableStateOf(false) }

    when (page) {
        AdminPanelPage.NOTICES -> NoticesPage(
            notices = notices,
            isLoading = isLoading,
            onBack = { page = AdminPanelPage.DASHBOARD },
            onRefresh = { viewModel.loadDashboard() },
            onCreate = {
                editingNotice = null
                page = AdminPanelPage.NOTICE_FORM
            },
            onEdit = {
                editingNotice = it
                page = AdminPanelPage.NOTICE_FORM
            },
            onDelete = { viewModel.deleteNotice(it.id) }
        )

        AdminPanelPage.NOTICE_FORM -> NoticeFormPage(
            notice = editingNotice,
            employees = employees,
            onBack = { page = AdminPanelPage.NOTICES },
            onSave = { id, title, message, priority, audience, department, isActive ->
                viewModel.saveNotice(
                    noticeId = id,
                    title = title,
                    message = message,
                    priority = priority,
                    audienceType = audience,
                    department = department,
                    isActive = isActive,
                    onSuccess = { page = AdminPanelPage.NOTICES }
                )
            }
        )

        AdminPanelPage.ATTENDANCE -> AttendancePage(
            metrics = metrics,
            employees = employees,
            isLoading = isLoading,
            onBack = { page = AdminPanelPage.DASHBOARD },
            onRefresh = { viewModel.loadDashboard() }
        )

        AdminPanelPage.DASHBOARD -> DashboardPage(
            metrics = metrics,
            employees = employees,
            tasks = assignedTasks,
            notices = notices,
            isLoading = isLoading,
            message = errorMessage ?: successMessage,
            isError = errorMessage != null,
            onRefresh = { viewModel.loadDashboard() },
            onOpenNotices = { page = AdminPanelPage.NOTICES },
            onOpenAssignTask = { showAssignSheet = true },
            onOpenAttendance = { page = AdminPanelPage.ATTENDANCE },
            onOpenEmployees = { showAssignSheet = true },
            onOpenTasks = { showTaskStatusSheet = true }
        )
    }

    if (showAssignSheet) {
        ModalBottomSheet(onDismissRequest = { showAssignSheet = false }) {
            AssignTaskSheet(
                employees = employees,
                defaultDueDate = viewModel.defaultTaskDueDate(),
                onClose = { showAssignSheet = false },
                onAssign = { employeeIds, title, description, dueDate, priority ->
                    viewModel.assignTask(
                        employeeIds = employeeIds,
                        title = title,
                        description = description,
                        dueDate = dueDate,
                        priority = priority,
                        onSuccess = { showAssignSheet = false }
                    )
                }
            )
        }
    }

    if (showTaskStatusSheet) {
        ModalBottomSheet(onDismissRequest = { showTaskStatusSheet = false }) {
            TaskStatusSheet(
                tasks = assignedTasks,
                employees = employees,
                onClose = { showTaskStatusSheet = false },
                onRefresh = { viewModel.loadDashboard() }
            )
        }
    }
}

@Composable
private fun DashboardPage(
    metrics: DashboardMetrics,
    employees: List<EmployeeInfo>,
    tasks: List<ApiTask>,
    notices: List<NoticeResponse>,
    isLoading: Boolean,
    message: String?,
    isError: Boolean,
    onRefresh: () -> Unit,
    onOpenNotices: () -> Unit,
    onOpenAssignTask: () -> Unit,
    onOpenAttendance: () -> Unit,
    onOpenEmployees: () -> Unit,
    onOpenTasks: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(ColorPalette.backgroundSecondary).padding(PaddingPresets.screen),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        item {
            DashboardHeader(onRefresh = onRefresh, onNotifications = onOpenNotices)
        }

        if (message != null) {
            item {
                InlineMessage(message = message, isError = isError)
            }
        }

        if (isLoading) {
            item { LoadingBlock() }
        }

        item {
            TodayOverview(metrics)
        }

        item {
            ActionGrid(
                onOpenNotices = onOpenNotices,
                onOpenAssignTask = onOpenAssignTask,
                onOpenAttendance = onOpenAttendance,
                onOpenEmployees = onOpenEmployees,
                onOpenTasks = onOpenTasks
            )
        }

        item {
            CompactInfoCard(
                icon = "↗",
                iconColor = ColorPalette.success,
                title = "Top Attendance Today",
                body = topAttendanceText(employees),
                onClick = onOpenAttendance
            )
        }

        item {
            CompactInfoCard(
                icon = "✓",
                iconColor = ColorPalette.accentPurple,
                title = "Assigned Tasks",
                body = if (tasks.isEmpty()) "No assigned tasks yet." else "${tasks.size} assigned tasks tracked.",
                trailing = "Refresh",
                onClick = onOpenTasks
            )
        }

        if (notices.isNotEmpty()) {
            item {
                CompactInfoCard(
                    icon = "!",
                    iconColor = ColorPalette.accentBlue,
                    title = "Latest Notice",
                    body = notices.first().title,
                    onClick = onOpenNotices
                )
            }
        }

        item { Spacer(Modifier.height(Spacing.xl)) }
    }
}

@Composable
private fun DashboardHeader(onRefresh: () -> Unit, onNotifications: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
            Text("Welcome back,", style = AppTypography.titleMedium, color = ColorPalette.textPrimary)
            Text("Admin", style = AppTypography.headlineLarge, color = ColorPalette.textPrimary)
            Text("Here's what's happening today.", style = AppTypography.bodyMedium, color = ColorPalette.textSecondary)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalAlignment = Alignment.CenterVertically) {
            SoftButton(text = "Refresh", onClick = onRefresh)
            NotificationButton(onClick = onNotifications)
        }
    }
}

@Composable
private fun TodayOverview(metrics: DashboardMetrics) {
    BaseCard(Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.md), modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Today Overview", style = AppTypography.titleLarge, color = ColorPalette.textPrimary)
                Text(currentDateLabel(), style = AppTypography.bodySmall, color = ColorPalette.textSecondary)
            }
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), modifier = Modifier.fillMaxWidth()) {
                    SummaryCard("Present", metrics.presentToday, ColorPalette.success, "✓", Modifier.weight(1f))
                    SummaryCard("Absent", metrics.absentToday, ColorPalette.error, "×", Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), modifier = Modifier.fillMaxWidth()) {
                    SummaryCard("Late", metrics.lateToday, ColorPalette.warning, "◷", Modifier.weight(1f))
                    SummaryCard("Employees", metrics.totalEmployees, ColorPalette.accentPurple, "••", Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(label: String, count: Int, color: Color, icon: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(Shapes.card).background(color.copy(alpha = 0.08f)).padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconBubble(text = icon, color = color, background = color.copy(alpha = 0.12f))
        Text(label, style = AppTypography.bodySmall, color = ColorPalette.textSecondary)
        Text(count.toString(), style = AppTypography.headlineSmall, color = ColorPalette.textPrimary)
    }
}

@Composable
private fun ActionGrid(
    onOpenNotices: () -> Unit,
    onOpenAssignTask: () -> Unit,
    onOpenAttendance: () -> Unit,
    onOpenEmployees: () -> Unit,
    onOpenTasks: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            ActionCard("Notices", "Publish and manage notices", "!", ColorPalette.accentBlue, onOpenNotices, Modifier.weight(1f))
            ActionCard("Assign Task", "Assign tasks to employees", "+", ColorPalette.success, onOpenAssignTask, Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            ActionCard("Select Employees", "Choose employees for actions", "••", ColorPalette.accentPurple, onOpenEmployees, Modifier.weight(1f))
            ActionCard("Employee Attendance", "View today's attendance details", "A", ColorPalette.error, onOpenAttendance, Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            ActionCard("Assigned Tasks", "Track task status labels", "✓", ColorPalette.warning, onOpenTasks, Modifier.weight(1f))
            ActionCard("Top Attendance", "View attendance leaders", "↗", ColorPalette.accentGreen, onOpenAttendance, Modifier.weight(1f))
        }
    }
}

@Composable
private fun ActionCard(title: String, body: String, icon: String, color: Color, onClick: () -> Unit, modifier: Modifier) {
    BaseCard(modifier = modifier.heightIn(min = 138.dp), onClick = onClick) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.SpaceBetween) {
            IconBubble(text = icon, color = color, background = color.copy(alpha = 0.14f))
            Spacer(Modifier.height(Spacing.md))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                    Text(title, style = AppTypography.titleMedium, color = ColorPalette.textPrimary)
                    Text(body, style = AppTypography.bodySmall, color = ColorPalette.textSecondary)
                }
                Text("›", style = AppTypography.headlineSmall, color = ColorPalette.textPrimary)
            }
        }
    }
}

@Composable
private fun NoticesPage(
    notices: List<NoticeResponse>,
    isLoading: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onCreate: () -> Unit,
    onEdit: (NoticeResponse) -> Unit,
    onDelete: (NoticeResponse) -> Unit
) {
    var pendingDelete by remember { mutableStateOf<NoticeResponse?>(null) }
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(ColorPalette.background).padding(PaddingPresets.screen),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        item { PageHeader("Notices", onBack, trailing = { SoftButton("Refresh", onRefresh) }) }
        item {
            BaseCard(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Notices", style = AppTypography.titleMedium)
                        Text("Create, manage and publish notices for employees.", style = AppTypography.bodySmall, color = ColorPalette.textSecondary)
                    }
                    SoftButton("+ New Notice", onCreate, filled = true)
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("All Notices", style = AppTypography.titleMedium)
                Chip("${notices.size} Total", ColorPalette.accentPurple)
            }
        }
        if (isLoading) item { LoadingBlock() }
        if (!isLoading && notices.isEmpty()) item { EmptyState("No notices yet.") }
        items(notices, key = { it.id }) { notice ->
            NoticeCard(notice, onEdit = { onEdit(notice) }, onDelete = { pendingDelete = notice })
        }
    }
    pendingDelete?.let { notice ->
        ConfirmDeleteDialog(
            title = "Delete notice?",
            body = notice.title,
            onCancel = { pendingDelete = null },
            onConfirm = {
                onDelete(notice)
                pendingDelete = null
            }
        )
    }
}

@Composable
private fun NoticeCard(notice: NoticeResponse, onEdit: () -> Unit, onDelete: () -> Unit) {
    BaseCard(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            IconBubble("!", priorityColor(notice.priority), priorityColor(notice.priority).copy(alpha = 0.14f))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs), verticalAlignment = Alignment.CenterVertically) {
                    Text(notice.title, style = AppTypography.titleMedium, color = ColorPalette.textPrimary, modifier = Modifier.weight(1f))
                    Chip(notice.priority.labelize(), priorityColor(notice.priority))
                }
                Text(notice.message, style = AppTypography.bodySmall, color = ColorPalette.textSecondary)
                Text("${notice.startAt.take(10)} • Admin", style = AppTypography.labelSmall, color = ColorPalette.textTertiary)
            }
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit notice", tint = ColorPalette.accentPurple)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete notice", tint = ColorPalette.error)
                }
            }
        }
    }
}

@Composable
private fun NoticeFormPage(
    notice: NoticeResponse?,
    employees: List<EmployeeInfo>,
    onBack: () -> Unit,
    onSave: (String?, String, String, String, String, String?, Boolean) -> Unit
) {
    var title by remember(notice?.id) { mutableStateOf(notice?.title ?: "") }
    var message by remember(notice?.id) { mutableStateOf(notice?.message ?: "") }
    var priority by remember(notice?.id) { mutableStateOf(notice?.priority ?: "NORMAL") }
    var audience by remember(notice?.id) { mutableStateOf(notice?.audienceType ?: "ALL_EMPLOYEES") }
    var department by remember(notice?.id) { mutableStateOf("") }
    var isActive by remember(notice?.id) { mutableStateOf(notice?.isActive ?: true) }
    var submitted by remember { mutableStateOf(false) }
    val departments = employees.map { it.department }.filter { it.isNotBlank() && it != "N/A" }.distinct()

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(ColorPalette.background).padding(PaddingPresets.screen),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        item { PageHeader(if (notice == null) "Create Notice" else "Edit Notice", onBack) }
        item {
            BaseCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Text(if (notice == null) "Create a New Notice" else "Update Notice", style = AppTypography.titleMedium)
                    Text("Fill in the details below to publish a professional notice.", style = AppTypography.bodySmall, color = ColorPalette.textSecondary)
                }
            }
        }
        item {
            CustomTextField(title, { title = it.take(100) }, "Title *", placeholder = "Enter notice title")
            if (submitted && title.isBlank()) FieldError("Title is required")
        }
        item {
            MultilineField(message, { message = it.take(1000) }, "Message *", "Enter notice message")
            if (submitted && message.isBlank()) FieldError("Message is required")
        }
        item {
            ChipGroup("Priority", listOf("NORMAL", "IMPORTANT", "URGENT"), priority) { priority = it }
        }
        item {
            ChipGroup("Audience *", listOf("ALL_EMPLOYEES", "DEPARTMENT"), audience) { audience = it }
            if (audience == "DEPARTMENT") {
                Spacer(Modifier.height(Spacing.sm))
                ChipGroup("Department / Team", departments.ifEmpty { listOf("Engineering", "QA") }, department.ifBlank { departments.firstOrNull() ?: "" }) { department = it }
            }
        }
        item {
            BaseCard(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Publish notice", style = AppTypography.bodyMedium)
                        Text("Turn off to save as draft.", style = AppTypography.bodySmall, color = ColorPalette.textSecondary)
                    }
                    Switch(checked = isActive, onCheckedChange = { isActive = it })
                }
            }
        }
        item {
            PrimaryButton(
                text = if (isActive) "Publish Notice" else "Save as Draft",
                enabled = title.isNotBlank() && message.isNotBlank(),
                onClick = {
                    submitted = true
                    if (title.isNotBlank() && message.isNotBlank()) {
                        onSave(notice?.id, title, message, priority, audience, department.takeIf { it.isNotBlank() }, isActive)
                    }
                }
            )
            Spacer(Modifier.height(Spacing.sm))
            SecondaryButton(text = "Cancel", onClick = onBack)
        }
    }
}

@Composable
private fun AssignTaskSheet(
    employees: List<EmployeeInfo>,
    defaultDueDate: String,
    onClose: () -> Unit,
    onAssign: (List<String>, String, String?, String, TaskPriority) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf(defaultDueDate) }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var search by remember { mutableStateOf("") }
    var selectedIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var submitted by remember { mutableStateOf(false) }
    val filtered = employees.filter { it.name.contains(search, ignoreCase = true) || it.department.contains(search, ignoreCase = true) }

    LazyColumn(Modifier.fillMaxWidth().padding(PaddingPresets.screen), verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        item { SheetHeader("Assign Task", "Create and assign tasks to employees", onClose) }
        item {
            CustomTextField(title, { title = it.take(100) }, "Task Title *", placeholder = "Enter task title")
            if (submitted && title.isBlank()) FieldError("Task title is required")
        }
        item {
            MultilineField(details, { details = it.take(500) }, "Task Details / Description *", "Describe the task in detail")
            if (submitted && details.isBlank()) FieldError("Description is required")
        }
        item {
            CustomTextField(dueDate, { dueDate = it }, "Due Date *", placeholder = "YYYY-MM-DD")
            if (submitted && dueDate.isBlank()) FieldError("Due date is required")
        }
        item { PrioritySelector(priority) { priority = it } }
        item {
            CustomTextField(search, { search = it }, "Assign To *", placeholder = "Search employee")
            if (submitted && selectedIds.isEmpty()) FieldError("Select at least one employee")
        }
        items(filtered.take(8), key = { it.id }) { employee ->
            EmployeeSelectRow(
                employee = employee,
                selected = employee.id in selectedIds,
                onToggle = {
                    selectedIds = if (employee.id in selectedIds) selectedIds - employee.id else selectedIds + employee.id
                }
            )
        }
        item {
            StatusLegend()
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md), modifier = Modifier.fillMaxWidth()) {
                SecondaryButton(text = "Cancel", onClick = onClose, modifier = Modifier.weight(1f))
                PrimaryButton(
                    text = "Assign Task",
                    modifier = Modifier.weight(1f),
                    enabled = title.isNotBlank() && details.isNotBlank() && dueDate.isNotBlank() && selectedIds.isNotEmpty(),
                    onClick = {
                        submitted = true
                        if (title.isNotBlank() && details.isNotBlank() && dueDate.isNotBlank() && selectedIds.isNotEmpty()) {
                            onAssign(selectedIds.toList(), title, details, dueDate, priority)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun TaskStatusSheet(tasks: List<ApiTask>, employees: List<EmployeeInfo>, onClose: () -> Unit, onRefresh: () -> Unit) {
    var filter by remember { mutableStateOf("All") }
    val filtered = tasks.filter { filter == "All" || it.status.displayStatus() == filter }
    val employeesById = employees.associateBy { it.id }
    LazyColumn(Modifier.fillMaxWidth().padding(PaddingPresets.screen), verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        item { SheetHeader("Task Status Overview", "Track live status of assigned tasks", onClose, trailing = { SoftButton("Refresh", onRefresh) }) }
        item { Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
            listOf("All", "To Do", "In Progress", "Review", "Completed", "Blocked").forEach { label ->
                SmallFilterChip(label, filter == label) { filter = label }
            }
        } }
        if (filtered.isEmpty()) item { EmptyState("No tasks for this status.") }
        items(filtered, key = { it.id }) { task ->
            TaskStatusCard(task, employeesById)
        }
    }
}

@Composable
private fun TaskStatusCard(task: ApiTask, employeesById: Map<String, EmployeeInfo>) {
    val assignees = task.assignedEmployeeIds.mapNotNull { employeesById[it] }
    BaseCard(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md), verticalAlignment = Alignment.CenterVertically) {
            IconBubble("✓", priorityColor(task.priority.name), priorityColor(task.priority.name).copy(alpha = 0.12f))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(task.title, style = AppTypography.titleMedium)
                Text(assignees.joinToString { "${it.name} • ${it.department}" }.ifBlank { "Assigned employee" }, style = AppTypography.bodySmall, color = ColorPalette.textSecondary)
                Text("Due ${task.dueDate ?: "-"}", style = AppTypography.labelSmall, color = ColorPalette.textTertiary)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Chip(task.priority.displayName, priorityColor(task.priority.name))
                Chip(task.status.displayStatus(), statusColor(task.status))
            }
        }
    }
}

@Composable
private fun AttendancePage(
    metrics: DashboardMetrics,
    employees: List<EmployeeInfo>,
    isLoading: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("All") }
    val filtered = employees.filter {
        (query.isBlank() || it.name.contains(query, ignoreCase = true) || it.department.contains(query, ignoreCase = true)) &&
            (filter == "All" || it.status == filter)
    }

    LazyColumn(Modifier.fillMaxSize().background(ColorPalette.background).padding(PaddingPresets.screen), verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
        item { PageHeader("Employee Attendance", onBack, subtitle = "Track daily employee presence", trailing = { SoftButton("Refresh", onRefresh) }) }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    AttendanceSummaryCard("Present", metrics.presentToday, ColorPalette.success, Modifier.weight(1f))
                    AttendanceSummaryCard("Absent", metrics.absentToday, ColorPalette.error, Modifier.weight(1f))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    AttendanceSummaryCard("Late", metrics.lateToday, ColorPalette.warning, Modifier.weight(1f))
                    AttendanceSummaryCard("Employees", metrics.totalEmployees, ColorPalette.accentPurple, Modifier.weight(1f))
                }
            }
        }
        item {
            BaseCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md), modifier = Modifier.fillMaxWidth()) {
                    CustomTextField(query, { query = it }, "Search employee", placeholder = "Search by name or department")
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs), modifier = Modifier.fillMaxWidth()) {
                        listOf("All", "Present", "Absent", "Late").forEach { label -> SmallFilterChip(label, filter == label) { filter = label } }
                    }
                }
            }
        }
        if (isLoading) item { LoadingBlock() }
        if (!isLoading && filtered.isEmpty()) item { EmptyState("No employees match this filter.") }
        if (!isLoading && filtered.isNotEmpty()) {
            item {
                AttendanceTable(employees = filtered)
            }
        }
        item {
            BaseCard(Modifier.fillMaxWidth(), onClick = onRefresh) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("View Full Attendance Report", style = AppTypography.titleMedium, color = ColorPalette.primary)
                    Text("›", style = AppTypography.headlineSmall, color = ColorPalette.primary)
                }
            }
        }
    }
}

@Composable
private fun AttendanceTable(employees: List<EmployeeInfo>) {
    BaseCard(Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.md), modifier = Modifier.fillMaxWidth()) {
            Text("Daily Attendance Table", style = AppTypography.titleMedium, color = ColorPalette.textPrimary)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                AttendanceTableHeader()
                employees.forEach { employee ->
                    AttendanceTableRow(employee)
                }
            }
        }
    }
}

@Composable
private fun AttendanceTableHeader() {
    Row(
        modifier = Modifier
            .width(760.dp)
            .clip(Shapes.button)
            .background(ColorPalette.backgroundTertiary)
            .padding(vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TableCell("Employee", 190.dp, header = true)
        TableCell("Check In", 110.dp, header = true)
        TableCell("Check Out", 110.dp, header = true)
        TableCell("Late", 80.dp, header = true, align = TextAlign.Center)
        TableCell("Leave", 80.dp, header = true, align = TextAlign.Center)
        TableCell("Status", 150.dp, header = true, align = TextAlign.Center)
    }
}

@Composable
private fun AttendanceTableRow(employee: EmployeeInfo) {
    Row(
        modifier = Modifier
            .width(760.dp)
            .border(1.dp, ColorPalette.divider, Shapes.button)
            .padding(vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.width(190.dp).padding(horizontal = Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(38.dp).clip(CircleShape).background(employee.statusColor().copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(employee.name.firstOrNull()?.uppercase() ?: "E", style = AppTypography.labelMedium, color = employee.statusColor())
            }
            Column(Modifier.weight(1f)) {
                Text(employee.name, style = AppTypography.bodyMedium, color = ColorPalette.textPrimary)
                Text(employee.department, style = AppTypography.labelSmall, color = ColorPalette.textSecondary)
            }
        }
        TableCell(employee.checkInTime ?: "-", 110.dp)
        TableCell(employee.checkOutTime ?: "-", 110.dp)
        TableCell(if (employee.isLate) "Yes" else "No", 80.dp, color = if (employee.isLate) ColorPalette.warning else ColorPalette.textSecondary, align = TextAlign.Center)
        TableCell(if (employee.isOnLeave) "Yes" else "No", 80.dp, color = if (employee.isOnLeave) ColorPalette.accentBlue else ColorPalette.textSecondary, align = TextAlign.Center)
        Box(modifier = Modifier.width(150.dp).padding(horizontal = Spacing.sm), contentAlignment = Alignment.Center) {
            Chip(employee.status, employee.statusColor())
        }
    }
}

@Composable
private fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    header: Boolean = false,
    color: Color = ColorPalette.textPrimary,
    align: TextAlign = TextAlign.Start
) {
    Text(
        text = text,
        style = if (header) AppTypography.labelMedium else AppTypography.bodySmall,
        color = if (header) ColorPalette.textSecondary else color,
        textAlign = align,
        modifier = Modifier.width(width).padding(horizontal = Spacing.sm)
    )
}

@Composable
private fun AttendanceSummaryCard(label: String, count: Int, color: Color, modifier: Modifier) {
    BaseCard(modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            IconBubble(label.take(1), color, color.copy(alpha = 0.12f))
            Text(count.toString(), style = AppTypography.headlineSmall, color = ColorPalette.textPrimary)
            Text(label, style = AppTypography.bodyMedium, color = color)
        }
    }
}

@Composable
private fun EmployeeSelectRow(employee: EmployeeInfo, selected: Boolean, onToggle: () -> Unit) {
    BaseCard(Modifier.fillMaxWidth(), onClick = onToggle) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(employee.name, style = AppTypography.bodyMedium)
                Text(employee.department, style = AppTypography.bodySmall, color = ColorPalette.textSecondary)
            }
            Chip(if (selected) "Selected" else "Select", if (selected) ColorPalette.primary else ColorPalette.textTertiary)
        }
    }
}

@Composable
private fun StatusLegend() {
    BaseCard(Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Text("Employee Status Labels", style = AppTypography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                listOf("To Do", "In Progress", "Review", "Completed", "Blocked").forEach { label ->
                    Chip(label, statusColor(label))
                }
            }
            Text("Employees can update these labels after task assignment.", style = AppTypography.bodySmall, color = ColorPalette.primary)
        }
    }
}

@Composable
private fun PrioritySelector(priority: TaskPriority, onChange: (TaskPriority) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text("Priority *", style = AppTypography.labelMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), modifier = Modifier.fillMaxWidth()) {
            TaskPriority.entries.forEach { option ->
                SelectableBox(option.displayName, priority == option, priorityColor(option.name), Modifier.weight(1f)) { onChange(option) }
            }
        }
    }
}

@Composable
private fun ChipGroup(title: String, values: List<String>, selected: String, onSelected: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text(title, style = AppTypography.labelMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), modifier = Modifier.fillMaxWidth()) {
            values.forEach { value ->
                SelectableBox(value.labelize(), selected == value, priorityColor(value), Modifier.weight(1f)) { onSelected(value) }
            }
        }
    }
}

@Composable
private fun SelectableBox(text: String, selected: Boolean, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(Shapes.button)
            .background(if (selected) color.copy(alpha = 0.12f) else ColorPalette.surface)
            .border(1.dp, if (selected) color else ColorPalette.border, Shapes.button)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = AppTypography.labelMedium, color = if (selected) color else ColorPalette.textSecondary)
    }
}

@Composable
private fun PageHeader(title: String, onBack: () -> Unit, subtitle: String? = null, trailing: @Composable (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md), verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            IconButton(onClick = onBack, modifier = Modifier.size(52.dp).clip(Shapes.button).background(ColorPalette.surface)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = ColorPalette.textPrimary)
            }
            Column {
                Text(title, style = AppTypography.headlineSmall, color = ColorPalette.textPrimary)
                if (subtitle != null) Text(subtitle, style = AppTypography.bodySmall, color = ColorPalette.textSecondary)
            }
        }
        trailing?.invoke()
    }
}

@Composable
private fun SheetHeader(title: String, subtitle: String, onClose: () -> Unit, trailing: @Composable (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, style = AppTypography.titleLarge)
            Text(subtitle, style = AppTypography.bodySmall, color = ColorPalette.textSecondary)
        }
        trailing?.invoke()
        IconButton(onClick = onClose) { Text("×", style = AppTypography.headlineSmall, color = ColorPalette.textPrimary) }
    }
}

@Composable
private fun CompactInfoCard(icon: String, iconColor: Color, title: String, body: String, trailing: String? = null, onClick: () -> Unit) {
    BaseCard(Modifier.fillMaxWidth(), onClick = onClick) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md), verticalAlignment = Alignment.CenterVertically) {
            IconBubble(icon, iconColor, iconColor.copy(alpha = 0.12f))
            Column(Modifier.weight(1f)) {
                Text(title, style = AppTypography.titleMedium)
                Text(body, style = AppTypography.bodySmall, color = ColorPalette.textSecondary)
            }
            Text(trailing ?: "›", style = AppTypography.titleMedium, color = ColorPalette.primary)
        }
    }
}

@Composable
private fun IconBubble(text: String, color: Color, background: Color) {
    Box(Modifier.size(48.dp).clip(CircleShape).background(background), contentAlignment = Alignment.Center) {
        Text(text, style = AppTypography.titleMedium, color = color)
    }
}

@Composable
private fun NotificationButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(Shapes.button)
            .background(ColorPalette.surface)
            .border(1.dp, ColorPalette.border, Shapes.button)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(24.dp)) {
            val stroke = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
            val c = ColorPalette.textPrimary
            drawArc(
                color = c,
                startAngle = 205f,
                sweepAngle = 130f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.22f, size.height * 0.18f),
                size = androidx.compose.ui.geometry.Size(size.width * 0.56f, size.height * 0.58f),
                style = stroke
            )
            drawLine(c, androidx.compose.ui.geometry.Offset(size.width * 0.22f, size.height * 0.66f), androidx.compose.ui.geometry.Offset(size.width * 0.78f, size.height * 0.66f), strokeWidth = 2.4.dp.toPx(), cap = StrokeCap.Round)
            drawLine(c, androidx.compose.ui.geometry.Offset(size.width * 0.50f, size.height * 0.10f), androidx.compose.ui.geometry.Offset(size.width * 0.50f, size.height * 0.18f), strokeWidth = 2.4.dp.toPx(), cap = StrokeCap.Round)
            drawCircle(c, radius = 2.2.dp.toPx(), center = androidx.compose.ui.geometry.Offset(size.width * 0.50f, size.height * 0.78f))
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-8).dp, y = 8.dp)
                .size(9.dp)
                .clip(CircleShape)
                .background(ColorPalette.error)
        )
    }
}

@Composable
private fun SoftButton(text: String, onClick: () -> Unit, filled: Boolean = false) {
    Box(
        Modifier
            .clip(Shapes.button)
            .background(if (filled) ColorPalette.accentPurple else ColorPalette.surface)
            .border(1.dp, if (filled) ColorPalette.accentPurple else ColorPalette.border, Shapes.button)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm)
    ) {
        Text(text, style = AppTypography.labelMedium, color = if (filled) ColorPalette.textOnDark else ColorPalette.primary)
    }
}

@Composable
private fun SmallFilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .height(38.dp)
            .clip(Shapes.button)
            .background(if (selected) ColorPalette.primaryLight else ColorPalette.surface)
            .border(1.dp, if (selected) ColorPalette.primary else ColorPalette.border, Shapes.button)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.sm),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = AppTypography.labelSmall, color = if (selected) ColorPalette.primaryDark else ColorPalette.textSecondary)
    }
}

@Composable
private fun Chip(text: String, color: Color) {
    Box(Modifier.clip(Shapes.badge).background(color.copy(alpha = 0.12f)).padding(horizontal = Spacing.sm, vertical = Spacing.xxs)) {
        Text(text, style = AppTypography.labelSmall, color = color)
    }
}

@Composable
private fun Detail(label: String, value: String, color: Color = ColorPalette.textSecondary) {
    Column {
        Text(label, style = AppTypography.labelSmall, color = ColorPalette.textTertiary)
        Text(value, style = AppTypography.bodySmall, color = color)
    }
}

@Composable
private fun InlineMessage(message: String, isError: Boolean) {
    BaseCard(Modifier.fillMaxWidth()) {
        Text(message, style = AppTypography.bodyMedium, color = if (isError) ColorPalette.error else ColorPalette.success)
    }
}

@Composable
private fun LoadingBlock() {
    Box(Modifier.fillMaxWidth().padding(Spacing.lg), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = ColorPalette.primary)
    }
}

@Composable
private fun EmptyState(text: String) {
    BaseCard(Modifier.fillMaxWidth()) {
        Text(text, style = AppTypography.bodyMedium, color = ColorPalette.textSecondary)
    }
}

@Composable
private fun FieldError(text: String) {
    Text(text, style = AppTypography.labelSmall, color = ColorPalette.error)
}

@Composable
private fun ConfirmDeleteDialog(title: String, body: String, onCancel: () -> Unit, onConfirm: () -> Unit) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(title, style = AppTypography.titleMedium) },
        text = { Text(body, style = AppTypography.bodyMedium) },
        confirmButton = { SoftButton("Delete", onConfirm, filled = true) },
        dismissButton = { SoftButton("Cancel", onCancel) }
    )
}

@Composable
private fun MultilineField(value: String, onValueChange: (String) -> Unit, label: String, placeholder: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = AppTypography.labelMedium) },
        placeholder = { Text(placeholder, style = AppTypography.bodyMedium, color = ColorPalette.textTertiary) },
        modifier = Modifier.fillMaxWidth().height(132.dp),
        textStyle = AppTypography.bodyMedium,
        shape = Shapes.input,
        singleLine = false,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ColorPalette.primary,
            unfocusedBorderColor = ColorPalette.border,
            focusedLabelColor = ColorPalette.primary,
            unfocusedLabelColor = ColorPalette.textSecondary,
            cursorColor = ColorPalette.primary,
            focusedContainerColor = ColorPalette.background,
            unfocusedContainerColor = ColorPalette.background
        )
    )
}

private fun topAttendanceText(employees: List<EmployeeInfo>): String {
    val best = employees.filter { it.totalWorkedMinutes > 0 }.maxByOrNull { it.totalWorkedMinutes }
    return best?.let { "${it.name} • ${it.totalWorkedMinutes / 60}h ${it.totalWorkedMinutes % 60}m" } ?: "No worked time recorded yet."
}

private fun currentDateLabel(): String = kotlinx.datetime.Clock.System.now()
    .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    .date
    .toString()

private fun String.labelize(): String = lowercase().replace("_", " ").split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }

private fun String.displayStatus(): String = when (lowercase()) {
    "pending" -> "To Do"
    "in_progress" -> "In Progress"
    "review" -> "Review"
    "completed" -> "Completed"
    "blocked", "cancelled" -> "Blocked"
    else -> labelize()
}

private fun priorityColor(priority: String): Color = when (priority.uppercase()) {
    "LOW", "NORMAL" -> ColorPalette.success
    "MEDIUM", "IMPORTANT" -> ColorPalette.warning
    "HIGH", "URGENT" -> ColorPalette.error
    else -> ColorPalette.accentPurple
}

private fun statusColor(status: String): Color = when (status.displayStatus()) {
    "Completed" -> ColorPalette.success
    "In Progress" -> ColorPalette.accentBlue
    "Review" -> ColorPalette.accentPurple
    "Blocked" -> ColorPalette.error
    else -> ColorPalette.textTertiary
}

private fun EmployeeInfo.statusColor(): Color = when (status) {
    "Present" -> ColorPalette.success
    "Late" -> ColorPalette.warning
    "On Leave" -> ColorPalette.accentBlue
    else -> ColorPalette.error
}
