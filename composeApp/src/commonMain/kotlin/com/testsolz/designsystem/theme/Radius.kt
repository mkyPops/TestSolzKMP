package com.testsolz.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Border Radius System
 * Consistent corner radius values for cards, buttons, inputs
 * Keeps the UI cohesive and professional
 */
object Radius {
    
    // MARK: - Base Radius Scale
    val none: Dp = 0.dp        // Sharp corners (rarely used)
    val xs: Dp = 4.dp          // Subtle rounding (badges, tags)
    val sm: Dp = 8.dp          // Small rounding (buttons, inputs)
    val md: Dp = 12.dp         // Medium rounding (cards, modals) - MOST COMMON
    val lg: Dp = 16.dp         // Large rounding (prominent cards)
    val xl: Dp = 20.dp         // Extra large (hero cards)
    val xxl: Dp = 24.dp        // Double extra large (special elements)
    val full: Dp = 999.dp      // Fully rounded (pills, circular buttons)
    
    // MARK: - Semantic Radius (named by component)
    val button: Dp = sm              // 8dp - standard button
    val buttonLarge: Dp = md         // 12dp - large CTA buttons
    val buttonPill: Dp = full        // Fully rounded pill buttons
    
    val card: Dp = md                // 12dp - standard cards
    val cardLarge: Dp = lg           // 16dp - prominent cards
    
    val input: Dp = sm               // 8dp - text fields
    val badge: Dp = xs               // 4dp - small badges
    val modal: Dp = lg               // 16dp - modals, sheets
    
    val avatar: Dp = full            // Circular avatars
    val avatarSquare: Dp = sm        // Rounded square avatars
}

/**
 * Shape Presets
 * Common shapes for composables
 */
object Shapes {
    val button = RoundedCornerShape(Radius.button)
    val buttonLarge = RoundedCornerShape(Radius.buttonLarge)
    val buttonPill = RoundedCornerShape(Radius.buttonPill)
    
    val card = RoundedCornerShape(Radius.card)
    val cardLarge = RoundedCornerShape(Radius.cardLarge)
    
    val input = RoundedCornerShape(Radius.input)
    val badge = RoundedCornerShape(Radius.badge)
    val modal = RoundedCornerShape(Radius.modal)
    
    val avatar = RoundedCornerShape(Radius.avatar)
    val avatarSquare = RoundedCornerShape(Radius.avatarSquare)
}
