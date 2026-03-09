package com.testsolz.features.admin.requests.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.testsolz.designsystem.theme.*
import com.testsolz.domain.models.LeaveRequest
import com.testsolz.domain.models.RequestType

/**
 * Request Detail Modal
 * Shows request details with Approve/Reject actions
 */
@Composable
fun RequestDetailModal(
    request: LeaveRequest,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = Shadows.modal,
                    shape = RoundedCornerShape(Radius.modal),
                    clip = false
                )
                .clip(RoundedCornerShape(Radius.modal))
                .background(ColorPalette.surface)
                .padding(Spacing.lg)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Request Details",
                        style = AppTypography.headlineMedium
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(Radius.sm))
                            .background(ColorPalette.backgroundTertiary)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = ColorPalette.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(ColorPalette.divider)
                )

                // Employee name and department
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxxs)) {
                    Text(
                        text = request.userName,
                        style = AppTypography.titleLarge
                    )
                    if (request.userDepartment != null) {
                        Text(
                            text = request.userDepartment,
                            style = AppTypography.bodyMedium,
                            color = ColorPalette.textSecondary
                        )
                    }
                }

                // Request info rows
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Radius.sm))
                        .background(ColorPalette.backgroundSecondary)
                        .padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    ModalInfoRow("Type", request.type.displayName)

                    if (request.type == RequestType.LEAVE && request.leaveType != null) {
                        ModalInfoRow("Leave Type", request.leaveType.displayName)
                    }

                    ModalInfoRow("Date", request.formattedDateRange)

                    if (request.daysCount != null) {
                        ModalInfoRow("Duration", "${request.daysCount} day(s)")
                    }

                    if (request.expectedTime != null) {
                        ModalInfoRow("Expected Time", request.expectedTime)
                    }
                }

                // Reason
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                    Text(
                        text = "Reason",
                        style = AppTypography.labelMedium,
                        color = ColorPalette.textSecondary
                    )
                    Text(
                        text = request.reason,
                        style = AppTypography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xs))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    // Reject button (red)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(Radius.button))
                            .background(ColorPalette.error)
                            .clickable(onClick = onReject),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Reject",
                            style = AppTypography.buttonMedium,
                            color = ColorPalette.textOnPrimary
                        )
                    }

                    // Approve button (green)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(Radius.button))
                            .background(ColorPalette.success)
                            .clickable(onClick = onApprove),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Approve",
                            style = AppTypography.buttonMedium,
                            color = ColorPalette.textOnPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModalInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = AppTypography.labelMedium,
            color = ColorPalette.textSecondary
        )
        Text(
            text = value,
            style = AppTypography.bodyMedium
        )
    }
}
