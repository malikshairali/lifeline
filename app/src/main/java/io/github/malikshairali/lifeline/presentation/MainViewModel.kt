package io.github.malikshairali.lifeline.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import io.github.malikshairali.lifeline.data.FaceScanner
import io.github.malikshairali.lifeline.data.source.local.LocalImageDataSource
import io.github.malikshairali.lifeline.domain.model.Photo
import io.github.malikshairali.lifeline.domain.clusterFaces
import io.github.malikshairali.lifeline.domain.model.DateGroup
import io.github.malikshairali.lifeline.domain.model.FaceMetadata
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel: ViewModel() {

    val _similarPhotos = MutableStateFlow<List<DateGroup>>(emptyList())
    val similarPhotos = _similarPhotos.asStateFlow()


    suspend fun getPhotos(context: Context) {
        val photos = LocalImageDataSource.getDeviceImages(context)
//        groupedPhotos = groupPhotosByDate(photos)
        val faces = scanGallery(context = context, photos = photos)
//        println("Faces: $faces")
        val clusters = clusterFaces(faces)

        _similarPhotos.value = clusters.map {
            DateGroup(
                dateLabel = "",
                photos = it.faceIds.map { faceId ->
                    Photo(
                        id = 0,
                        uri = faces.first { face -> face.faceId == faceId }.photoUri,
                        timestamp = 0
                    )
                }
            )
        }

//        clusters.forEach { cluster ->
//            println("Person ${cluster.personId} has ${cluster.faceIds.size} faces")
//        }
    }

    suspend fun scanGallery(context: Context, photos: List<Photo>): List<FaceMetadata> {
        val allFaces = mutableListOf<FaceMetadata>()
        photos.forEach { photo ->
            val faces = FaceScanner.scanPhoto(context, photo)
            allFaces += faces
        }
        return allFaces
    }
}