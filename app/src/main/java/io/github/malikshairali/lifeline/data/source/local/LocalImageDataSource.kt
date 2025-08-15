package io.github.malikshairali.lifeline.data.source.local

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import io.github.malikshairali.lifeline.data.album.PhotoEntity

object LocalImageDataSource {
    fun doesPhotoExist(context: Context, startMillis: Long, endMillis: Long): Boolean {
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.DATE_ADDED
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC LIMIT 1"

        context.contentResolver.query(
            collection,
            projection,
            null,  // no selection; we'll filter in code
            null,
            sortOrder
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                // Try DATE_TAKEN
                var timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN))
                if (timestamp <= 0L) {
                    // Fallback to DATE_MODIFIED (convert seconds to millis)
                    timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)) * 1000L
                }
                if (timestamp <= 0L) {
                    // Fallback to DATE_ADDED
                    timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)) * 1000L
                }

                if (timestamp in startMillis..endMillis) {
                    return true // found at least one photo
                }
            }
        }

        return false // none found
    }

    fun getDeviceImagesBetween(
        context: Context,
        startMillis: Long,
        endMillis: Long
    ): List<PhotoEntity> {
        val photos = mutableListOf<PhotoEntity>()
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.DATE_ADDED
        )

        val selection = """
            (${MediaStore.Images.Media.DATE_TAKEN} BETWEEN ? AND ?)
            OR (${MediaStore.Images.Media.DATE_TAKEN} = 0 AND ${MediaStore.Images.Media.DATE_MODIFIED} * 1000 BETWEEN ? AND ?)
            OR (${MediaStore.Images.Media.DATE_TAKEN} = 0 AND ${MediaStore.Images.Media.DATE_MODIFIED} = 0 AND ${MediaStore.Images.Media.DATE_ADDED} * 1000 BETWEEN ? AND ?)
        """.trimIndent()

        val selectionArgs = arrayOf(
            startMillis.toString(), endMillis.toString(), // DATE_TAKEN
            startMillis.toString(), endMillis.toString(), // DATE_MODIFIED
            startMillis.toString(), endMillis.toString()  // DATE_ADDED
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val takenCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            val modifiedCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            val addedCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                var timestamp = cursor.getLong(takenCol)
                println(timestamp)

                if (timestamp <= 0L) {
                    timestamp = cursor.getLong(modifiedCol) * 1000L
                    if (timestamp <= 0L) {
                        timestamp = cursor.getLong(addedCol) * 1000L
                    }
                }

                if (timestamp in startMillis..endMillis) {
                    val uri = ContentUris.withAppendedId(collection, id)
                    photos += PhotoEntity(
                        id = id,
                        uri = uri.toString(),
                        timestamp = timestamp
                    )
                }
            }
        }

        return photos
    }
}
