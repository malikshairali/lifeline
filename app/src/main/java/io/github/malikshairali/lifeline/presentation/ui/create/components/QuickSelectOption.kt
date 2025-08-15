package io.github.malikshairali.lifeline.presentation.ui.create.components

import io.github.malikshairali.lifeline.presentation.util.toEpochMillisEndOfDay
import io.github.malikshairali.lifeline.presentation.util.toEpochMillisStartOfDay
import java.time.DayOfWeek
import java.time.LocalDate

enum class QuickSelectOption(val displayText: String) {
    NONE("None"),
    TODAY("Today"),
    YESTERDAY("Yesterday"),
    THIS_WEEK("This Week"),
    LAST_WEEK("Last Week"),
    THIS_MONTH("This Month"),
    LAST_MONTH("Last Month"),
    THIS_YEAR("This Year"),
    LAST_YEAR("Last Year");

    companion object {
        fun determineActiveQuickSelect(currentFrom: Long?, currentTo: Long?): QuickSelectOption? {
            if (currentFrom == null || currentTo == null) return NONE

            val now = LocalDate.now()
            val todayStart = now.toEpochMillisStartOfDay()
            val todayEnd = now.toEpochMillisEndOfDay()
            if (currentFrom == todayStart && currentTo == todayEnd) return TODAY

            val yesterday = LocalDate.now().minusDays(1)
            val yesterdayStart = yesterday.toEpochMillisStartOfDay()
            val yesterdayEnd = yesterday.toEpochMillisEndOfDay()
            if (currentFrom == yesterdayStart && currentTo == yesterdayEnd) return YESTERDAY

            val thisWeekStart = now.with(DayOfWeek.MONDAY).toEpochMillisStartOfDay()
            val thisWeekEnd = now.with(DayOfWeek.SUNDAY).toEpochMillisEndOfDay()
            if (currentFrom == thisWeekStart && currentTo == thisWeekEnd) return THIS_WEEK

            val lastWeek = LocalDate.now().minusWeeks(1)
            val lastWeekStart = lastWeek.with(DayOfWeek.MONDAY).toEpochMillisStartOfDay()
            val lastWeekEnd = lastWeek.with(DayOfWeek.SUNDAY).toEpochMillisEndOfDay()
            if (currentFrom == lastWeekStart && currentTo == lastWeekEnd) return LAST_WEEK

            val thisMonthStart = now.withDayOfMonth(1).toEpochMillisStartOfDay()
            val thisMonthEnd = now.withDayOfMonth(now.lengthOfMonth()).toEpochMillisEndOfDay()
            if (currentFrom == thisMonthStart && currentTo == thisMonthEnd) return THIS_MONTH

            val lastMonth = LocalDate.now().minusMonths(1)
            val lastMonthStart = lastMonth.withDayOfMonth(1).toEpochMillisStartOfDay()
            val lastMonthEnd = lastMonth.withDayOfMonth(lastMonth.lengthOfMonth()).toEpochMillisEndOfDay()
            if (currentFrom == lastMonthStart && currentTo == lastMonthEnd) return LAST_MONTH

            val thisYearStart = now.withDayOfYear(1).toEpochMillisStartOfDay()
            val thisYearEnd = now.withDayOfYear(now.lengthOfYear()).toEpochMillisEndOfDay()
            if (currentFrom == thisYearStart && currentTo == thisYearEnd) return THIS_YEAR

            val lastYear = LocalDate.now().minusYears(1)
            val lastYearStart = lastYear.withDayOfYear(1).toEpochMillisStartOfDay()
            val lastYearEnd = lastYear.withDayOfYear(lastYear.lengthOfYear()).toEpochMillisEndOfDay()
            if (currentFrom == lastYearStart && currentTo == lastYearEnd) return LAST_YEAR

            return NONE
        }
    }
}