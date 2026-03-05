package com.testsolz.designsystem.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Spacing System
 * Consistent spacing scale for the entire app
 * Based on 4dp base unit (like 8pt grid system)
 * Prevents random padding values scattered throughout code
 */
object Spacing {
    
    // MARK: - Base Spacing Scale
    val xxxs: Dp = 2.dp      // Micro spacing (icon padding, badges)
    val xxs: Dp = 4.dp       // Tiny spacing (between related elements)
    val xs: Dp = 8.dp        // Extra small (tight groups)
    val sm: Dp = 12.dp       // Small (compact layouts)
    val md: Dp = 16.dp       // Medium (default padding) - MOST COMMON
    val lg: Dp = 24.dp       // Large (section spacing)
    val xl: Dp = 32.dp       // Extra large (major sections)
    val xxl: Dp = 48.dp      // Double extra large (screen sections)
    val xxxl: Dp = 64.dp     // Massive spacing (hero sections)
    
    // MARK: - Semantic Spacing (named by purpose)
    
    // Card & Container Padding
    val cardPadding: Dp = md           // 16dp - standard card padding
    val cardPaddingCompact: Dp = sm    // 12dp - compact card padding
    val cardPaddingGenerous: Dp = lg   // 24dp - generous card padding
    
    // Screen Padding
    val screenHorizontal: Dp = md      // 16dp - left/right screen edges
    val screenVertical: Dp = lg        // 24dp - top/bottom screen edges
    
    // List & Stack Spacing
    val listItemSpacing: Dp = xs       // 8dp - between list items
    val stackSpacing: Dp = sm          // 12dp - between stacked elements
    val sectionSpacing: Dp = lg        // 24dp - between major sections
    
    // Button Spacing
    val buttonPaddingHorizontal: Dp = lg  // 24dp - button left/right padding
    val buttonPaddingVertical: Dp = sm    // 12dp - button top/bottom padding
    val buttonSpacing: Dp = xs            // 8dp - between buttons
    
    // Form & Input Spacing
    val inputPaddingHorizontal: Dp = md   // 16dp - input field padding
    val inputPaddingVertical: Dp = sm     // 12dp - input field padding
    val formFieldSpacing: Dp = md         // 16dp - between form fields
    val formSectionSpacing: Dp = xl       // 32dp - between form sections
    
    // Icon & Text Spacing
    val iconTextSpacing: Dp = xs          // 8dp - icon next to text
    val badgeSpacing: Dp = xxs            // 4dp - badge padding
    
    // Divider & Separator
    val dividerSpacing: Dp = md           // 16dp - around dividers
}

/**
 * Padding Presets
 * Common padding combinations for composables
 */
object PaddingPresets {
    
    // Card Padding
    val card = PaddingValues(all = Spacing.cardPadding)
    val cardCompact = PaddingValues(all = Spacing.cardPaddingCompact)
    val cardGenerous = PaddingValues(all = Spacing.cardPaddingGenerous)
    
    // Screen Padding
    val screen = PaddingValues(
        horizontal = Spacing.screenHorizontal,
        vertical = Spacing.screenVertical
    )
    
    val screenHorizontalOnly = PaddingValues(horizontal = Spacing.screenHorizontal)
    val screenVerticalOnly = PaddingValues(vertical = Spacing.screenVertical)
    
    // Button Padding
    val button = PaddingValues(
        horizontal = Spacing.buttonPaddingHorizontal,
        vertical = Spacing.buttonPaddingVertical
    )
    
    val buttonCompact = PaddingValues(
        horizontal = Spacing.md,
        vertical = Spacing.xs
    )
    
    // Input Field Padding
    val input = PaddingValues(
        horizontal = Spacing.inputPaddingHorizontal,
        vertical = Spacing.inputPaddingVertical
    )
}
