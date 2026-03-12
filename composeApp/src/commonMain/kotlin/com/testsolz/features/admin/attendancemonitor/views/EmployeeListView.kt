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
import com.testsolz.features.admin.attendancemonitor.viewmodels.EmployeeViewModel
import com.testsolz.network.models.Employee
import com.testsolz.shared.components.cards.BaseCard

/**
 * Employee List View
 * Monitor all employee attendance
 */
@Composable
fun EmployeeListView() {
    val viewModel: EmployeeViewModel = viewModel()
    
    var showAddEmployeeModal by remember { mutableStateOf(false) }
    var editingEmployee by remember { mutableStateOf<Employee?>(null) }
    var viewingHistoryEmployee by remember { mutableStateOf<Employee?>(null) }

    val employees by viewModel.employees
    val isLoading by viewModel.isLoading
    val error by viewModel.error
    val isLoggedIn by viewModel.isLoggedIn

    // Auto-login for testing/development
    LaunchedEffect(Unit) {
        viewModel.login("admin@testsolz.com", "admin123")
    }

    if (viewingHistoryEmployee != null) {
        // EmployeeHistoryView integration
    }

    if (showAddEmployeeModal) {
        AddEmployeeModal(
            viewModel = viewModel,
            onDismiss = { showAddEmployeeModal = false }
        )
    }

    if (editingEmployee != null) {
        // EditEmployeeModal integration
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        enabled = isLoggedIn,
                        modifier = Modifier
                            .background(
                                color = if (isLoggedIn) ColorPalette.primary.copy(alpha = 0.1f)
                                        else ColorPalette.textSecondary.copy(alpha = 0.1f),
                                shape = Shapes.badge
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Employee",
                            tint = if (isLoggedIn) ColorPalette.primary else ColorPalette.textSecondary
                        )
                    }
                }
            }

            if (isLoading && employees.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(Spacing.xl), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            error?.let {
                item {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(Spacing.md)
                    )
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
                                IconButton(onClick = { viewingHistoryEmployee = employee }) {
                                    Icon(Icons.Default.History, null, tint = ColorPalette.primary)
                                }
                                IconButton(onClick = { editingEmployee = employee }) {
                                    Icon(Icons.Default.Edit, null, tint = ColorPalette.textSecondary)
                                }
                                IconButton(onClick = { /* Delete logic */ }) {
                                    Icon(Icons.Default.Delete, null, tint = ColorPalette.error)
                                }
                            }
                        }

                        Text(
                            text = employee.department,
                            style = AppTypography.bodySmall,
                            color = ColorPalette.textSecondary
                        )
                    }
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(Spacing.xl))
            }
        }
        
        if (isLoading && employees.isNotEmpty()) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
        }
    }
}
