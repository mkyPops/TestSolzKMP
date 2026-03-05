package com.testsolz.features.admin.requests.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.testsolz.designsystem.theme.*
import com.testsolz.domain.models.RequestStatus
import com.testsolz.features.admin.requests.viewmodels.AdminRequestsViewModel
import com.testsolz.shared.components.cards.BaseCard
import com.testsolz.shared.components.cards.RequestCard

/**
 * Admin Requests View
 * View and manage all employee requests
 */
@Composable
fun AdminRequestsView(
    viewModel: AdminRequestsViewModel = viewModel()
) {
    val requests by viewModel.requests.collectAsState()
    val pendingRequests = requests.filter { it.status == RequestStatus.PENDING }

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

        // Pending Requests
        if (pendingRequests.isNotEmpty()) {
            item {
                Text(
                    text = "Pending Requests",
                    style = AppTypography.headlineMedium
                )
            }

            items(pendingRequests) { request ->
                RequestCard(
                    request = request,
                    onClick = { /* TODO: Review request */ }
                )
            }
        }

        // All Requests
        item {
            Text(
                text = "All Requests",
                style = AppTypography.headlineMedium
            )
        }

        if (requests.isEmpty()) {
            item {
                BaseCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No requests yet",
                        style = AppTypography.bodyMedium,
                        color = ColorPalette.textSecondary
                    )
                }
            }
        } else {
            items(requests) { request ->
                RequestCard(
                    request = request,
                    onClick = { /* TODO: View details */ }
                )
            }
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}
