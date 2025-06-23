package io.github.malikshairali.lifeline.presentation.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.malikshairali.lifeline.data.album.AlbumEntity
import io.github.malikshairali.lifeline.data.album.AlbumRepository
import io.github.malikshairali.lifeline.data.source.local.LocalImageDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import kotlin.random.Random

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

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun getAlbumId(context: Context, name: String, startMillis: Long, endMillis: Long) : Long? {
        var id: Long? = null
        val photos = LocalImageDataSource.getDeviceImagesBetween(
            context = context,
            startMillis = startMillis,
            endMillis = endMillis
        )

        if (photos.isEmpty()) {
            _error.value = "No photos found within date range selected."
        } else {
            id = Random.nextLong()
            saveAlbum(
                AlbumEntity(
                    id = id,
                    title = name,
                    startDate = startMillis,
                    endDate = endMillis,
                    coverUri = photos.first().uri.toString(),
                    size = photos.size
                )
            )
        }

        return id
    }

    private fun saveAlbum(album: AlbumEntity) {
        viewModelScope.launch {
            albumRepository.insertAlbum(album)
        }
    }

    fun removeError() {
        _error.value = null
    }
}
