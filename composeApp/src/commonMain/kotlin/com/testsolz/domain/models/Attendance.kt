package com.testsolz.domain.models

import androidx.compose.ui.graphics.Color
import com.testsolz.designsystem.theme.ColorPalette
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import kotlin.random.Random

/**
 * Attendance Record
 * Represents a single day's attendance (check-in and check-out)
 */
@Serializable
data class Attendance(
    val id: String,
    val userId: String,
    @Serializable(with = InstantSerializer::class)
    val checkInTime: Instant,
    @Serializable(with = InstantSerializer::class)
    val checkOutTime: Instant? = null,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate
) {
    val hoursWorked: Double?
        get() {
            val checkOut = checkOutTime ?: return null
            return (checkOut.toEpochMilliseconds() - checkInTime.toEpochMilliseconds()) / 3_600_000.0
        }

    val isCheckedOut: Boolean
        get() = checkOutTime != null

    val status: AttendanceStatus
        get() {
            val localDateTime = checkInTime.toLocalDateTime(TimeZone.currentSystemDefault())
            val totalMinutes = localDateTime.hour * 60 + localDateTime.minute

            // Work start time: 9:00 AM (540 minutes)
            return when {
                totalMinutes <= 540 -> AttendanceStatus.ON_TIME
                totalMinutes <= 555 -> AttendanceStatus.SLIGHTLY_LATE
                else -> AttendanceStatus.LATE
            }
        }

    companion object {
        val mock = Attendance(
            id = generateId(),
            userId = "1",
            checkInTime = Clock.System.now(),
            checkOutTime = null,
            date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        )

        val mockCheckedOut = Attendance(
            id = generateId(),
            userId = "1",
            checkInTime = Clock.System.now().minus(8, DateTimeUnit.HOUR),
            checkOutTime = Clock.System.now(),
            date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        )

        fun mockHistory(userId: String, daysBack: Int = 30): List<Attendance> {
            val history = mutableListOf<Attendance>()
            val timezone = TimeZone.currentSystemDefault()
            val today = Clock.System.now().toLocalDateTime(timezone).date

            for (day in 0 until daysBack) {
                // Randomly skip some days (absent)
                if (Random.nextInt(11) > 8) continue

                val date = today.minus(day, DateTimeUnit.DAY)

                // Random check-in time between 8:30 AM and 10:00 AM
                val checkInHour = Random.nextInt(8, 10)
                val checkInMinute = Random.nextInt(0, 60)
                val checkInTime = LocalDateTime(
                    date.year, date.monthNumber, date.dayOfMonth,
                    checkInHour, checkInMinute
                ).toInstant(timezone)

                // Random check-out time between 5:00 PM and 7:00 PM
                val checkOutHour = Random.nextInt(17, 20)
                val checkOutMinute = Random.nextInt(0, 60)
                val checkOutTime = LocalDateTime(
                    date.year, date.monthNumber, date.dayOfMonth,
                    checkOutHour, checkOutMinute
                ).toInstant(timezone)

                history.add(
                    Attendance(
                        id = generateId(),
                        userId = userId,
                        checkInTime = checkInTime,
                        checkOutTime = checkOutTime,
                        date = date
                    )
                )
            }

            return history.sortedByDescending { it.date }
        }
    }
}

/**
 * Attendance Status
 */
@Serializable
enum class AttendanceStatus(val value: String) {
    ON_TIME("on_time"),
    SLIGHTLY_LATE("slightly_late"),
    LATE("late"),
    ABSENT("absent");

    val displayName: String
        get() = when (this) {
            ON_TIME -> "On Time"
            SLIGHTLY_LATE -> "Slightly Late"
            LATE -> "Late"
            ABSENT -> "Absent"
        }

    val color: Color
        get() = when (this) {
            ON_TIME -> ColorPalette.success
            SLIGHTLY_LATE -> ColorPalette.warning
            LATE -> ColorPalette.error
            ABSENT -> ColorPalette.textTertiary
        }
}

/**
 * Serializers for kotlinx.datetime
 */
object InstantSerializer : kotlinx.serialization.KSerializer<Instant> {
    override val descriptor =
        kotlinx.serialization.descriptors.PrimitiveSerialDescriptor(
            "Instant",
            kotlinx.serialization.descriptors.PrimitiveKind.STRING
        )

    override fun serialize(
        encoder: kotlinx.serialization.encoding.Encoder,
        value: Instant
    ) = encoder.encodeString(value.toString())

    override fun deserialize(
        decoder: kotlinx.serialization.encoding.Decoder
    ): Instant = Instant.parse(decoder.decodeString())
}

object LocalDateSerializer : kotlinx.serialization.KSerializer<LocalDate> {
    override val descriptor =
        kotlinx.serialization.descriptors.PrimitiveSerialDescriptor(
            "LocalDate",
            kotlinx.serialization.descriptors.PrimitiveKind.STRING
        )

    override fun serialize(
        encoder: kotlinx.serialization.encoding.Encoder,
        value: LocalDate
    ) = encoder.encodeString(value.toString())

    override fun deserialize(
        decoder: kotlinx.serialization.encoding.Decoder
    ): LocalDate = LocalDate.parse(decoder.decodeString())
}

/**
 * Date Formatting Extensions (KMP safe)
 */
fun Instant.formatted(style: String = "time"): String {
    val dt = this.toLocalDateTime(TimeZone.currentSystemDefault())

    val hour12 = if (dt.hour % 12 == 0) 12 else dt.hour % 12
    val minute = dt.minute.toString().padStart(2, '0')
    val amPm = if (dt.hour < 12) "AM" else "PM"

    return when (style) {
        "time" -> "$hour12:$minute $amPm"
        "date" -> "${dt.month.name.take(3)} ${dt.dayOfMonth}, ${dt.year}"
        "datetime" -> "${dt.month.name.take(3)} ${dt.dayOfMonth}, ${dt.year} at $hour12:$minute $amPm"
        else -> "$hour12:$minute $amPm"
    }
}

/**
 * KMP-safe ID generator
 */
private fun generateId(): String =
    Random.nextLong().toString()