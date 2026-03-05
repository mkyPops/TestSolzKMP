package com.testsolz.features.admin.attendancemonitor.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.testsolz.designsystem.theme.*
import com.testsolz.domain.models.User
import com.testsolz.domain.models.UserRole
import com.testsolz.shared.components.cards.BaseCard

/**
 * Employee List View
 * Monitor all employee attendance
 */
@Composable
fun EmployeeListView() {
    // Mock employee list (dummy "Checked In" statuses; IoT integration later)
    val employees = listOf(
        User.mock,
        User(
            id = "2",
            email = "sarah@testsolz.com",
            name = "Sarah Wilson",
            role = UserRole.EMPLOYEE,
            department = "Design"
        ),
        User(
            id = "3",
            email = "mike@testsolz.com",
            name = "Mike Johnson",
            role = UserRole.EMPLOYEE,
            department = "Marketing"
        ),
        User(
            id = "4",
            email = "jane@testsolz.com",
            name = "Jane Smith",
            role = UserRole.EMPLOYEE,
            department = "Engineering"
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.background)
            .padding(PaddingPresets.screen),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        // Header
        item {
            Text(
                text = "Employees",
                style = AppTypography.headlineLarge
            )
        }

        items(employees) { employee ->
            val isLate = employee.id.toIntOrNull()?.rem(2) == 0
            BaseCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { /* TODO: View employee details */ }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Text(
                        text = employee.name,
                        style = AppTypography.titleMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = employee.department ?: "N/A",
                            style = AppTypography.bodySmall,
                            color = ColorPalette.textSecondary
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = ColorPalette.statusCheckedIn.copy(alpha = 0.12f),
                                        shape = Shapes.badge
                                    )
                                    .padding(horizontal = Spacing.sm, vertical = Spacing.xxs)
                            ) {
                                Text(
                                    text = "Checked In",
                                    style = AppTypography.labelSmall,
                                    color = ColorPalette.statusCheckedIn
                                )
                            }

                            val statusText = if (isLate) "Late" else "On Time"
                            val statusColor = if (isLate) ColorPalette.error else ColorPalette.primary
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = statusColor.copy(alpha = 0.12f),
                                        shape = Shapes.badge
                                    )
                                    .padding(horizontal = Spacing.sm, vertical = Spacing.xxs)
                            ) {
                                Text(
                                    text = statusText,
                                    style = AppTypography.labelSmall,
                                    color = statusColor
                                )
                            }
                        }
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
