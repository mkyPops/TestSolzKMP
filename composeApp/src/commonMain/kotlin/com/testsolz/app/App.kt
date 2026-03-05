package com.testsolz.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.testsolz.core.authentication.AuthenticationView
import com.testsolz.core.authentication.AuthenticationViewModel
import com.testsolz.designsystem.theme.AppTheme
import com.testsolz.domain.models.UserRole
import com.testsolz.features.admin.tabcontainer.AdminTabView
import com.testsolz.features.employee.tabcontainer.EmployeeTabView

/**
 * TestSolz App
 * Main application entry point
 */
@Composable
fun App() {
    val authViewModel: AuthenticationViewModel = viewModel()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    AppTheme {
        when {
            !isAuthenticated -> {
                AuthenticationView(authViewModel)
            }
            currentUser?.role == UserRole.ADMIN -> {
                AdminTabView(authViewModel)
            }
            else -> {
                EmployeeTabView(authViewModel)
            }
        }
    }
}
