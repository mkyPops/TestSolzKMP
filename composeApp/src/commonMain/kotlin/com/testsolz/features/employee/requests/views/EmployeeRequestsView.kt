package com.testsolz.features.employee.requests.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.testsolz.designsystem.theme.*
import com.testsolz.domain.models.LeaveType
import com.testsolz.features.employee.requests.viewmodels.RequestsViewModel
import com.testsolz.shared.components.buttons.PrimaryButton
import com.testsolz.shared.components.buttons.SecondaryButton
import com.testsolz.shared.components.cards.BaseCard
import com.testsolz.shared.components.cards.RequestCard
import com.testsolz.shared.components.input.CustomTextField
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

/**
 * Employee Requests View
 * View and create leave/late requests
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeRequestsView(
    viewModel: RequestsViewModel = viewModel()
) {
    val requests by viewModel.requests.collectAsState()
    var activeSheet by remember { mutableStateOf<RequestSheetType?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        text = "My Requests",
                        style = AppTypography.headlineLarge
                    )
                    Text(
                        text = "Submit and track leave & late arrival requests",
                        style = AppTypography.bodyMedium,
                        color = ColorPalette.textSecondary
                    )
                }
            }

            // Quick actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        PrimaryButton(
                            text = "Leave Request",
                            onClick = { activeSheet = RequestSheetType.LEAVE }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        PrimaryButton(
                            text = "Late Arrival",
                            onClick = { activeSheet = RequestSheetType.LATE }
                        )
                    }
                }
            }

            // Requests list
            if (requests.isEmpty()) {
                item {
                    BaseCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.lg),
                            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                        ) {
                            Text(
                                text = "No requests yet",
                                style = AppTypography.bodyMedium,
                                color = ColorPalette.textSecondary
                            )
                            Text(
                                text = "Create your first leave or late arrival request",
                                style = AppTypography.bodySmall,
                                color = ColorPalette.textTertiary
                            )
                        }
                    }
                }
            } else {
                items(requests) { request ->
                    RequestCard(
                        request = request,
                        onClick = { /* TODO: Navigate to details */ }
                    )
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(Spacing.xl))
            }
        }

        if (activeSheet != null) {
            val sheetState = rememberModalBottomSheetState()
            ModalBottomSheet(
                onDismissRequest = { activeSheet = null },
                sheetState = sheetState,
                containerColor = ColorPalette.surface
            ) {
                when (activeSheet) {
                    RequestSheetType.LEAVE -> LeaveRequestSheet(
                        onSubmit = { leaveType, start, end, reason ->
                            viewModel.submitLeaveRequest(
                                leaveType = leaveType,
                                startDate = start,
                                endDate = end,
                                reasonLabel = reason
                            )
                            activeSheet = null
                        },
                        onCancel = { activeSheet = null }
                    )

                    RequestSheetType.LATE -> LateArrivalSheet(
                        onSubmit = { expectedTime, note ->
                            viewModel.submitLateArrival(expectedTime = expectedTime, note = note)
                            activeSheet = null
                        },
                        onCancel = { activeSheet = null }
                    )

                    null -> {}
                }
            }
        }
    }
}

private enum class RequestSheetType { LEAVE, LATE }

@Composable
private fun LeaveRequestSheet(
    onSubmit: (leaveType: LeaveType, start: LocalDate, end: LocalDate?, reasonLabel: String) -> Unit,
    onCancel: () -> Unit
) {
    val timeZone = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(timeZone).date

    var monthCursor by remember { mutableStateOf(LocalDate(today.year, today.month, 1)) }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedReason by remember { mutableStateOf(LeaveReason.EMERGENCY) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        Text(text = "Leave Request", style = AppTypography.headlineSmall)

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Text(text = "Reason", style = AppTypography.labelMedium, color = ColorPalette.textSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                LeaveReasonChip(reason = LeaveReason.EMERGENCY, selected = selectedReason == LeaveReason.EMERGENCY) {
                    selectedReason = LeaveReason.EMERGENCY
                }
                LeaveReasonChip(reason = LeaveReason.VACATION, selected = selectedReason == LeaveReason.VACATION) {
                    selectedReason = LeaveReason.VACATION
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                LeaveReasonChip(reason = LeaveReason.MEDICAL, selected = selectedReason == LeaveReason.MEDICAL) {
                    selectedReason = LeaveReason.MEDICAL
                }
                LeaveReasonChip(reason = LeaveReason.PERSONAL, selected = selectedReason == LeaveReason.PERSONAL) {
                    selectedReason = LeaveReason.PERSONAL
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MonthNavChip(text = "Prev") {
                    monthCursor = monthCursor.minus(DatePeriod(months = 1))
                }

                Text(
                    text = "${monthCursor.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${monthCursor.year}",
                    style = AppTypography.titleSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                MonthNavChip(text = "Next") {
                    monthCursor = monthCursor.plus(DatePeriod(months = 1))
                }
            }

            LeaveDatePicker(
                month = monthCursor,
                startDate = startDate,
                endDate = endDate,
                onDateSelected = { date ->
                    when {
                        startDate == null || (startDate != null && endDate != null) -> {
                            startDate = date
                            endDate = null
                        }
                        startDate != null && endDate == null && date < startDate!! -> startDate = date
                        else -> endDate = date
                    }
                }
            )

            val rangeLabel = when {
                startDate == null -> "Select leave date(s)"
                endDate == null -> "Selected: $startDate"
                else -> "Selected: $startDate - $endDate"
            }
            Text(text = rangeLabel, style = AppTypography.captionMedium, color = ColorPalette.textSecondary)
        }

        PrimaryButton(
            text = "Submit",
            enabled = startDate != null,
            onClick = {
                val start = startDate ?: return@PrimaryButton
                val leaveType = selectedReason.toLeaveType()
                val reasonLabel = selectedReason.label
                onSubmit(leaveType, start, endDate, reasonLabel)
            }
        )

        SecondaryButton(text = "Cancel", onClick = onCancel)

        Spacer(modifier = Modifier.height(Spacing.lg))
    }
}

@Composable
private fun MonthNavChip(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(Shapes.badge)
            .background(ColorPalette.backgroundSecondary)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.sm, vertical = Spacing.xxs)
    ) {
        Text(text = text, style = AppTypography.labelSmall, color = ColorPalette.textSecondary)
    }
}

