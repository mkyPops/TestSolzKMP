package com.testsolz.shared.components.input

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.testsolz.designsystem.theme.*

/**
 * Custom Text Field
 * Styled text input field with consistent design
 */
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = label,
                    style = AppTypography.labelMedium
                )
            },
            placeholder = {
                Text(
                    text = placeholder,
                    style = AppTypography.bodyMedium,
                    color = ColorPalette.textTertiary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            enabled = enabled,
            isError = isError,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = AppTypography.bodyMedium,
            shape = Shapes.input,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorPalette.primary,
                unfocusedBorderColor = ColorPalette.border,
                errorBorderColor = ColorPalette.error,
                focusedLabelColor = ColorPalette.primary,
                unfocusedLabelColor = ColorPalette.textSecondary,
                cursorColor = ColorPalette.primary,
                focusedContainerColor = ColorPalette.background,
                unfocusedContainerColor = ColorPalette.background,
                disabledContainerColor = ColorPalette.backgroundSecondary
            ),
            singleLine = true
        )
        
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = AppTypography.captionMedium,
                color = ColorPalette.error
            )
        }
    }
}
