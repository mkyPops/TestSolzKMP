package com.testsolz.features.admin.attendancemonitor.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    var showAddEmployeeModal by remember { mutableStateOf(false) }
    var editingEmployee by remember { mutableStateOf<User?>(null) }
    var viewingHistoryEmployee by remember { mutableStateOf<User?>(null) }

    // Use mutableStateListOf to make the list reactive
    val employees = remember {
        mutableStateListOf(
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
    }

    if (viewingHistoryEmployee != null) {
        EmployeeHistoryView(
            employee = viewingHistoryEmployee!!,
            onBack = { viewingHistoryEmployee = null }
        )
        return
    }

    if (showAddEmployeeModal) {
        AddEmployeeModal(
            onDismiss = { showAddEmployeeModal = false },
            onConfirm = { name, email, department ->
                val newId = (employees.size + 1).toString()
                val newEmployee = User(
                    id = newId,
                    email = email,
                    name = name,
                    role = UserRole.EMPLOYEE,
                    department = department
                )
                employees.add(newEmployee)
                showAddEmployeeModal = false
            }
        )
    }

    if (editingEmployee != null) {
        EditEmployeeModal(
            employee = editingEmployee!!,
            onDismiss = { editingEmployee = null },
            onConfirm = { name, email, department ->
                val index = employees.indexOfFirst { it.id == editingEmployee!!.id }
                if (index != -1) {
                    employees[index] = editingEmployee!!.copy(
                        name = name,
                        email = email,
                        department = department
                    )
                }
                editingEmployee = null
            }
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Employees",
                    style = AppTypography.headlineLarge
                )

                IconButton(
                    onClick = { showAddEmployeeModal = true },
                    modifier = Modifier
                        .background(
                            color = ColorPalette.primary.copy(alpha = 0.1f),
                            shape = Shapes.badge
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Employee",
                        tint = ColorPalette.primary
                    )
                }
            }
        }

        items(employees, key = { it.id }) { employee ->
            val isLate = employee.id.toIntOrNull()?.rem(2) == 0
            BaseCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = employee.name,
                            style = AppTypography.titleMedium
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                            IconButton(
                                onClick = { viewingHistoryEmployee = employee },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "Attendance History",
                                    tint = ColorPalette.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(
                                onClick = { editingEmployee = employee },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = ColorPalette.textSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(
                                onClick = { employees.remove(employee) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = ColorPalette.error,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
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
