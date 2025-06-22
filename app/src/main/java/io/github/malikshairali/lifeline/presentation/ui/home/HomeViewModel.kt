package io.github.malikshairali.lifeline.presentation.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.malikshairali.lifeline.data.album.AlbumEntity
import io.github.malikshairali.lifeline.data.album.AlbumRepository
import io.github.malikshairali.lifeline.data.source.local.LocalImageDataSource
import io.github.malikshairali.lifeline.domain.groupPhotosByDate
import io.github.malikshairali.lifeline.domain.model.DateGroup
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel(
    private val albumRepository: AlbumRepository
) : ViewModel() {

    val albums: StateFlow<List<AlbumEntity>> = albumRepository
        .getAllAlbums()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getPhotos(context: Context, startMillis: Long, endMillis: Long) : List<DateGroup> {
        val photos = LocalImageDataSource.getDeviceImagesBetween(
            context = context,
            startMillis = startMillis,
            endMillis = endMillis
        )
        return groupPhotosByDate(photos)
    }

    fun saveAlbum(album: AlbumEntity) {
        viewModelScope.launch {
            albumRepository.insertAlbum(album)
        }
    }
}
