package io.github.malikshairali.lifeline.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.malikshairali.lifeline.data.album.AlbumEntity
import io.github.malikshairali.lifeline.data.album.AlbumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel(
    albumRepository: AlbumRepository
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

    fun removeError() {
        _error.value = null
    }
}
