package com.testsolz.features.employee.employeehome.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.testsolz.designsystem.theme.AppTypography
import com.testsolz.designsystem.theme.ColorPalette
import com.testsolz.designsystem.theme.PaddingPresets
import com.testsolz.designsystem.theme.Shapes
import com.testsolz.designsystem.theme.Spacing
import com.testsolz.domain.models.Project
import com.testsolz.domain.models.TaskItem
import com.testsolz.features.employee.employeehome.viewmodels.SubtaskItem
import com.testsolz.shared.components.buttons.AppTextButton
import com.testsolz.shared.components.buttons.PrimaryButton
import com.testsolz.shared.components.buttons.SecondaryButton
import com.testsolz.shared.components.cards.BaseCard
import com.testsolz.shared.components.input.CustomTextField

@Composable
fun TaskDetailView(
    task: TaskItem,
    project: Project?,
    subtasks: List<SubtaskItem>,
    onBack: () -> Unit,
    onToggleSubtask: (subtaskId: String) -> Unit,
    onDeleteSubtask: (subtaskId: String) -> Unit,
    onAddSubtask: (title: String) -> Unit,
    onMarkTaskDone: () -> Unit,
    onDeleteTask: () -> Unit
) {
    var newSubtaskTitle by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.background)
            .padding(PaddingPresets.screen),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTextButton(text = "Back", onClick = onBack)
                Text(text = "Task Details", style = AppTypography.headlineSmall)
                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        item {
            BaseCard(modifier = Modifier.fillMaxWidth()) {
                androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Text(
                        text = task.title,
                        style = AppTypography.titleLarge.copy(
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                            color = if (task.isCompleted) ColorPalette.textSecondary else ColorPalette.textPrimary
                        )
                    )

                    if (project != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            androidx.compose.foundation.layout.Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(project.displayColor)
                            )
                            Text(
                                text = project.name,
                                style = AppTypography.bodySmall,
                                color = ColorPalette.textSecondary
                            )
                        }
                    }

                    if (task.description != null) {
                        Text(
                            text = task.description,
                            style = AppTypography.bodyMedium,
                            color = ColorPalette.textSecondary
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .background(
                                    color = task.priority.color.copy(alpha = 0.12f),
                                    shape = Shapes.badge
                                )
                                .padding(horizontal = Spacing.sm, vertical = Spacing.xxs)
                        ) {
                            Text(
                                text = task.priority.displayName,
                                style = AppTypography.labelSmall,
                                color = task.priority.color
                            )
                        }

                        val statusText = if (task.isCompleted) "Done" else "In Progress"
                        val statusColor = if (task.isCompleted) ColorPalette.success else ColorPalette.textSecondary
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .background(
                                    color = statusColor.copy(alpha = 0.12f),
                                    shape = Shapes.badge
                                )
                                .padding(horizontal = Spacing.sm, vertical = Spacing.xxs)
                        ) {
                            Text(
                                text = statusText,
                                style = AppTypography.labelSmall,
                                color = statusColor
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(text = "Subtasks", style = AppTypography.headlineSmall)
        }

        if (subtasks.isEmpty()) {
            item {
                BaseCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No subtasks yet",
                        style = AppTypography.bodyMedium,
                        color = ColorPalette.textSecondary
                    )
                }
            }
        } else {
            items(subtasks) { subtask ->
                BaseCard(modifier = Modifier.fillMaxWidth(), onClick = { onToggleSubtask(subtask.id) }) {
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
                            Checkbox(
                                checked = subtask.isCompleted,
                                onCheckedChange = { onToggleSubtask(subtask.id) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = ColorPalette.success,
                                    uncheckedColor = ColorPalette.border,
                                    checkmarkColor = ColorPalette.textOnDark
                                )
                            )
                            Text(
                                text = subtask.title,
                                style = AppTypography.bodyMedium.copy(
                                    textDecoration = if (subtask.isCompleted) TextDecoration.LineThrough else null,
                                    color = if (subtask.isCompleted) ColorPalette.textSecondary else ColorPalette.textPrimary
                                )
                            )
                        }

                        AppTextButton(
                            text = "Delete",
                            onClick = { onDeleteSubtask(subtask.id) }
                        )
                    }
                }
            }
        }

        item {
            androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                CustomTextField(
                    value = newSubtaskTitle,
                    onValueChange = { newSubtaskTitle = it },
                    label = "New subtask",
                    placeholder = "Enter subtask title"
                )

                PrimaryButton(
                    text = "Add Subtask",
                    onClick = {
                        onAddSubtask(newSubtaskTitle)
                        newSubtaskTitle = ""
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.sm))
        }

        item {
            androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                SecondaryButton(
                    text = if (task.isCompleted) "Task Completed" else "Mark Task as Done",
                    enabled = !task.isCompleted,
                    onClick = onMarkTaskDone
                )
                SecondaryButton(
                    text = "Delete Task",
                    onClick = onDeleteTask
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}

