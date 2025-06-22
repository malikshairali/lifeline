package io.github.malikshairali.lifeline.data.album

import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class AlbumRepository(
    private val albumDao: AlbumDao
) {
    fun getAllAlbums(): Flow<List<AlbumEntity>> = albumDao.getAll()

    suspend fun insertAlbum(album: AlbumEntity) = albumDao.insert(album)

    suspend fun getAlbumById(id: Long): Flow<AlbumEntity?> = albumDao.getAlbumById(id)
}
