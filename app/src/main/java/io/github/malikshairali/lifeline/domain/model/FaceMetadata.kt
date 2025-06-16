package io.github.malikshairali.lifeline.domain.model

import android.graphics.Rect
import android.net.Uri

data class FaceMetadata(
    val faceId: String,
    val photoUri: Uri,
    val boundingBox: Rect,
    val centerX: Float,
    val centerY: Float,
    val size: Float
)
