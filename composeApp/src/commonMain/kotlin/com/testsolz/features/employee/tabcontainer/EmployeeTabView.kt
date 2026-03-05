package com.testsolz.features.employee.tabcontainer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.testsolz.core.authentication.AuthenticationViewModel
import com.testsolz.core.navigation.AppTab
import com.testsolz.designsystem.theme.ColorPalette
import com.testsolz.domain.models.User
import com.testsolz.features.employee.attendancehistory.views.AttendanceHistoryView
import com.testsolz.features.employee.employeehome.views.EmployeeHomeView
import com.testsolz.features.employee.profile.views.ProfileView
import com.testsolz.features.employee.requests.views.EmployeeRequestsView
import com.testsolz.shared.components.navigation.CustomTabBar

/**
 * Employee Tab View
 * Main tab container for employee screens
 */
@Composable
fun EmployeeTabView(
    authViewModel: AuthenticationViewModel
) {
    var selectedTab by remember { mutableStateOf(AppTab.HOME) }
    val currentUser by authViewModel.currentUser.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Content based on selected tab
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    AppTab.HOME -> EmployeeHomeView()
                    AppTab.HISTORY -> AttendanceHistoryView()
                    AppTab.REQUESTS -> EmployeeRequestsView()
                    AppTab.PROFILE -> ProfileView(
                        user = currentUser ?: User.mock,
                        onLogout = { authViewModel.logout() }
                    )
                    else -> {}
                }
            }

            // Tab bar
            CustomTabBar(
                tabs = AppTab.employeeTabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    }
}
