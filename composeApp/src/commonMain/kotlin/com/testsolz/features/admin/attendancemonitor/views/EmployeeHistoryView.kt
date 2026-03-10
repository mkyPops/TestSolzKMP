package com.testsolz.features.admin.attendancemonitor.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.testsolz.designsystem.theme.*
import com.testsolz.domain.models.Attendance
import com.testsolz.domain.models.User
import com.testsolz.domain.models.formatted
import com.testsolz.shared.components.cards.BaseCard

@Composable
fun EmployeeHistoryView(
    employee: User,
    onBack: () -> Unit
) {
    val history = remember(employee.id) {
        Attendance.mockHistory(employee.id, daysBack = 14)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.background)
    ) {
        // Custom Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = ColorPalette.textPrimary
                )
            }

            Column {
                Text(
                    text = "${employee.name}'s History",
                    style = AppTypography.headlineSmall
                )
                Text(
                    text = "Last 14 days attendance",
                    style = AppTypography.captionMedium,
                    color = ColorPalette.textSecondary
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
            contentPadding = PaddingValues(bottom = Spacing.xl)
        ) {
            items(history) { record ->
                BaseCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                            Text(
                                text = record.checkInTime.formatted("date"),
                                style = AppTypography.labelLarge
                            )
                            Text(
                                text = "Check-in: ${record.checkInTime.formatted("time")}",
                                style = AppTypography.bodySmall,
                                color = ColorPalette.textSecondary
                            )
                            record.checkOutTime?.let {
                                Text(
                                    text = "Check-out: ${it.formatted("time")}",
                                    style = AppTypography.bodySmall,
                                    color = ColorPalette.textSecondary
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(Spacing.xxs)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = record.status.color.copy(alpha = 0.1f),
                                        shape = Shapes.badge
                                    )
                                    .padding(horizontal = Spacing.sm, vertical = Spacing.xxs)
                            ) {
                                Text(
                                    text = record.status.displayName,
                                    style = AppTypography.labelSmall,
                                    color = record.status.color
                                )
                            }

                            record.hoursWorked?.let { hours ->
                                val formattedHours = (hours * 10).toInt() / 10.0
                                Text(
                                    text = "$formattedHours hrs",
                                    style = AppTypography.titleSmall,
                                    color = ColorPalette.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
