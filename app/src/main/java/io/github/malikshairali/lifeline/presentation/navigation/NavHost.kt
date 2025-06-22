package io.github.malikshairali.lifeline.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import io.github.malikshairali.lifeline.presentation.ui.home.HomeScreen
import io.github.malikshairali.lifeline.presentation.ui.timeline.TimelineScreen

data object Home
data class Timeline(val id: Long)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavExample() {

    val backStack = remember { mutableStateListOf<Any>(Home) }
    val viewModelDecorator = rememberViewModelStoreNavEntryDecorator()

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is Home -> NavEntry(key) {
                    HomeScreen { id ->
                        backStack.add(Timeline(id))
                    }
                }

                is Timeline -> NavEntry(key) {
                    TimelineScreen(
                        id = key.id
                    )
                }

                else -> NavEntry(Unit) { Text("Unknown route") }
            }
        },
        entryDecorators =
            listOf(
                rememberSceneSetupNavEntryDecorator(),
                rememberSavedStateNavEntryDecorator(),
                viewModelDecorator
            )
    )
}