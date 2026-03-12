package com.testsolz.features.admin.attendancemonitor.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.testsolz.designsystem.theme.*
import com.testsolz.features.admin.attendancemonitor.viewmodels.EmployeeViewModel
import com.testsolz.shared.components.buttons.PrimaryButton
import com.testsolz.shared.components.input.CustomTextField

@Composable
fun AddEmployeeModal(
    viewModel: EmployeeViewModel,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading
    val error by viewModel.error
    val employeeAdded by viewModel.employeeAdded

    // Auto-close modal when employee is added successfully
    LaunchedEffect(employeeAdded) {
        if (employeeAdded) {
            viewModel.resetEmployeeAdded()
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(Shapes.modal)
                .background(ColorPalette.surface)
                .padding(Spacing.lg)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Text(text = "Add New Employee", style = AppTypography.headlineSmall)

                Text(
                    text = "Enter the details for the new employee account.",
                    style = AppTypography.bodyMedium,
                    color = ColorPalette.textSecondary
                )

                Spacer(modifier = Modifier.height(Spacing.xs))

                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Full Name",
                    placeholder = "e.g. John Doe"
                )

                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                    placeholder = "e.g. john@testsolz.com"
                )

                CustomTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = "Department",
                    placeholder = "e.g. Engineering"
                )

                CustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "Min 6 characters"
                )

                // Show error if any
                error?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, style = AppTypography.bodySmall)
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = Shapes.button,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = ColorPalette.textSecondary
                        )
                    ) {
                        Text("Cancel", style = AppTypography.buttonMedium)
                    }

                    PrimaryButton(
                        text = if (isLoading) "Adding..." else "Add Employee",
                        onClick = { viewModel.createEmployee(name, email, department, password) },
                        modifier = Modifier.weight(1f),
                        enabled = name.isNotBlank() && email.contains("@") && department.isNotBlank() && password.length >= 6 && !isLoading
                    )
                }
            }
        }
    }
}
