package io.github.malikshairali.lifeline.domain.model

import io.github.malikshairali.lifeline.data.album.PhotoEntity

data class DateGroup(
    val dateLabel: String,
    val photos: List<PhotoEntity>
)
