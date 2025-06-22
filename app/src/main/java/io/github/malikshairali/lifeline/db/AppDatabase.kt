package io.github.malikshairali.lifeline.db

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.malikshairali.lifeline.data.album.AlbumDao
import io.github.malikshairali.lifeline.data.album.AlbumEntity

@Database(
    entities = [AlbumEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun albumDao(): AlbumDao
}
