package com.testsolz.features.admin.dashboard.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.testsolz.designsystem.theme.*
import com.testsolz.features.admin.dashboard.viewmodels.AdminDashboardViewModel
import com.testsolz.shared.components.cards.MetricCard

/**
 * Admin Dashboard View
 * Overview metrics and analytics
 */
@Composable
fun AdminDashboardView(
    viewModel: AdminDashboardViewModel = viewModel()
) {
    val metrics by viewModel.metrics.collectAsState()

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
                    text = "Dashboard",
                    style = AppTypography.headlineLarge
                )
                Text(
                    text = "Overview of attendance and requests",
                    style = AppTypography.bodyMedium,
                    color = ColorPalette.textSecondary
                )
            }
        }

        // Metrics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    MetricCard(
                        label = "Total Employees",
                        value = metrics.totalEmployees.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        label = "Present Today",
                        value = metrics.presentToday.toString(),
                        subtitle = "${((metrics.presentToday.toFloat() / metrics.totalEmployees) * 100).toInt()}%",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    MetricCard(
                        label = "On Leave",
                        value = metrics.onLeave.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        label = "Late Today",
                        value = metrics.lateToday.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }

                MetricCard(
                    label = "Pending Requests",
                    value = metrics.pendingRequests.toString(),
                    subtitle = "Awaiting review",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { /* Navigate to requests */ }
                )
            }
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}
