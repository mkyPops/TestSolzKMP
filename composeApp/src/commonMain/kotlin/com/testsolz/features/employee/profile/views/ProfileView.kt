package com.testsolz.features.employee.profile.views

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
 * Profile View
 * Employee profile screen with info and edit
 */
@Composable
fun ProfileView(
    user: User,
    onLogout: () -> Unit = {}
) {
    var employeeUser by remember(user.id) { mutableStateOf(user) }
    var isEditing by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val apiClient = remember { TestSolzApiClient() }
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose { apiClient.close() }
    }

    // Form states
    var name by remember(employeeUser) { mutableStateOf(employeeUser.name) }
    var email by remember(employeeUser) { mutableStateOf(employeeUser.email) }
    var phone by remember { mutableStateOf("") } // New contact info field

    val initials = employeeUser.name
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
                    text = if (isEditing) "Edit your profile" else "Your account details",
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
                                text = employeeUser.name,
                                style = AppTypography.titleLarge
                            )
                            Text(
                                text = employeeUser.email,
                                style = AppTypography.captionMedium,
                                color = ColorPalette.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.sm))

                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        InfoRow("Role", employeeUser.role.displayName)
                        InfoRow("Department", employeeUser.department ?: "N/A")
                        InfoRow("Contact", if (phone.isNotBlank()) phone else "Not set")
                        if (errorMessage != null) {
                            InfoRow("Error", errorMessage!!)
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
                            placeholder = "Update your email"
                        )

                        CustomTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = "Phone Number",
                            placeholder = "Update your contact info"
                        )

                        Spacer(modifier = Modifier.height(Spacing.sm))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                        ) {
                            OutlinedButton(
                                onClick = { isEditing = false },
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
                                                department = employeeUser.department
                                            )
                                            employeeUser = updatedUser
                                            AuthSession.updateUser(updatedUser)
                                            isEditing = false
                                        } catch (error: Exception) {
                                            errorMessage = error.message ?: "Failed to update profile"
                                        } finally {
                                            isSaving = false
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                isLoading = isSaving,
                                enabled = !isSaving && name.isNotBlank() && email.contains("@")
                            )
                        }
                    }
                }
            }
        }

        if (!isEditing) {
            Spacer(modifier = Modifier.height(Spacing.md))

            SecondaryButton(
                text = "Logout",
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Signed in as ${employeeUser.email}",
            style = AppTypography.captionSmall,
            color = ColorPalette.textTertiary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
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
