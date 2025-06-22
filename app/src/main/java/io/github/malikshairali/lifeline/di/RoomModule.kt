package io.github.malikshairali.lifeline.di


import android.content.Context
import androidx.room.Room
import io.github.malikshairali.lifeline.data.album.AlbumDao
import io.github.malikshairali.lifeline.db.AppDatabase
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class RoomModule {
    @Single
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "lifeline.db").build()
    }

    @Single
    fun provideAlbumDao(database: AppDatabase): AlbumDao {
        return database.albumDao()
    }
}
