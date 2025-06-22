package io.github.malikshairali.lifeline

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.malikshairali.lifeline.presentation.navigation.NavExample
import io.github.malikshairali.lifeline.presentation.util.hasReadPermission

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var hasPermission by remember { mutableStateOf(hasReadPermission(this)) }

            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->
                hasPermission = granted
            }

            LaunchedEffect(Unit) {
                if (!hasPermission) {
//                    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//                        arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED)
//                    } else
//
                    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        READ_MEDIA_IMAGES
                    } else {
                        READ_EXTERNAL_STORAGE
                    }

                    launcher.launch(permission)
                }
            }

            if (hasPermission) {
                NavExample()
            }
        }
    }
}