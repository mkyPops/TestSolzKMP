package com.testsolz.designsystem.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Shadow System
 * Elevation and shadow definitions for depth
 */
object Shadows {
    
    // MARK: - Elevation Levels
    val none: Dp = 0.dp
    val xs: Dp = 1.dp      // Subtle elevation (inputs, badges)
    val sm: Dp = 2.dp      // Small elevation (buttons)
    val md: Dp = 4.dp      // Medium elevation (cards) - MOST COMMON
    val lg: Dp = 8.dp      // Large elevation (modals, sheets)
    val xl: Dp = 12.dp     // Extra large elevation (popups)
    val xxl: Dp = 16.dp    // Massive elevation (overlays)
    
    // MARK: - Semantic Shadows (named by component)
    val button: Dp = sm           // 2dp - button elevation
    val card: Dp = md             // 4dp - card elevation
    val cardHover: Dp = lg        // 8dp - card on hover/press
    val modal: Dp = lg            // 8dp - modal sheets
    val dropdown: Dp = lg         // 8dp - dropdowns, menus
    val floating: Dp = xl         // 12dp - FABs, floating elements
}
