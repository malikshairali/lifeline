package io.github.malikshairali.lifeline.presentation.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.malikshairali.lifeline.presentation.ui.dialogs.DateSelectionDialog

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCreateTimeline: () -> Unit,
    // We'll add a list of saved timelines here later
) {
    var showDatePickerDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Digital Albums") })
        },
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = { showDatePickerDialog = true },
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Create New Album"
                    )
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Later: This will be a list of saved albums/timelines
            Text("Your saved albums will appear here.")
        }

        if (showDatePickerDialog) {
            DateSelectionDialog(
                onDismiss = { showDatePickerDialog = false },
                onConfirmSelection = { startDate, endDate, selectionType ->
                    // For now, just log. Later, this will trigger fetching and navigation
                    // to the timeline view.
                    println("Date selected: $selectionType, Start: $startDate, End: $endDate")
                    showDatePickerDialog = false // Dismiss dialog
                    // TODO: Navigate to a new screen/state to show this temporary timeline
                    // For now, let's assume onNavigateToCreateTimeline means "start the process"
                    onNavigateToCreateTimeline() // Placeholder, we'll pass dates later
                }
            )
        }
    }
}