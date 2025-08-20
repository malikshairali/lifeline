package io.github.malikshairali.lifeline.presentation.ui.create

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.malikshairali.lifeline.data.album.AlbumEntity
import io.github.malikshairali.lifeline.data.album.AlbumRepository
import io.github.malikshairali.lifeline.data.source.local.LocalImageDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import kotlin.random.Random

@KoinViewModel
class CreateAlbumViewModel(
    private val albumRepository: AlbumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateAlbumUiState())
    val uiState = _uiState.asStateFlow()

    fun updateAlbumName(name: String) {
        _uiState.value = _uiState.value.copy(albumName = name)
    }

    fun updateFromDate(from: Long?) {
        _uiState.value = _uiState.value.copy(fromDate = from)
    }

    fun updateToDate(to: Long?) {
        _uiState.value = _uiState.value.copy(toDate = to)
    }

    fun loadPhotos(context: Context) {
        val from = _uiState.value.fromDate ?: return
        val to = _uiState.value.toDate ?: return
        val photos = LocalImageDataSource.getDeviceImagesBetween(context, from, to)
        _uiState.value = _uiState.value.copy(
            photos = photos,
            error = if (photos.isEmpty()) "No photos found within date range selected." else null
        )
    }

    fun createAlbum(): Long {
        val id = Random.nextLong()
        val albumEntity = AlbumEntity(
            id = id,
            title = uiState.value.albumName ?: "",
            startDate = uiState.value.fromDate ?: 0,
            endDate = uiState.value.toDate ?: 0,
            coverUri = uiState.value.photos?.firstOrNull()?.uri.toString(),
            size = uiState.value.photos?.size ?: 0
        )

        viewModelScope.launch {
            albumRepository.insertAlbum(albumEntity)
        }

        return id
    }
}