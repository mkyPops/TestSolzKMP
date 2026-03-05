package com.testsolz.shared.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.testsolz.designsystem.theme.*

/**
 * Status Card
 * Displays status information with colored badge
 */
@Composable
fun StatusCard(
    title: String,
    value: String,
    statusColor: Color,
    statusText: String,
    modifier: Modifier = Modifier
) {
    BaseCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = AppTypography.labelMedium,
                    color = ColorPalette.textSecondary
                )
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Radius.badge))
                        .background(statusColor.copy(alpha = 0.1f))
                        .padding(horizontal = Spacing.sm, vertical = Spacing.xxs)
                ) {
                    Text(
                        text = statusText,
                        style = AppTypography.labelSmall,
                        color = statusColor
                    )
                }
            }
            
            Text(
                text = value,
                style = AppTypography.displaySmall
            )
        }
    }
}
