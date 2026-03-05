package com.testsolz.features.employee.profile.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.testsolz.designsystem.theme.AppTypography
import com.testsolz.designsystem.theme.ColorPalette
import com.testsolz.designsystem.theme.PaddingPresets
import com.testsolz.designsystem.theme.Shapes
import com.testsolz.designsystem.theme.Spacing
import com.testsolz.domain.models.User
import com.testsolz.shared.components.buttons.SecondaryButton
import com.testsolz.shared.components.cards.BaseCard

/**
 * Profile View
 * Employee profile screen
 */
@Composable
fun ProfileView(
    user: User = User.mock,
    onLogout: () -> Unit = {}
) {
    val initials = user.name
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.background)
            .padding(PaddingPresets.screen),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Text(
                text = "Profile",
                style = AppTypography.headlineLarge
            )
            Text(
                text = "Your account details",
                style = AppTypography.bodySmall,
                color = ColorPalette.textSecondary
            )
        }

        BaseCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(ColorPalette.primaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            style = AppTypography.titleMedium,
                            color = ColorPalette.primary
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxxs)) {
                        Text(
                            text = user.name,
                            style = AppTypography.titleLarge
                        )
                        Text(
                            text = user.email,
                            style = AppTypography.captionMedium,
                            color = ColorPalette.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.sm))

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    InfoRow("Role", user.role.displayName)
                    InfoRow("Department", user.department ?: "N/A")
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        SecondaryButton(
            text = "Logout",
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Signed in as ${user.email}",
            style = AppTypography.captionSmall,
            color = ColorPalette.textTertiary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = AppTypography.labelMedium,
            color = ColorPalette.textSecondary
        )
        Text(
            text = value,
            style = AppTypography.bodyMedium
        )
    }
}
