package io.github.malikshairali.lifeline.domain

import io.github.malikshairali.lifeline.domain.model.Photo
import io.github.malikshairali.lifeline.domain.model.DateGroup
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date.formatDateWithSuffix(): String {
    val day = SimpleDateFormat("d", Locale.getDefault()).format(this).toInt()
    val suffix = when {
        day in 11..13 -> "th"
        day % 10 == 1 -> "st"
        day % 10 == 2 -> "nd"
        day % 10 == 3 -> "rd"
        else -> "th"
    }

    val dateFormat = SimpleDateFormat("d'$suffix' MMMM, yyyy", Locale.getDefault())
    return dateFormat.format(this)
}

fun groupPhotosByDate(photos: List<Photo>): List<DateGroup> {
    return photos
        .groupBy { photo ->
            val calendar = Calendar.getInstance().apply { time = Date(photo.timestamp) }
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.time
        }
        .toSortedMap(compareBy { it })
        .map { (date, grouped) ->
            DateGroup(
                dateLabel = date.formatDateWithSuffix(),
                photos = grouped.sortedBy { it.timestamp }
            )
        }
}
