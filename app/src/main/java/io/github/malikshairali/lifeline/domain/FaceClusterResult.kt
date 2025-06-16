package io.github.malikshairali.lifeline.domain

import io.github.malikshairali.lifeline.domain.model.FaceMetadata
import kotlin.math.sqrt

data class FaceClusterResult(
    val personId: String,
    val faceIds: List<String>,
    val representativeFaceId: String
)

fun clusterFaces(
    faces: List<FaceMetadata>,
    threshold: Float = 50f
): List<FaceClusterResult> {
    if (faces.isEmpty()) return emptyList()

    val features = faces.map { floatArrayOf(it.centerX, it.centerY, it.size) }.toMutableList()
    val clusters = mutableListOf<MutableList<Int>>() // indices of faces

    for (i in features.indices) {
        val current = features[i]
        var added = false

        for (cluster in clusters) {
            val repIndex = cluster.first()
            val dist = euclideanDistance(current, features[repIndex])
            if (dist < threshold) {
                cluster.add(i)
                added = true
                break
            }
        }

        if (!added) {
            clusters.add(mutableListOf(i))
        }
    }

    return clusters.mapIndexed { index, clusterIndices ->
        val id = "person_${index + 1}"
        val faceIds = clusterIndices.map { faces[it].faceId }
        FaceClusterResult(
            personId = id,
            faceIds = faceIds,
            representativeFaceId = faceIds.first()
        )
    }
}

private fun euclideanDistance(a: FloatArray, b: FloatArray): Float {
    return sqrt((a[0] - b[0]) * (a[0] - b[0]) +
                (a[1] - b[1]) * (a[1] - b[1]) +
                (a[2] - b[2]) * (a[2] - b[2]))
}
