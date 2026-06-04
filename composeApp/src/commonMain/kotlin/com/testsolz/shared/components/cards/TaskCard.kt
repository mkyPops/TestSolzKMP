package com.testsolz.shared.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.testsolz.designsystem.theme.*
import com.testsolz.domain.models.TaskItem

/**
 * Task Card
 * Displays a task with checkbox and priority indicator
 */
@Composable
fun TaskCard(
    task: TaskItem,
    onToggle: () -> Unit,
    onClick: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    BaseCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                // Priority indicator
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(task.priority.color)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = AppTypography.titleSmall.copy(
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                            color = if (task.isCompleted) ColorPalette.textSecondary else ColorPalette.textPrimary
                        )
                    )
                    
                    if (task.description != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.description,
                            style = AppTypography.bodySmall,
                            color = ColorPalette.textSecondary
                        )
                    }
                }
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = ColorPalette.success,
                        uncheckedColor = ColorPalette.border,
                        checkmarkColor = ColorPalette.textOnDark
                    )
                )
                if (onDelete != null) {
                    androidx.compose.material3.TextButton(onClick = onDelete) {
                        Text(
                            text = "Delete",
                            style = AppTypography.labelSmall,
                            color = ColorPalette.error
                        )
                    }
                }
            }
        }
    }
}
