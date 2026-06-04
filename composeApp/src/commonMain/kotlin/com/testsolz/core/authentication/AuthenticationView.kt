package com.testsolz.core.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.testsolz.designsystem.theme.*
import com.testsolz.shared.components.buttons.PrimaryButton
import com.testsolz.shared.components.input.CustomTextField

/**
 * Authentication View
 * Login screen with email/password input
 */
@Composable
fun AuthenticationView(
    viewModel: AuthenticationViewModel = viewModel()
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingPresets.screen),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            // Logo/Title section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Text(
                    text = "TestSolz",
                    style = AppTypography.displayMedium,
                    color = ColorPalette.primary
                )
                
                Text(
                    text = "Employee Attendance System",
                    style = AppTypography.bodyMedium,
                    color = ColorPalette.textSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.xl))
            
            // Login form
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                CustomTextField(
                    value = email,
                    onValueChange = { viewModel.updateEmail(it) },
                    label = "Email",
                    placeholder = "your.email@testsolz.com",
                    keyboardType = KeyboardType.Email,
                    enabled = !isLoading
                )
                
                CustomTextField(
                    value = password,
                    onValueChange = { viewModel.updatePassword(it) },
                    label = "Password",
                    placeholder = "Enter your password",
                    isPassword = true,
                    keyboardType = KeyboardType.Password,
                    enabled = !isLoading
                )
                
                // Error message
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        style = AppTypography.bodySmall,
                        color = ColorPalette.error
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                PrimaryButton(
                    text = "Login",
                    onClick = { viewModel.login() },
                    isLoading = isLoading
                )
            }
            
        }
    }
}
