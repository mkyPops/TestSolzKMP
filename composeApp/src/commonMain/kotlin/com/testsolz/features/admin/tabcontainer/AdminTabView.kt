package com.testsolz.features.admin.tabcontainer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.testsolz.core.authentication.AuthenticationViewModel
import com.testsolz.core.navigation.AppTab
import com.testsolz.designsystem.theme.ColorPalette
import com.testsolz.features.admin.attendancemonitor.views.EmployeeListView
import com.testsolz.features.admin.dashboard.views.AdminDashboardView
import com.testsolz.features.admin.profile.views.AdminProfileView
import com.testsolz.features.admin.requests.views.AdminRequestsView
import com.testsolz.shared.components.navigation.CustomTabBar

/**
 * Admin Tab View
 * Main tab container for admin screens
 */
@Composable
fun AdminTabView(
    authViewModel: AuthenticationViewModel
) {
    var selectedTab by remember { mutableStateOf(AppTab.DASHBOARD) }

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
                    AppTab.DASHBOARD -> AdminDashboardView()
                    AppTab.EMPLOYEES -> EmployeeListView()
                    AppTab.ADMIN_REQUESTS -> AdminRequestsView()
                    AppTab.ADMIN_PROFILE -> AdminProfileView(
                        user = currentUser ?: com.testsolz.domain.models.User.mockAdmin,
                        onLogout = { authViewModel.logout() }
                    )
                    else -> {}
                }
            }

            // Tab bar
            CustomTabBar(
                tabs = AppTab.adminTabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    }
}
