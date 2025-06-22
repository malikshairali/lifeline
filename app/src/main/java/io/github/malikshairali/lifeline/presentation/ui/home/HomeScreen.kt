package io.github.malikshairali.lifeline.presentation.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import io.github.malikshairali.lifeline.R
import io.github.malikshairali.lifeline.data.album.AlbumEntity
import io.github.malikshairali.lifeline.presentation.ui.dialogs.DateSelectionDialog
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToTimeline: (Long) -> Unit
) {
    val albums by viewModel.albums.collectAsState()
    var showDatePickerDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = "My Digital Albums",
                    fontWeight = FontWeight.Bold
                )
            })
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = listState.isScrollingUp().value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = { showDatePickerDialog = true },
                    content = {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Create New Album"
                        )
                    }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding)
        ) {
            if (albums.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        contentScale = ContentScale.Crop,
                        painter = painterResource(R.drawable.placeholder_homepage),
                        contentDescription = "Placeholder photo"
                    )
                    Text(
                        text = "No albums yet\n" + "Tap the + button to create your first memory.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(albums) { album ->
                        AlbumCard(album) { onNavigateToTimeline(album.id) }
                    }
                }
            }
        }

        if (showDatePickerDialog) {
            DateSelectionDialog(
                onDismiss = { showDatePickerDialog = false },
                onConfirmSelection = { selectionType, startDate, endDate ->
                    showDatePickerDialog = false
                    val photos = viewModel.getPhotos(
                        context = context,
                        startMillis = startDate,
                        endMillis = endDate
                    )

                    if (photos.isNotEmpty()) {
                        val id = Random.nextLong()
                        viewModel.saveAlbum(
                            AlbumEntity(
                                id = id,
                                title = "New Album",
                                startDate = startDate,
                                endDate = endDate,
                                coverUri = photos.first().photos.first().uri.toString(),
                                size = photos.size
                            )
                        )
                        onNavigateToTimeline(id)
                    }
                }
            )
        }
    }
}

@Composable
fun AlbumCard(
    album: AlbumEntity,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        onClick = { onClick() }
    ) {
        Box(
            modifier = Modifier.aspectRatio(2f),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = rememberAsyncImagePainter(album.coverUri),
                contentDescription = "Cover photo",
                contentScale = ContentScale.Crop,
            )

            Row(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(2f)
                ) {
                    Text(
                        text = album.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = formatDateRange(album.startDate, album.endDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val photosCount = if (album.size > 1) {
                        "${album.size} photos"
                    } else {
                        "${album.size} photo"
                    }
                    Text(
                        text = photosCount,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = "Create New Album",
                        tint = Color.White
                    )
                }
            }
        }

    }
}

@Composable
fun LazyListState.isScrollingUp(): State<Boolean> {
    return produceState(initialValue = true) {
        var lastIndex = 0
        var lastScroll = Int.MAX_VALUE
        snapshotFlow {
            firstVisibleItemIndex to firstVisibleItemScrollOffset
        }.collect { (currentIndex, currentScroll) ->
            if (currentIndex != lastIndex || currentScroll != lastScroll) {
                value = currentIndex < lastIndex ||
                        (currentIndex == lastIndex && currentScroll < lastScroll)
                lastIndex = currentIndex
                lastScroll = currentScroll
            }
        }
    }
}

fun formatDateRange(startMillis: Long, endMillis: Long): String {
    val formatter = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
    val start = formatter.format(Date(startMillis))
    val end = formatter.format(Date(endMillis))
    return if (start == end) start else "$start - $end"
}