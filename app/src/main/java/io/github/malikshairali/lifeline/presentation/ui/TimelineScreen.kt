package io.github.malikshairali.lifeline.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import io.github.malikshairali.lifeline.presentation.MainViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun TimelineScreen(
    viewModel: MainViewModel = MainViewModel()
) {
    val similarPhotos by viewModel.similarPhotos.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getPhotos(context = context)
    }

    val overlayVisible = remember { mutableStateOf(true) }
    val overlayScope = rememberCoroutineScope()
    var hideJob by remember { mutableStateOf<Job?>(null) }

    val restartOverlayTimer = rememberUpdatedState {
        overlayVisible.value = true
        hideJob?.cancel()
        hideJob = overlayScope.launch {
            delay(1000)
            overlayVisible.value = false
        }
    }


    val verticalPagerState = rememberPagerState { similarPhotos.size }
    VerticalPager(
        state = verticalPagerState, modifier = Modifier.fillMaxSize()
    ) { index ->
        val group = similarPhotos[index]

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            awaitPointerEvent() // fires on any touch/move
                            restartOverlayTimer.value()
                        }
                    }
                }) {
            val horizontalPagerState = rememberPagerState { group.photos.size }

            LaunchedEffect(horizontalPagerState.currentPage, verticalPagerState.currentPage) {
                restartOverlayTimer.value()
            }

            HorizontalPager(
                state = horizontalPagerState, modifier = Modifier.fillMaxSize()
            ) { page ->
                val photo = group.photos[page]

                val pageOffset =
                    ((horizontalPagerState.currentPage - page) + horizontalPagerState.currentPageOffsetFraction).absoluteValue

                Image(
                    painter = rememberAsyncImagePainter(model = photo.uri),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val scale = 1f - (0.1f * pageOffset)
                            val alpha = 1f - (0.5f * pageOffset)

                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        })
            }

            PageIndicatorDotRow(
                currentPage = horizontalPagerState.currentPage,
                total = group.photos.size,
                modifier = Modifier.alpha(0.68f)
            )

            AnimatedVisibility(
                visible = overlayVisible.value, enter = fadeIn(), exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .alpha(0.68f)
                ) {
                    Spacer(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .width(4.dp)
                            .height(32.dp)
                            .fillMaxHeight()
                            .background(if (index == 0) Color.Transparent else Color.White)
                    )

                    Surface(
                        shape = MaterialTheme.shapes.large, color = Color.White
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = group.dateLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .width(4.dp)
                            .fillMaxHeight()
                            .background(if (index == similarPhotos.size - 1) Color.Transparent else Color.White)
                    )
                }
            }
        }
    }
}

@Composable
fun PageIndicatorDotRow(currentPage: Int, total: Int, modifier: Modifier) {

    if (total == 1) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 64.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .background(Color.LightGray, shape = CircleShape)
                .padding(vertical = 6.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(total) { index ->
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = if (index == currentPage) Color.White else Color.DarkGray,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

