package com.testsolz.features.admin.dashboard.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.testsolz.designsystem.theme.*
import com.testsolz.features.admin.dashboard.viewmodels.EmployeeInfo
import com.testsolz.features.admin.dashboard.viewmodels.PendingRequestInfo
import com.testsolz.features.admin.dashboard.viewmodels.StatCategory
import com.testsolz.shared.components.cards.BaseCard

/**
 * Stat Detail View
 * Shows a detailed list of employees for a selected stat category
 */
@Composable
fun StatDetailView(
    category: StatCategory,
    employees: List<EmployeeInfo>,
    pendingRequests: List<PendingRequestInfo>,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.background)
            .padding(PaddingPresets.screen),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        // Back button + Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Radius.sm))
                        .clickable(onClick = onBack)
                        .padding(vertical = Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ColorPalette.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Dashboard",
                        style = AppTypography.labelMedium,
                        color = ColorPalette.primary
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Text(
                        text = category.title,
                        style = AppTypography.headlineLarge
                    )
                    Text(
                        text = when (category) {
                            StatCategory.PRESENT_TODAY -> "${employees.size} employees present"
                            StatCategory.ON_LEAVE -> "${employees.size} employees on leave"
                            StatCategory.LATE_TODAY -> "${employees.size} employees late"
                            StatCategory.PENDING_REQUESTS -> "${pendingRequests.size} pending requests"
                        },
                        style = AppTypography.bodyMedium,
                        color = ColorPalette.textSecondary
                    )
                }
            }
        }

        // Content
        if (category == StatCategory.PENDING_REQUESTS) {
            items(pendingRequests) { request ->
                PendingRequestListItem(request)
            }
        } else {
            items(employees) { employee ->
                EmployeeListItem(employee = employee, category = category)
            }
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}

@Composable
private fun EmployeeListItem(
    employee: EmployeeInfo,
    category: StatCategory
) {
    BaseCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            // Name and Role
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xxxs)
                ) {
                    Text(
                        text = employee.name,
                        style = AppTypography.titleMedium
                    )
                    Text(
                        text = employee.role,
                        style = AppTypography.bodySmall,
                        color = ColorPalette.textSecondary
                    )
                }

                // Status badge
                val statusColor = when (employee.status) {
                    "Present" -> ColorPalette.success
                    "On Leave" -> ColorPalette.warning
                    "Late" -> ColorPalette.error
                    else -> ColorPalette.textTertiary
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Radius.badge))
                        .background(statusColor.copy(alpha = 0.1f))
                        .padding(horizontal = Spacing.sm, vertical = Spacing.xxs)
                ) {
                    Text(
                        text = employee.status,
                        style = AppTypography.labelSmall,
                        color = statusColor
                    )
                }
            }

            // Department and Check-in time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = employee.department,
                    style = AppTypography.captionMedium,
                    color = ColorPalette.textSecondary
                )
                if (employee.checkInTime != null) {
                    Text(
                        text = "Checked in: ${employee.checkInTime}",
                        style = AppTypography.captionMedium,
                        color = ColorPalette.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun PendingRequestListItem(request: PendingRequestInfo) {
    BaseCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xxxs)
                ) {
                    Text(
                        text = request.employeeName,
                        style = AppTypography.titleMedium
                    )
                    Text(
                        text = request.department,
                        style = AppTypography.bodySmall,
                        color = ColorPalette.textSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Radius.badge))
                        .background(ColorPalette.warning.copy(alpha = 0.1f))
                        .padding(horizontal = Spacing.sm, vertical = Spacing.xxs)
                ) {
                    Text(
                        text = "Pending",
                        style = AppTypography.labelSmall,
                        color = ColorPalette.warning
                    )
                }
            }

            Text(
                text = request.requestType,
                style = AppTypography.labelMedium,
                color = ColorPalette.primary
            )

            Text(
                text = request.date,
                style = AppTypography.bodySmall,
                color = ColorPalette.textSecondary
            )

            Text(
                text = request.reason,
                style = AppTypography.bodyMedium
            )
        }
    }
}
