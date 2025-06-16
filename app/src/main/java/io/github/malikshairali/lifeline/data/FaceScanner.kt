package io.github.malikshairali.lifeline.data

import android.content.Context
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import io.github.malikshairali.lifeline.domain.model.FaceMetadata
import io.github.malikshairali.lifeline.domain.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object FaceScanner {

    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .build()
    )

    suspend fun scanPhoto(context: Context, photo: Photo): List<FaceMetadata> = withContext(Dispatchers.IO) {
        val image = InputImage.fromFilePath(context, photo.uri)
        val result = detector.process(image).await()

        result.mapIndexed { index, face ->
            val bounds = face.boundingBox
            val centerX = bounds.exactCenterX()
            val centerY = bounds.exactCenterY()
            val size = bounds.width().toFloat()

            FaceMetadata(
                faceId = "${photo.id}_$index",
                photoUri = photo.uri,
                boundingBox = bounds,
                centerX = centerX,
                centerY = centerY,
                size = size
            )
        }
    }
}
