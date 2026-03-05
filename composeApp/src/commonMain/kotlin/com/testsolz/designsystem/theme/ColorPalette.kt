package com.testsolz.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * TestSolz Color Palette
 * Brand-aligned color system based on company logo
 * Primary: Cyan/Turquoise (gear icon color)
 * Professional, minimalistic, enterprise-grade
 */
object ColorPalette {
    
    // MARK: - Brand Colors (from logo)
    val primary = Color(0xFF00D9D9)        // Cyan - main brand color (gears)
    val primaryDark = Color(0xFF00B8B8)    // Darker cyan for pressed states
    val primaryLight = Color(0xFFE0F9F9)   // Very light cyan for backgrounds
    val primarySubtle = Color(0xFFB3F0F0)  // Subtle cyan for highlights
    
    val dark = Color(0xFF2D2D2D)           // Dark charcoal (logo background)
    val darkSecondary = Color(0xFF3D3D3D)  // Slightly lighter dark
    
    // MARK: - Semantic Colors (professional, minimal)
    val success = Color(0xFF00D9A3)        // Teal-green - for check-in success
    val successLight = Color(0xFFE0FAF3)   // Light teal background
    
    val warning = Color(0xFFFFB84D)        // Warm amber - for warnings
    val warningLight = Color(0xFFFFF4E0)   // Light amber background
    
    val error = Color(0xFFFF6B6B)          // Coral red - for errors
    val errorLight = Color(0xFFFFE8E8)     // Light coral background
    
    val info = Color(0xFF00D9D9)           // Using brand cyan for info
    val infoLight = Color(0xFFE0F9F9)      // Light cyan background
    
    // MARK: - Neutral Colors (clean, minimal)
    val background = Color(0xFFFFFFFF)     // Pure white
    val backgroundSecondary = Color(0xFFF9FAFB) // Off-white for surfaces
    val backgroundTertiary = Color(0xFFF3F4F6)  // Light gray for sections
    
    val surface = Color(0xFFFFFFFF)        // White cards/surfaces
    val surfaceElevated = Color(0xFFFAFBFC) // Slightly elevated surfaces
    
    // MARK: - Text Colors (high readability)
    val textPrimary = Color(0xFF1A1A1A)    // Almost black - main text
    val textSecondary = Color(0xFF6B7280)  // Gray - secondary text
    val textTertiary = Color(0xFF9CA3AF)   // Light gray - placeholder
    val textOnDark = Color(0xFFFFFFFF)     // White text on dark backgrounds
    val textOnPrimary = Color(0xFFFFFFFF)  // White text on cyan
    
    // MARK: - Border & Divider Colors
    val border = Color(0xFFE5E7EB)         // Light gray border
    val borderMedium = Color(0xFFD1D5DB)   // Medium gray border
    val borderDark = Color(0xFF9CA3AF)     // Darker border for emphasis
    
    val divider = Color(0xFFF3F4F6)        // Very subtle dividers
    
    // MARK: - Status Colors (attendance-specific)
    val statusCheckedIn = success              // Teal-green for checked in
    val statusCheckedOut = Color(0xFF9CA3AF)   // Gray for checked out
    val statusLate = warning                   // Amber for late arrival
    val statusAbsent = Color(0xFFD1D5DB)       // Light gray for absent
    val statusOnTime = primary                 // Cyan for on-time
    
    // MARK: - Interactive Elements
    val buttonPrimary = primary                // Cyan buttons
    val buttonPrimaryPressed = primaryDark     // Darker when pressed
    val buttonSecondary = Color(0xFFF3F4F6)    // Light gray secondary buttons
    val buttonSecondaryText = textPrimary      // Dark text on secondary buttons
    
    val buttonDisabled = Color(0xFFE5E7EB)     // Disabled button background
    val buttonDisabledText = Color(0xFF9CA3AF) // Disabled button text
    
    // MARK: - Card & Shadow
    val cardBackground = surface
    val cardShadow = Color.Black.copy(alpha = 0.04f)       // Very subtle shadow
    val cardShadowHover = Color.Black.copy(alpha = 0.08f)  // Slightly stronger on hover
    
    // MARK: - Accent Colors (optional, for variety)
    val accentPurple = Color(0xFFA78BFA)       // Soft purple for admin features
    val accentBlue = Color(0xFF60A5FA)         // Soft blue for analytics
    val accentGreen = Color(0xFF34D399)        // Fresh green for positive metrics
}
