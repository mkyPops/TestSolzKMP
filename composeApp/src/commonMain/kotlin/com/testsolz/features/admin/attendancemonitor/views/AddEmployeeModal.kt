package com.testsolz.features.admin.attendancemonitor.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.testsolz.designsystem.theme.*
import com.testsolz.shared.components.buttons.PrimaryButton
import com.testsolz.shared.components.input.CustomTextField

@Composable
fun AddEmployeeModal(
    onDismiss: () -> Unit,
    onConfirm: (name: String, email: String, department: String, password: String, cardUid: String?) -> Unit,
    isSaving: Boolean = false
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var cardUid by remember { mutableStateOf("") }

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
                    text = "Add New Employee",
                    style = AppTypography.headlineSmall
                )

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
                    label = "Temporary Password",
                    placeholder = "Minimum 6 characters",
                    isPassword = true
                )

                CustomTextField(
                    value = cardUid,
                    onValueChange = { cardUid = it.formatCardUidInput() },
                    label = "Card UID",
                    placeholder = "Optional, e.g. 00 00 00 00"
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
                        text = "Add Employee",
                        onClick = { onConfirm(name, email, department, password, cardUid.takeIf { it.isNotBlank() }) },
                        modifier = Modifier.weight(1f),
                        isLoading = isSaving,
                        enabled = !isSaving &&
                            name.isNotBlank() &&
                            email.contains("@") &&
                            department.isNotBlank() &&
                            password.length >= 6
                    )
                }
            }
        }
    }
}

private fun String.formatCardUidInput(): String {
    val compact = filter { it.isLetterOrDigit() }.uppercase().take(8)
    return compact.chunked(2).joinToString(" ")
}
