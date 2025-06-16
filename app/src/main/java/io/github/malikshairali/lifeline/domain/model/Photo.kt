package io.github.malikshairali.lifeline.domain.model

import android.net.Uri

data class Photo(
    val id: Long,
    val uri: Uri,
    val timestamp: Long
)