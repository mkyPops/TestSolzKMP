package com.testsolz.features.admin.profile.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.testsolz.core.authentication.AuthSession
import com.testsolz.core.network.TestSolzApiClient
import com.testsolz.designsystem.theme.*
import com.testsolz.domain.models.User
import com.testsolz.shared.components.buttons.PrimaryButton
import com.testsolz.shared.components.buttons.SecondaryButton
import com.testsolz.shared.components.cards.BaseCard
import com.testsolz.shared.components.input.CustomTextField
import kotlinx.coroutines.launch

/**
 * Admin Profile View
 * Admin profile screen with info, edit and logout
 */
@Composable
fun AdminProfileView(
    user: User,
    onLogout: () -> Unit = {}
) {
    var adminUser by remember(user.id) { mutableStateOf(user) }
    var isEditing by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val apiClient = remember { TestSolzApiClient() }
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose { apiClient.close() }
    }

    // Form states
    var name by remember(adminUser) { mutableStateOf(adminUser.name) }
    var email by remember(adminUser) { mutableStateOf(adminUser.email) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val initials = adminUser.name
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.background)
            .padding(PaddingPresets.screen)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = "Profile",
                    style = AppTypography.headlineLarge
                )
                Text(
                    text = if (isEditing) "Update your credentials" else "Your account details",
                    style = AppTypography.bodySmall,
                    color = ColorPalette.textSecondary
                )
            }

            if (!isEditing) {
                TextButton(onClick = { isEditing = true }) {
                    Text(
                        text = "Edit",
                        style = AppTypography.labelLarge,
                        color = ColorPalette.primary
                    )
                }
            }
        }

        // Profile card
        BaseCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                if (!isEditing) {
                    // View Mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(ColorPalette.primaryLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                style = AppTypography.titleMedium,
                                color = ColorPalette.primary
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxxs)) {
                            Text(
                                text = adminUser.name,
                                style = AppTypography.titleLarge
                            )
                            Text(
                                text = adminUser.email,
                                style = AppTypography.captionMedium,
                                color = ColorPalette.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.sm))

                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        AdminInfoRow("Role", adminUser.role.displayName)
                        AdminInfoRow("Department", adminUser.department ?: "N/A")
                        AdminInfoRow("Employee ID", adminUser.id)
                        if (errorMessage != null) {
                            AdminInfoRow("Error", errorMessage!!)
                        }
                    }
                } else {
                    // Edit Mode
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        CustomTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Full Name",
                            placeholder = "Update your name"
                        )

                        CustomTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email Address",
                            placeholder = "Update your email",
                            keyboardType = KeyboardType.Email
                        )

                        Divider(modifier = Modifier.padding(vertical = Spacing.xs))

                        Text(
                            text = "Change Password (Optional)",
                            style = AppTypography.labelMedium,
                            color = ColorPalette.textSecondary
                        )

                        CustomTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "New Password",
                            placeholder = "Enter new password",
                            isPassword = true
                        )

                        CustomTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Confirm Password",
                            placeholder = "Confirm new password",
                            isPassword = true,
                            isError = password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword,
                            errorMessage = if (password != confirmPassword) "Passwords do not match" else null
                        )

                        Spacer(modifier = Modifier.height(Spacing.sm))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                        ) {
                            OutlinedButton(
                                onClick = { 
                                    isEditing = false
                                    password = ""
                                    confirmPassword = ""
                                },
                                modifier = Modifier.weight(1f).height(56.dp),
                                shape = Shapes.button
                            ) {
                                Text("Cancel", style = AppTypography.buttonMedium)
                            }

                            PrimaryButton(
                                text = "Save",
                                onClick = {
                                    coroutineScope.launch {
                                        isSaving = true
                                        errorMessage = null
                                        try {
                                            val updatedUser = apiClient.updateMe(
                                                name = name,
                                                department = adminUser.department,
                                                password = password.takeIf { it.isNotBlank() }
                                            )
                                            adminUser = updatedUser
                                            AuthSession.updateUser(updatedUser)
                                            isEditing = false
                                            password = ""
                                            confirmPassword = ""
                                        } catch (error: Exception) {
                                            errorMessage = error.message ?: "Failed to update profile"
                                        } finally {
                                            isSaving = false
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                isLoading = isSaving,
                                enabled = !isSaving && name.isNotBlank() && email.contains("@") &&
                                          (password.isEmpty() || (password == confirmPassword && password.length >= 6))
                            )
                        }
                    }
                }
            }
        }

        if (!isEditing) {
            Spacer(modifier = Modifier.height(Spacing.md))

            // Logout button
            SecondaryButton(
                text = "Logout",
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Signed in as ${adminUser.email}",
            style = AppTypography.captionSmall,
            color = ColorPalette.textTertiary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AdminInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = AppTypography.labelMedium,
            color = ColorPalette.textSecondary
        )
        Text(
            text = value,
            style = AppTypography.bodyMedium
        )
    }
}
