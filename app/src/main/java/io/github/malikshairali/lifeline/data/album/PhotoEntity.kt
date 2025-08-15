package io.github.malikshairali.lifeline.data.album

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.random.Random

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey
    val id: Long = Random.nextLong(),
    val albumId: Long = 0,
    val uri: String,
    val timestamp: Long,
    val hidden: Boolean = false
)