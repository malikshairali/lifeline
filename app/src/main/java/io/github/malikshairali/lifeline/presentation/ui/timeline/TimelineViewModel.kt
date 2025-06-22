package io.github.malikshairali.lifeline.presentation.ui.timeline

import android.content.Context
import androidx.lifecycle.ViewModel
import io.github.malikshairali.lifeline.data.album.AlbumRepository
import io.github.malikshairali.lifeline.data.source.local.LocalImageDataSource
import io.github.malikshairali.lifeline.domain.groupPhotosByDate
import io.github.malikshairali.lifeline.domain.model.DateGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class TimelineViewModel(
    private val albumRepository: AlbumRepository
) : ViewModel() {

    private val _photos = MutableStateFlow<List<DateGroup>>(emptyList())
    val photos = _photos.asStateFlow()


    suspend fun getPhotos(context: Context, id: Long) {
        albumRepository.getAlbumById(id).collectLatest { album ->
            album?.let {
                val photos = LocalImageDataSource.getDeviceImagesBetween(
                    context = context,
                    startMillis = album.startDate,
                    endMillis = album.endDate
                )
                _photos.value = groupPhotosByDate(photos)
            }
        }
    }
}