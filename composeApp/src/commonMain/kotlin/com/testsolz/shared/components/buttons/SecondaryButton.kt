package com.testsolz.shared.components.buttons

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.testsolz.designsystem.theme.*

/**
 * Secondary Button
 * Secondary action button with gray background
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = ColorPalette.buttonSecondary,
            contentColor = ColorPalette.buttonSecondaryText,
            disabledContainerColor = ColorPalette.buttonDisabled,
            disabledContentColor = ColorPalette.buttonDisabledText
        ),
        shape = Shapes.button,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = Shadows.none,
            pressedElevation = Shadows.none
        ),
        contentPadding = PaddingPresets.button
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = ColorPalette.textPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = AppTypography.buttonMedium
            )
        }
    }
}