private enum class LeaveReason(val label: String) {
    EMERGENCY("Emergency"),
    VACATION("Vacation"),
    MEDICAL("Medical"),
    PERSONAL("Personal")
}

private fun LeaveReason.toLeaveType(): LeaveType = when (this) {
    LeaveReason.EMERGENCY -> LeaveType.EMERGENCY
    LeaveReason.VACATION -> LeaveType.VACATION
    LeaveReason.MEDICAL -> LeaveType.SICK
    LeaveReason.PERSONAL -> LeaveType.PERSONAL
}

@Composable
private fun LeaveReasonChip(
    reason: LeaveReason,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(Shapes.badge)
            .background(
                color = if (selected) ColorPalette.primaryLight else ColorPalette.backgroundSecondary,
                shape = Shapes.badge
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.sm, vertical = Spacing.xxs)
    ) {
        Text(
            text = reason.label,
            style = AppTypography.labelSmall,
            color = if (selected) ColorPalette.primary else ColorPalette.textSecondary
        )
    }
}

@Composable
private fun LeaveDatePicker(
    month: LocalDate,
    startDate: LocalDate?,
    endDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDay = LocalDate(month.year, month.month, 1)
    val firstIso = firstDay.dayOfWeek.ordinal + 1
    val leadingEmpty = firstIso - 1
    val daysInMonth = firstDay.plus(DatePeriod(months = 1)).minus(DatePeriod(days = 1)).dayOfMonth

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        for (weekStart in 0 until 42 step 7) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (i in 0 until 7) {
                    val cell = weekStart + i
                    val dayNumber = cell - leadingEmpty + 1
                    val date = if (dayNumber in 1..daysInMonth) LocalDate(month.year, month.month, dayNumber) else null

                    val inRange = if (startDate != null && date != null) {
                        when {
                            endDate == null -> date == startDate
                            else -> date >= startDate && date <= endDate
                        }
                    } else false

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(Spacing.xxxs)
                            .clip(Shapes.badge)
                            .background(if (inRange) ColorPalette.primaryLight else ColorPalette.background)
                            .then(
                                if (date != null) Modifier.clickable { onDateSelected(date) } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date?.dayOfMonth?.toString() ?: "",
                            style = AppTypography.bodySmall,
                            color = if (date != null) ColorPalette.textPrimary else ColorPalette.textTertiary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LateArrivalSheet(
    onSubmit: (expectedTime: String, note: String) -> Unit,
    onCancel: () -> Unit
) {
    var note by remember { mutableStateOf("") }
    var hour by remember { mutableStateOf(10) }
    var minute by remember { mutableStateOf(0) }
    var amPm by remember { mutableStateOf("AM") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        Text(text = "Late Arrival", style = AppTypography.headlineSmall)

        CustomTextField(
            value = note,
            onValueChange = { note = it },
            label = "Reason / Note",
            placeholder = "Add a short note"
        )

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Text(text = "Expected arrival time", style = AppTypography.labelMedium, color = ColorPalette.textSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                TimeDropdown(
                    label = "Hour",
                    valueText = hour.toString(),
                    options = (1..12).map { it.toString() },
                    onSelected = { hour = it.toInt() },
                    modifier = Modifier.weight(1f)
                )
                TimeDropdown(
                    label = "Min",
                    valueText = minute.toString().padStart(2, '0'),
                    options = listOf("00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"),
                    onSelected = { minute = it.toInt() },
                    modifier = Modifier.weight(1f)
                )
                TimeDropdown(
                    label = "AM/PM",
                    valueText = amPm,
                    options = listOf("AM", "PM"),
                    onSelected = { amPm = it },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        PrimaryButton(
            text = "Submit",
            onClick = {
                val expectedTime = "${hour}:${minute.toString().padStart(2, '0')} $amPm"
                onSubmit(expectedTime, note)
            }
        )

        SecondaryButton(text = "Cancel", onClick = onCancel)

        Spacer(modifier = Modifier.height(Spacing.lg))
    }
}

@Composable
private fun TimeDropdown(
    label: String,
    valueText: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(text = label, style = AppTypography.captionMedium, color = ColorPalette.textSecondary)
        Spacer(modifier = Modifier.height(Spacing.xxxs))

        Box {
            BaseCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { expanded = true }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = valueText, style = AppTypography.bodyMedium)
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(ColorPalette.textTertiary)
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { opt ->
                    DropdownMenuItem(
                        text = { Text(opt) },
                        onClick = {
                            onSelected(opt)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
