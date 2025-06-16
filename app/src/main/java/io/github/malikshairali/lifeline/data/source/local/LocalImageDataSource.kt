package io.github.malikshairali.lifeline.data.source.local

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import io.github.malikshairali.lifeline.domain.model.Photo

object LocalImageDataSource {
    fun getDeviceImages(context: Context): List<Photo> {
        val photos = mutableListOf<Photo>()
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.DATE_ADDED
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        context.contentResolver.query(
            collection, projection, null, null, sortOrder
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val takenCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            val modifiedCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            val addedCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                var timestamp = cursor.getLong(takenCol)

                if (timestamp <= 0L) {
                    timestamp = cursor.getLong(modifiedCol) * 1000L
                    if (timestamp <= 0L) {
                        timestamp = cursor.getLong(addedCol) * 1000L
                    }
                }

                val uri = ContentUris.withAppendedId(collection, id)
                photos += Photo(id, uri, timestamp)
            }
        }

        return photos
    }
}