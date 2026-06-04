package com.testsolz.shared.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.testsolz.designsystem.theme.*
import com.testsolz.domain.models.LeaveRequest
import com.testsolz.domain.models.RequestType

/**
 * Request Card
 * Displays leave/late request information
 */
@Composable
fun RequestCard(
    request: LeaveRequest,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    BaseCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            // Header with status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = request.userName,
                        style = AppTypography.titleMedium
                    )

                    if (request.userDepartment != null) {
                        Text(
                            text = request.userDepartment,
                            style = AppTypography.bodySmall,
                            color = ColorPalette.textSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Radius.badge))
                        .background(request.status.color.copy(alpha = 0.1f))
                        .padding(horizontal = Spacing.sm, vertical = Spacing.xxs)
                ) {
                    Text(
                        text = request.status.displayName,
                        style = AppTypography.labelSmall,
                        color = request.status.color
                    )
                }
            }

            // Request type and details
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = request.type.displayName,
                    style = AppTypography.labelMedium,
                    color = ColorPalette.primary
                )

                if (request.type == RequestType.LEAVE && request.leaveType != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Radius.badge))
                            .background(request.leaveType.color.copy(alpha = 0.1f))
                            .padding(horizontal = Spacing.xs, vertical = Spacing.xxxs)
                    ) {
                        Text(
                            text = request.leaveType.displayName,
                            style = AppTypography.captionSmall,
                            color = request.leaveType.color
                        )
                    }
                }
            }

            // Date range or expected time
            Text(
                text = if (request.type == RequestType.LEAVE) {
                    request.formattedDateRange + (request.daysCount?.let { " ($it days)" } ?: "")
                } else {
                    "Expected: ${request.expectedTime ?: ""}"
                },
                style = AppTypography.bodySmall,
                color = ColorPalette.textSecondary
            )

            // Reason
            if (request.reason.isNotEmpty()) {
                Text(
                    text = request.reason,
                    style = AppTypography.bodyMedium
                )
            }

            // Admin comment (if any)
            val adminComment = request.adminComment
            if (adminComment != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Radius.xs))
                        .background(ColorPalette.backgroundSecondary)
                        .padding(Spacing.sm)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Admin Comment:",
                            style = AppTypography.labelSmall,
                            color = ColorPalette.textSecondary
                        )
                        Text(
                            text = adminComment,
                            style = AppTypography.bodySmall
                        )
                    }
                }
            }

            if (onDelete != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    androidx.compose.material3.TextButton(onClick = onDelete) {
                        Text(
                            text = "Delete",
                            style = AppTypography.labelMedium,
                            color = ColorPalette.error
                        )
                    }
                }
            }
        }
    }
}
