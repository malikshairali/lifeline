package io.github.malikshairali.lifeline.presentation.util

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
object DatePickerSelectableDates : SelectableDates {

    // Calculate midnight UTC of today. The picker deals with UTC dates at their start of day.
    private val todayEpochMillisInUtcZoneAtStartOfDay: Long by lazy {
        val systemDefaultToday = LocalDate.now(ZoneId.systemDefault())
        systemDefaultToday.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
    }

    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        // Allow selection if the given date (at midnight UTC) is less than or equal to today (at midnight UTC).
        return utcTimeMillis <= todayEpochMillisInUtcZoneAtStartOfDay
    }

    override fun isSelectableYear(year: Int): Boolean {
        // Also ensure that selectable years do not go into the future.
        return year <= LocalDate.now(ZoneId.systemDefault()).year
    }
}