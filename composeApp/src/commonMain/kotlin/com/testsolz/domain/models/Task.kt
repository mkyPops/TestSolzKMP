package com.testsolz.domain.models

import androidx.compose.ui.graphics.Color
import com.testsolz.designsystem.theme.ColorPalette
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.days

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

    companion object {
        val mock = Project(
            id = "1",
            name = "Mobile App Redesign",
            description = "Redesigning the company mobile application",
            color = "#00D9D9"
        )

        val mockProjects = listOf(
            mock,
            Project(
                id = "2",
                name = "Website Optimization",
                description = "SEO and performance",
                color = "#10B981"
            ),
            Project(
                id = "3",
                name = "Client Dashboard",
                description = "New client portal",
                color = "#F59E0B"
            )
        )
    }
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
    val priority: TaskPriority
) {
    companion object {

        val mock = TaskItem(
            id = "1",
            userId = "1",
            projectId = "1",
            title = "Design login screen",
            description = "Create mockups for new login",
            isCompleted = false,
            createdAt = Clock.System.now(),
            completedAt = null,
            priority = TaskPriority.HIGH
        )

        val mockTasks = listOf(
            mock,
            TaskItem(
                id = "2",
                userId = "1",
                projectId = "1",
                title = "Implement API integration",
                description = "Connect to backend",
                isCompleted = false,
                createdAt = Clock.System.now() - 1.days,
                completedAt = null,
                priority = TaskPriority.MEDIUM
            ),
            TaskItem(
                id = "3",
                userId = "1",
                projectId = "1",
                title = "Write unit tests",
                description = null,
                isCompleted = true,
                createdAt = Clock.System.now() - 2.days,
                completedAt = Clock.System.now(),
                priority = TaskPriority.LOW
            )
        )
    }
}

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