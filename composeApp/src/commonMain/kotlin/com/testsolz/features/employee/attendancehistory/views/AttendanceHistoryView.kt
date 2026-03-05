package com.testsolz.features.employee.attendancehistory.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.testsolz.designsystem.theme.*
import com.testsolz.features.employee.attendancehistory.viewmodels.AttendanceHistoryViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

/**
 * Attendance History View
 * Displays attendance in a minimal calendar view
 */
@Composable
fun AttendanceHistoryView(
    viewModel: AttendanceHistoryViewModel = viewModel()
) {
    val history by viewModel.attendanceHistory.collectAsState()
    val timeZone = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(timeZone).date
    val historyByDate = history.associateBy { it.date }

    var monthCursor by remember { mutableStateOf(LocalDate(today.year, today.month, 1)) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.background)
            .padding(PaddingPresets.screen),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        item {
            Text(
                text = "Attendance History",
                style = AppTypography.headlineLarge
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(Shapes.badge)
                        .background(ColorPalette.backgroundSecondary)
                        .clickable { monthCursor = monthCursor.minus(DatePeriod(months = 1)) }
                        .padding(horizontal = Spacing.sm, vertical = Spacing.xxs)
                ) {
                    Text(
                        text = "Prev",
                        style = AppTypography.labelSmall,
                        color = ColorPalette.textSecondary,
                        modifier = Modifier.padding(0.dp)
                    )
                }

                Text(
                    text = "${monthCursor.month.displayName()} ${monthCursor.year}",
                    style = AppTypography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .clip(Shapes.badge)
                        .background(ColorPalette.backgroundSecondary)
                        .clickable { monthCursor = monthCursor.plus(DatePeriod(months = 1)) }
                        .padding(horizontal = Spacing.sm, vertical = Spacing.xxs)
                ) {
                    Text(
                        text = "Next",
                        style = AppTypography.labelSmall,
                        color = ColorPalette.textSecondary,
                        modifier = Modifier.padding(0.dp)
                    )
                }
            }
        }

        item {
            CalendarMonth(
                month = monthCursor,
                historyByDate = historyByDate,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                LegendDot(color = ColorPalette.primary, label = "On Time")
                LegendDot(color = ColorPalette.error, label = "Late")
            }
        }

        item { Spacer(modifier = Modifier.height(Spacing.xl)) }
    }
}

@Composable
private fun CalendarMonth(
    month: LocalDate,
    historyByDate: Map<LocalDate, com.testsolz.domain.models.Attendance>,
    modifier: Modifier = Modifier
) {
    val firstDay = LocalDate(month.year, month.month, 1)
    val firstIsoDay = firstDay.dayOfWeek.ordinal + 1 // 1..7 (Mon..Sun)
    val leadingEmpty = (firstIsoDay - 1).coerceAtLeast(0)
    val daysInMonth = firstDay.plus(DatePeriod(months = 1)).minus(DatePeriod(days = 1)).dayOfMonth
    val totalCells = 42

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            DayOfWeek.entries.forEach { dow ->
                Text(
                    text = dow.shortName(),
                    style = AppTypography.captionSmall,
                    color = ColorPalette.textTertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        for (weekStart in 0 until totalCells step 7) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (i in 0 until 7) {
                    val cellIndex = weekStart + i
                    val dayNumber = cellIndex - leadingEmpty + 1
                    val date = if (dayNumber in 1..daysInMonth) {
                        LocalDate(month.year, month.month, dayNumber)
                    } else null

                    val attendance = date?.let { historyByDate[it] }
                    val dotColor = when (attendance?.status) {
                        com.testsolz.domain.models.AttendanceStatus.ON_TIME -> ColorPalette.primary
                        com.testsolz.domain.models.AttendanceStatus.SLIGHTLY_LATE,
                        com.testsolz.domain.models.AttendanceStatus.LATE -> ColorPalette.error
                        else -> null
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(Spacing.xxs),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(Spacing.xxxs),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = date?.dayOfMonth?.toString() ?: "",
                                style = AppTypography.bodySmall,
                                color = if (date != null) ColorPalette.textPrimary else ColorPalette.textTertiary
                            )

                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(dotColor ?: ColorPalette.background)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: androidx.compose.ui.graphics.Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(text = label, style = AppTypography.captionMedium, color = ColorPalette.textSecondary)
    }
}

private fun Month.displayName(): String =
    name.lowercase().replaceFirstChar { it.uppercase() }

private fun DayOfWeek.shortName(): String =
    name.take(3).lowercase().replaceFirstChar { it.uppercase() }
