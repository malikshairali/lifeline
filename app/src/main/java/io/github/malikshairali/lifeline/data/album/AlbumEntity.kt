package io.github.malikshairali.lifeline.data.album

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.random.Random

@Entity(tableName = "albums")
data class AlbumEntity(
    @PrimaryKey val id: Long = Random.nextLong(),
    val title: String,
    val startDate: Long,
    val endDate: Long,
    val coverUri: String,
    val size: Int
)