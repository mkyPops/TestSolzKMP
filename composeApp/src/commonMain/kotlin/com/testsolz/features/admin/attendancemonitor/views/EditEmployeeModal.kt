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
    onConfirm: (name: String, department: String, password: String?, cardUid: String?) -> Unit,
    isSaving: Boolean = false
) {
    var name by remember { mutableStateOf(employee.name) }
    var department by remember { mutableStateOf(employee.department ?: "") }
    var password by remember { mutableStateOf("") }
    var cardUid by remember { mutableStateOf(employee.cardUid ?: "") }

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

                        Text(
                            text = employee.email,
                            style = AppTypography.bodyMedium,
                            color = ColorPalette.textSecondary
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
                            label = "New Password (Optional)",
                            placeholder = "Leave blank to keep current password",
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
                        text = "Save Changes",
                        onClick = { onConfirm(name, department, password.takeIf { it.isNotBlank() }, cardUid) },
                        modifier = Modifier.weight(1f),
                        isLoading = isSaving,
                        enabled = !isSaving &&
                            name.isNotBlank() &&
                            department.isNotBlank() &&
                            (password.isBlank() || password.length >= 6)
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
