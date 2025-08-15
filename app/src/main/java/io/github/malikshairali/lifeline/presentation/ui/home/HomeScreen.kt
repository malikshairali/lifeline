package io.github.malikshairali.lifeline.presentation.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.malikshairali.lifeline.R
import io.github.malikshairali.lifeline.presentation.theme.MyPrimaryPurple
import io.github.malikshairali.lifeline.presentation.theme.MySecondaryPink
import io.github.malikshairali.lifeline.presentation.ui.home.components.AlbumCard
import io.github.malikshairali.lifeline.presentation.util.isScrollingUp
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToTimeline: (Long) -> Unit,
    onNavigateToCreateAlbum: () -> Unit
) {
    val albums by viewModel.albums.collectAsState()
    val error by viewModel.error.collectAsState()

    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        if (error != null) {
            snackbarHostState.showSnackbar(error!!)
            viewModel.removeError()
        }
    }

    Box(
        contentAlignment = Alignment.BottomCenter
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MyPrimaryPurple,
                            MySecondaryPink
                        )
                    )
                )
                .padding(paddingValues = WindowInsets.systemBars.asPaddingValues()),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "My Digital Albums",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            if (albums.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxHeight()) {
                        Placeholder()
                    }
                }
            }

            items(albums) { album ->
                AlbumCard(album) { onNavigateToTimeline(album.id) }
            }
        }

        AnimatedVisibility(
            visible = listState.isScrollingUp().value,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 24.dp),
                onClick = { onNavigateToCreateAlbum() },
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Create New Album"
                    )
                }
            )
        }
    }
}

@Composable
private fun Placeholder() {
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
            color = Color.White,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
    }
}