package com.testsolz.features.admin.requests.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.testsolz.designsystem.theme.*
import com.testsolz.domain.models.RequestStatus
import com.testsolz.features.admin.requests.viewmodels.AdminRequestsViewModel
import com.testsolz.shared.components.cards.BaseCard
import com.testsolz.shared.components.cards.RequestCard

/**
 * Admin Requests View
 * View and manage all employee requests with Pending/Reviewed tabs
 */
@Composable
fun AdminRequestsView(
    viewModel: AdminRequestsViewModel = viewModel()
) {
    val pendingRequests by viewModel.requests.collectAsState()
    val reviewedRequests by viewModel.reviewedRequests.collectAsState()
    val selectedRequest by viewModel.selectedRequest.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }

    // Show modal when a request is selected
    if (selectedRequest != null) {
        RequestDetailModal(
            request = selectedRequest!!,
            onApprove = { viewModel.approveRequest(selectedRequest!!.id) },
            onReject = { viewModel.rejectRequest(selectedRequest!!.id) },
            onDismiss = { viewModel.clearSelectedRequest() }
        )
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
                    text = "Request Management",
                    style = AppTypography.headlineLarge
                )
                Text(
                    text = "${pendingRequests.size} pending requests",
                    style = AppTypography.bodyMedium,
                    color = ColorPalette.textSecondary
                )
            }
        }

        // Tab Row: Pending | Reviewed
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.sm))
                    .background(ColorPalette.backgroundTertiary)
                    .padding(Spacing.xxs),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xxs)
            ) {
                TabButton(
                    text = "Pending (${pendingRequests.size})",
                    isSelected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    text = "Reviewed (${reviewedRequests.size})",
                    isSelected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        when (selectedTabIndex) {
            0 -> {
                // Pending tab
                if (pendingRequests.isEmpty()) {
                    item {
                        BaseCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "No pending requests",
                                style = AppTypography.bodyMedium,
                                color = ColorPalette.textSecondary
                            )
                        }
                    }
                } else {
                    items(pendingRequests) { request ->
                        RequestCard(
                            request = request,
                            onClick = { viewModel.selectRequest(request) }
                        )
                    }
                }
            }
            1 -> {
                // Reviewed tab
                if (reviewedRequests.isEmpty()) {
                    item {
                        BaseCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "No reviewed requests yet",
                                style = AppTypography.bodyMedium,
                                color = ColorPalette.textSecondary
                            )
                        }
                    }
                } else {
                    items(reviewedRequests) { request ->
                        RequestCard(
                            request = request,
                            onClick = { /* Already reviewed */ }
                        )
                    }
                }
            }
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.xs))
            .background(
                if (isSelected) ColorPalette.surface else ColorPalette.backgroundTertiary
            )
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.sm),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = AppTypography.labelMedium,
            color = if (isSelected) ColorPalette.primary else ColorPalette.textSecondary
        )
    }
}
