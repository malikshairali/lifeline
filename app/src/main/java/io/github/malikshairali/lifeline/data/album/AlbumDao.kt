package io.github.malikshairali.lifeline.data.album

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(album: AlbumEntity)

    @Query("SELECT * FROM albums ORDER BY startDate DESC")
    fun getAll(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM albums WHERE id = :id")
    fun getAlbumById(id: Long): Flow<AlbumEntity?>
}
