package io.github.malikshairali.lifeline.presentation.ui.create

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.malikshairali.lifeline.presentation.theme.MyPrimaryPurple
import io.github.malikshairali.lifeline.presentation.theme.MySecondaryPink
import io.github.malikshairali.lifeline.presentation.ui.create.components.AlbumDateStep
import io.github.malikshairali.lifeline.presentation.ui.create.components.AlbumNameStep
import io.github.malikshairali.lifeline.presentation.ui.create.components.AlbumPreviewStep
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAlbum(
    onBack: () -> Unit,
    onCreated: (id: Long) -> Unit,
    viewModel: CreateAlbumViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState { 3 }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current



    Column(
        modifier = Modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MyPrimaryPurple,
                        MySecondaryPink
                    )
                )
            )
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(horizontal = 16.dp)
    ) {
        // Top App Bar
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.offset(x = (-8).dp),
                onClick = {
                    if (pagerState.currentPage == 0) {
                        onBack()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Create Album",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step Progress Indicator (3 steps)
        Column {
            Text(
                text = "Step ${pagerState.currentPage + 1} of 3",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (index <= pagerState.currentPage) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary.copy(
                                    alpha = 0.3f
                                )
                            )
                    )
                    if (index < 2) Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false
        ) { page ->
            when (page) {
                0 -> AlbumNameStep(
                    name = uiState.albumName,
                    onNameChange = { viewModel.updateAlbumName(it) },
                    onNext = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    }
                )

                1 -> AlbumDateStep(
                    fromDate = uiState.fromDate,
                    toDate = uiState.toDate,
                    onFromDateChange = { viewModel.updateFromDate(it) },
                    onToDateChange = { viewModel.updateToDate(it) },
                    onNext = {
                        coroutineScope.launch {
                            viewModel.loadPhotos(context = context)
                            pagerState.animateScrollToPage(2)
                        }
                    }
                )

                2 -> AlbumPreviewStep(
                    viewModel = viewModel,
                    onCreate = {
                        val id = viewModel.createAlbum()
                        onCreated(id)
                    }
                )
            }
        }
    }

    BackHandler(enabled = pagerState.currentPage > 0) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage - 1)
        }
    }
}


