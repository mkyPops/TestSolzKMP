package com.testsolz.domain.models

import androidx.compose.ui.graphics.Color
import com.testsolz.designsystem.theme.ColorPalette
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import kotlin.math.abs

/**
 * Request Type
 */
@Serializable
enum class RequestType(val value: String) {
    LEAVE("leave"),
    LATE_ARRIVAL("late_arrival");

    val displayName: String
        get() = when (this) {
            LEAVE -> "Leave"
            LATE_ARRIVAL -> "Late Arrival"
        }

    val icon: String
        get() = when (this) {
            LEAVE -> "calendar_badge_clock"
            LATE_ARRIVAL -> "clock_badge_exclamationmark"
        }
}

/**
 * Leave Type
 */
@Serializable
enum class LeaveType(val value: String) {
    SICK("sick"),
    VACATION("vacation"),
    PERSONAL("personal"),
    EMERGENCY("emergency");

    val displayName: String
        get() = value.replaceFirstChar { it.uppercase() }

    val color: Color
        get() = when (this) {
            SICK -> ColorPalette.error
            VACATION -> ColorPalette.info
            PERSONAL -> ColorPalette.warning
            EMERGENCY -> ColorPalette.error
        }

    companion object {
        fun values() = enumValues<LeaveType>().toList()
    }
}

/**
 * Request Status
 */
@Serializable
enum class RequestStatus(val value: String) {
    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected");

    val displayName: String
        get() = value.replaceFirstChar { it.uppercase() }

    val color: Color
        get() = when (this) {
            PENDING -> ColorPalette.warning
            APPROVED -> ColorPalette.success
            REJECTED -> ColorPalette.error
        }

    val icon: String
        get() = when (this) {
            PENDING -> "clock_fill"
            APPROVED -> "checkmark_circle_fill"
            REJECTED -> "xmark_circle_fill"
        }
}

/**
 * Leave Request Model
 */
@Serializable
data class LeaveRequest(
    val id: String,
    val userId: String,
    val userName: String,
    val userDepartment: String? = null,
    val type: RequestType,
    val leaveType: LeaveType? = null, // Only for leave requests
    @Serializable(with = LocalDateSerializer::class)
    val startDate: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    val endDate: LocalDate? = null, // Only for leave requests
    val expectedTime: String? = null, // Only for late arrival (e.g., "10:30 AM")
    val reason: String,
    var status: RequestStatus,
    var adminComment: String? = null,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant,
    @Serializable(with = InstantSerializer::class)
    var reviewedAt: Instant? = null,
    var reviewedBy: String? = null
) {
    // Computed properties
    val daysCount: Int?
        get() {
            val end = endDate ?: return null
            return abs(end.toEpochDays() - startDate.toEpochDays()) + 1
        }

    val formattedDateRange: String
        get() {
            return if (endDate != null) {
                "${startDate.formatted()} - ${endDate.formatted()}"
            } else {
                startDate.formatted()
            }
        }

    companion object {
        // Mock data
        val mockLeave = LeaveRequest(
            id = "1",
            userId = "1",
            userName = "John Doe",
            userDepartment = "Engineering",
            type = RequestType.LEAVE,
            leaveType = LeaveType.VACATION,
            startDate = Clock.System.now().plus(7, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                .toLocalDateTime(TimeZone.currentSystemDefault()).date,
            endDate = Clock.System.now().plus(9, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                .toLocalDateTime(TimeZone.currentSystemDefault()).date,
            expectedTime = null,
            reason = "Family vacation",
            status = RequestStatus.PENDING,
            adminComment = null,
            createdAt = Clock.System.now(),
            reviewedAt = null,
            reviewedBy = null
        )

        val mockLate = LeaveRequest(
            id = "2",
            userId = "1",
            userName = "John Doe",
            userDepartment = "Engineering",
            type = RequestType.LATE_ARRIVAL,
            leaveType = null,
            startDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            endDate = null,
            expectedTime = "10:30 AM",
            reason = "Doctor's appointment",
            status = RequestStatus.PENDING,
            adminComment = null,
            createdAt = Clock.System.now(),
            reviewedAt = null,
            reviewedBy = null
        )

        val mockRequests = listOf(
            LeaveRequest(
                id = "1",
                userId = "2",
                userName = "Sarah Wilson",
                userDepartment = "Design",
                type = RequestType.LEAVE,
                leaveType = LeaveType.SICK,
                startDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                endDate = Clock.System.now().plus(2, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date,
                expectedTime = null,
                reason = "Flu",
                status = RequestStatus.PENDING,
                adminComment = null,
                createdAt = Clock.System.now(),
                reviewedAt = null,
                reviewedBy = null
            ),
            LeaveRequest(
                id = "2",
                userId = "3",
                userName = "Mike Johnson",
                userDepartment = "Marketing",
                type = RequestType.LATE_ARRIVAL,
                leaveType = null,
                startDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                endDate = null,
                expectedTime = "11:00 AM",
                reason = "Car trouble",
                status = RequestStatus.PENDING,
                adminComment = null,
                createdAt = Clock.System.now(),
                reviewedAt = null,
                reviewedBy = null
            ),
            LeaveRequest(
                id = "3",
                userId = "4",
                userName = "Jane Smith",
                userDepartment = "Engineering",
                type = RequestType.LEAVE,
                leaveType = LeaveType.VACATION,
                startDate = Clock.System.now().minus(5, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date,
                endDate = Clock.System.now().minus(3, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date,
                expectedTime = null,
                reason = "Beach trip",
                status = RequestStatus.APPROVED,
                adminComment = "Enjoy!",
                createdAt = Clock.System.now().minus(7, DateTimeUnit.DAY, TimeZone.currentSystemDefault()),
                reviewedAt = Clock.System.now().minus(6, DateTimeUnit.DAY, TimeZone.currentSystemDefault()),
                reviewedBy = "Admin"
            )
        )
    }
}

// Extension for LocalDate formatting
fun LocalDate.formatted(): String {
    return "${this.month.name.take(3)} ${this.dayOfMonth}, ${this.year}"
}
