package com.testsolz.features.admin.tabcontainer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.testsolz.core.authentication.AuthenticationViewModel
import com.testsolz.core.navigation.AppTab
import com.testsolz.designsystem.theme.*
import com.testsolz.features.admin.attendancemonitor.views.EmployeeListView
import com.testsolz.features.admin.dashboard.views.AdminDashboardView
import com.testsolz.features.admin.profile.views.AdminProfileView
import com.testsolz.features.admin.requests.views.AdminRequestsView
import com.testsolz.shared.components.cards.BaseCard
import com.testsolz.shared.components.navigation.CustomTabBar

/**
 * Admin Tab View
 * Main tab container for admin screens
 */
@Composable
fun AdminTabView(
    authViewModel: AuthenticationViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val sessionKey = currentUser?.id ?: "anonymous"
    var selectedTab by remember(sessionKey) { mutableStateOf(AppTab.DASHBOARD) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Content based on selected tab
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    AppTab.DASHBOARD -> AdminDashboardView(sessionKey = sessionKey)
                    AppTab.EMPLOYEES -> EmployeeListView(sessionKey = sessionKey)
                    AppTab.ADMIN_REQUESTS -> AdminRequestsView(sessionKey = sessionKey)
                    AppTab.ADMIN_PROFILE -> currentUser?.let { user ->
                        AdminProfileView(user = user, onLogout = { authViewModel.logout() })
                    } ?: LoadingAdminCard()
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

@Composable
private fun LoadingAdminCard() {
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
