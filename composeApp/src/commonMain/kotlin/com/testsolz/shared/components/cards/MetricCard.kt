package com.testsolz.shared.components.cards

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.testsolz.designsystem.theme.*

/**
 * Metric Card
 * Displays a metric with label and value
 */
@Composable
fun MetricCard(
    label: String,
    value: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    BaseCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Text(
                text = label,
                style = AppTypography.labelMedium,
                color = ColorPalette.textSecondary
            )
            
            Text(
                text = value,
                style = AppTypography.headlineLarge,
                color = ColorPalette.primary
            )
            
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = AppTypography.captionMedium,
                    color = ColorPalette.textSecondary
                )
            }
        }
    }
}
