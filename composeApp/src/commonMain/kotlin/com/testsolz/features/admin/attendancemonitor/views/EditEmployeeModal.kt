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
import com.testsolz.domain.models.User
import com.testsolz.shared.components.buttons.PrimaryButton
import com.testsolz.shared.components.input.CustomTextField

@Composable
fun EditEmployeeModal(
    employee: User,
    onDismiss: () -> Unit,
    onConfirm: (name: String, email: String, department: String) -> Unit
) {
    var name by remember { mutableStateOf(employee.name) }
    var email by remember { mutableStateOf(employee.email) }
    var department by remember { mutableStateOf(employee.department ?: "") }

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
                Text(
                    text = "Edit Employee",
                    style = AppTypography.headlineSmall
                )

                Text(
                    text = "Update the details for ${employee.name}.",
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

                Spacer(modifier = Modifier.height(Spacing.md))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = Shapes.button,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = ColorPalette.textSecondary
                        )
                    ) {
                        Text("Cancel", style = AppTypography.buttonMedium)
                    }

                    PrimaryButton(
                        text = "Save Changes",
                        onClick = { onConfirm(name, email, department) },
                        modifier = Modifier.weight(1f),
                        enabled = name.isNotBlank() && email.contains("@") && department.isNotBlank()
                    )
                }
            }
        }
    }
}
