package com.testsolz.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * TestSolz App Theme
 * Main theme composable that wraps the entire app
 */

private val LightColorScheme = lightColorScheme(
    primary = ColorPalette.primary,
    onPrimary = ColorPalette.textOnPrimary,
    secondary = ColorPalette.buttonSecondary,
    onSecondary = ColorPalette.textPrimary,
    background = ColorPalette.background,
    onBackground = ColorPalette.textPrimary,
    surface = ColorPalette.surface,
    onSurface = ColorPalette.textPrimary,
    error = ColorPalette.error,
    onError = ColorPalette.textOnDark,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // For now, only light theme is supported
    // Dark theme can be added later if needed
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
