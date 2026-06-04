package com.testsolz.domain.models

import androidx.compose.ui.graphics.Color
import com.testsolz.designsystem.theme.ColorPalette
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/**
 * Project Model
 */
@Serializable
data class Project(
    val id: String,
    val name: String,
    val description: String? = null,
    val color: String // Hex color for the project
) {
    // KMP-safe hex parser (no android.graphics)
    val displayColor: Color
        get() = parseHexColor(color)
}

/**
 * Task Priority
 */
@Serializable
enum class TaskPriority(val value: String) {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high");

    val displayName: String
        get() = value.replaceFirstChar { it.uppercase() }

    val color: Color
        get() = when (this) {
            LOW -> ColorPalette.info
            MEDIUM -> ColorPalette.warning
            HIGH -> ColorPalette.error
        }
}

/**
 * Task Model
 */
@Serializable
data class TaskItem(
    val id: String,
    val userId: String,
    val projectId: String,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant,
    @Serializable(with = InstantSerializer::class)
    val completedAt: Instant? = null,
    val priority: TaskPriority,
    @Serializable(with = LocalDateSerializer::class)
    val dueDate: LocalDate? = null
)

/**
 * Hex color parser (KMP safe)
 */
private fun parseHexColor(hex: String): Color {
    val cleanHex = hex.removePrefix("#")
    val colorLong = cleanHex.toLong(16)
    return when (cleanHex.length) {
        6 -> Color(0xFF000000 or colorLong)
        8 -> Color(colorLong)
        else -> Color.Gray
    }
}
