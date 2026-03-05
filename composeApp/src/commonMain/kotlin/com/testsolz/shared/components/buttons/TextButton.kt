package com.testsolz.shared.components.buttons

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.testsolz.designsystem.theme.AppTypography
import com.testsolz.designsystem.theme.ColorPalette

/**
 * Text Button
 * Text-only button for tertiary actions
 */
@Composable
fun AppTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled
    ) {
        Text(
            text = text,
            style = AppTypography.buttonMedium,
            color = if (enabled) ColorPalette.primary else ColorPalette.buttonDisabledText
        )
    }
}
