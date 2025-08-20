package io.github.malikshairali.lifeline.presentation.ui.create.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.malikshairali.lifeline.presentation.ui.create.CreateAlbumViewModel

@Composable
fun AlbumPreviewStep(
    viewModel: CreateAlbumViewModel,
    modifier: Modifier = Modifier,
    onCreate: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        val photos = uiState.photos

        when {
            photos == null -> {
                Text(
                    text = "Loading photos...",
                    color = Color.White
                )
            }

            photos.isEmpty() -> {
                Text(
                    text = "No photos found",
                    color = Color.White
                )
            }

            else -> {
                Column(modifier = Modifier.fillMaxSize().background(Color.Transparent)) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Preview",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "These photos will be part of your new album.",
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyVerticalGrid(
                        modifier = Modifier.weight(1f),
                        columns = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.spacedBy(1.dp),
                        horizontalArrangement = Arrangement.spacedBy(1.dp),
                        contentPadding = PaddingValues(1.dp)
                    ) {
                        items(photos) { photo ->
                            AsyncImage(
                                model = photo.uri,
                                contentDescription = null,
                                modifier = Modifier.aspectRatio(1f),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onCreate()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Create",
                            color = Color(0xFF9C27B0),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}