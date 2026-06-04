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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.testsolz.designsystem.theme.*
import com.testsolz.domain.models.User
import com.testsolz.features.admin.attendancemonitor.viewmodels.AdminEmployeesViewModel
import com.testsolz.shared.components.cards.BaseCard

/**
 * Employee List View
 * Monitor all employee attendance
 */
@Composable
fun EmployeeListView(
    sessionKey: String,
    viewModel: AdminEmployeesViewModel = viewModel(key = "admin-employees-$sessionKey")
) {
    var showAddEmployeeModal by remember { mutableStateOf(false) }
    var editingEmployee by remember { mutableStateOf<User?>(null) }
    var viewingHistoryEmployee by remember { mutableStateOf<User?>(null) }

    val employees by viewModel.employees.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

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
            onConfirm = { name, email, department, password, cardUid ->
                viewModel.createEmployee(
                    name = name,
                    email = email,
                    department = department,
                    password = password,
                    cardUid = cardUid,
                    onSuccess = { showAddEmployeeModal = false }
                )
            },
            isSaving = isSaving
        )
    }

    if (editingEmployee != null) {
        EditEmployeeModal(
            employee = editingEmployee!!,
            onDismiss = { editingEmployee = null },
            onConfirm = { name, department, password, cardUid ->
                viewModel.updateEmployee(
                    employee = editingEmployee!!,
                    name = name,
                    department = department,
                    password = password,
                    cardUid = cardUid,
                    onSuccess = { editingEmployee = null }
                )
            },
            isSaving = isSaving
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

        if (errorMessage != null) {
            item {
                BaseCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = errorMessage!!,
                        style = AppTypography.bodyMedium,
                        color = ColorPalette.error
                    )
                }
            }
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ColorPalette.primary)
                }
            }
        }

        if (!isLoading && employees.isEmpty()) {
            item {
                BaseCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No employees found",
                        style = AppTypography.bodyMedium,
                        color = ColorPalette.textSecondary
                    )
                }
            }
        }

        items(employees, key = { it.id }) { employee ->
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
                                onClick = { viewModel.deleteEmployee(employee) },
                                modifier = Modifier.size(32.dp),
                                enabled = !isSaving
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

                        Text(
                            text = employee.email,
                            style = AppTypography.bodySmall,
                            color = ColorPalette.textTertiary
                        )
                    }
                    Text(
                        text = employee.cardUid ?: "Card UID not assigned",
                        style = AppTypography.bodySmall,
                        color = if (employee.cardUid == null) ColorPalette.warning else ColorPalette.textSecondary
                    )
                }
            }
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}
