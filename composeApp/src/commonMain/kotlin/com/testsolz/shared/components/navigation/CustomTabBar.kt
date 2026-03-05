package com.testsolz.shared.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.testsolz.core.navigation.AppTab
import com.testsolz.designsystem.theme.*

/**
 * Custom Tab Bar
 * Bottom navigation bar for app tabs
 */
@Composable
fun CustomTabBar(
    tabs: List<AppTab>,
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = Shadows.card,
                shape = RoundedCornerShape(topStart = Radius.lg, topEnd = Radius.lg)
            )
            .background(ColorPalette.surface)
            .padding(vertical = Spacing.sm, horizontal = Spacing.md),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEach { tab ->
            TabItem(
                tab = tab,
                isSelected = tab == selectedTab,
                onClick = { onTabSelected(tab) }
            )
        }
    }
}

@Composable
private fun TabItem(
    tab: AppTab,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val icon = if (isSelected) tab.filledIcon() else tab.outlinedIcon()
    val tint = if (isSelected) ColorPalette.primary else ColorPalette.textTertiary

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(Radius.sm))
            .clickable(onClick = onClick)
            .padding(Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = tab.title,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        
        Text(
            text = tab.title,
            style = AppTypography.captionMedium,
            color = if (isSelected) ColorPalette.primary else ColorPalette.textSecondary
        )
    }
}

private fun AppTab.outlinedIcon(): ImageVector = when (this) {
    AppTab.HOME -> Icons.Outlined.Home
    AppTab.HISTORY -> Icons.AutoMirrored.Outlined.EventNote
    AppTab.REQUESTS -> Icons.Outlined.Description
    AppTab.PROFILE -> Icons.Outlined.Person
    AppTab.DASHBOARD -> Icons.Outlined.BarChart
    AppTab.EMPLOYEES -> Icons.Outlined.Group
    AppTab.ADMIN_REQUESTS -> Icons.Outlined.Description
}

private fun AppTab.filledIcon(): ImageVector = when (this) {
    AppTab.HOME -> Icons.Filled.Home
    AppTab.HISTORY -> Icons.AutoMirrored.Filled.EventNote
    AppTab.REQUESTS -> Icons.Filled.Description
    AppTab.PROFILE -> Icons.Filled.Person
    AppTab.DASHBOARD -> Icons.Filled.BarChart
    AppTab.EMPLOYEES -> Icons.Filled.Group
    AppTab.ADMIN_REQUESTS -> Icons.Filled.Description
}
