package com.testsolz.shared.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import com.testsolz.designsystem.theme.*

/**
 * Base Card
 * Base card component with consistent styling
 */
@Composable
fun BaseCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = Shadows.card,
                shape = RoundedCornerShape(Radius.card),
                clip = false
            )
            .clip(RoundedCornerShape(Radius.card))
            .background(ColorPalette.cardBackground)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(PaddingPresets.card)
    ) {
        content()
    }
}
