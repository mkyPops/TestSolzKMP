package com.testsolz.features.employee.tabcontainer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.testsolz.core.authentication.AuthenticationViewModel
import com.testsolz.core.navigation.AppTab
import com.testsolz.designsystem.theme.*
import com.testsolz.features.employee.attendancehistory.views.AttendanceHistoryView
import com.testsolz.features.employee.employeehome.views.EmployeeHomeView
import com.testsolz.features.employee.profile.views.ProfileView
import com.testsolz.features.employee.requests.views.EmployeeRequestsView
import com.testsolz.shared.components.cards.BaseCard
import com.testsolz.shared.components.navigation.CustomTabBar

/**
 * Employee Tab View
 * Main tab container for employee screens
 */
@Composable
fun EmployeeTabView(
    authViewModel: AuthenticationViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val sessionKey = currentUser?.id ?: "anonymous"
    var selectedTab by remember(sessionKey) { mutableStateOf(AppTab.HOME) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Content based on selected tab
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    AppTab.HOME -> EmployeeHomeView(sessionKey = sessionKey)
                    AppTab.HISTORY -> AttendanceHistoryView(sessionKey = sessionKey)
                    AppTab.REQUESTS -> EmployeeRequestsView(sessionKey = sessionKey)
                    AppTab.PROFILE -> currentUser?.let { user ->
                        ProfileView(user = user, onLogout = { authViewModel.logout() })
                    } ?: LoadingUserCard()
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

@Composable
private fun LoadingUserCard() {
    Box(modifier = Modifier.fillMaxSize().padding(PaddingPresets.screen)) {
        BaseCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Loading profile...",
                style = AppTypography.bodyMedium,
                color = ColorPalette.textSecondary
            )
        }
    }
}
