package io.github.malikshairali.lifeline.presentation.ui.create

import io.github.malikshairali.lifeline.data.album.PhotoEntity

data class CreateAlbumUiState(
    val albumName: String? = null,
    val fromDate: Long? = null,
    val toDate: Long? = null,
    val photos: List<PhotoEntity>? = null,
    val error: String? = null
)
