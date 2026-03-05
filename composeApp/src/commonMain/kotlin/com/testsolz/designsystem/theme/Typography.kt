package com.testsolz.designsystem.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography System
 * Professional, readable typography
 * Uses system fonts for consistency and readability
 */
object AppTypography {
    
    // MARK: - Display Styles (Large headings, hero text)
    val displayLarge = TextStyle(
        fontSize = 48.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 56.sp,
        color = ColorPalette.textPrimary
    )
    
    val displayMedium = TextStyle(
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 44.sp,
        color = ColorPalette.textPrimary
    )
    
    val displaySmall = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 36.sp,
        color = ColorPalette.textPrimary
    )
    
    // MARK: - Headline Styles (Screen titles, section headers)
    val headlineLarge = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 32.sp,
        color = ColorPalette.textPrimary
    )
    
    val headlineMedium = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 28.sp,
        color = ColorPalette.textPrimary
    )
    
    val headlineSmall = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 24.sp,
        color = ColorPalette.textPrimary
    )
    
    // MARK: - Title Styles (Card titles, list headers)
    val titleLarge = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 28.sp,
        color = ColorPalette.textPrimary
    )
    
    val titleMedium = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 24.sp,
        color = ColorPalette.textPrimary
    )
    
    val titleSmall = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 22.sp,
        color = ColorPalette.textPrimary
    )
    
    // MARK: - Body Styles (Main content text)
    val bodyLarge = TextStyle(
        fontSize = 17.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp,
        color = ColorPalette.textPrimary
    )
    
    val bodyMedium = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 22.sp,
        color = ColorPalette.textPrimary
    )
    
    val bodySmall = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
        color = ColorPalette.textPrimary
    )
    
    // MARK: - Label Styles (Buttons, tabs, form labels)
    val labelLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 20.sp,
        color = ColorPalette.textPrimary
    )
    
    val labelMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 18.sp,
        color = ColorPalette.textPrimary
    )
    
    val labelSmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 16.sp,
        color = ColorPalette.textPrimary
    )
    
    // MARK: - Caption Styles (Timestamps, helper text, metadata)
    val captionLarge = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 18.sp,
        color = ColorPalette.textSecondary
    )
    
    val captionMedium = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp,
        color = ColorPalette.textSecondary
    )
    
    val captionSmall = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 14.sp,
        color = ColorPalette.textSecondary
    )
    
    // MARK: - Button Styles
    val buttonLarge = TextStyle(
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 20.sp,
        color = ColorPalette.textPrimary
    )
    
    val buttonMedium = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 18.sp,
        color = ColorPalette.textPrimary
    )
    
    val buttonSmall = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 16.sp,
        color = ColorPalette.textPrimary
    )
}
