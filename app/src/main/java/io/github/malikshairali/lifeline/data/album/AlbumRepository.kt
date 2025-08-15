package io.github.malikshairali.lifeline.data.album

import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class AlbumRepository(
    private val albumDao: AlbumDao
) {
    suspend fun insertAlbum(album: AlbumEntity) = albumDao.insert(album)

    fun getAlbumById(id: Long): Flow<AlbumEntity?> = albumDao.getAlbumById(id)

    fun getAllAlbums(): Flow<List<AlbumEntity>> = albumDao.getAlbums()

    suspend fun insertPhotos(photos: List<PhotoEntity>) = albumDao.insertPhotos(photos)
}
