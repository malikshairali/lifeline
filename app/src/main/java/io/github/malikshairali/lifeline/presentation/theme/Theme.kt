package io.github.malikshairali.lifeline.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


val MyPrimaryPurple = Color(0xFF6740f5)
val MySecondaryPink = Color(0xff9e03a3)

// Light Color Scheme
private val LightColors = lightColorScheme(
    primary = MyPrimaryPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEADDFF), // Light purple container
    onPrimaryContainer = Color(0xFF31004A), // Dark purple text for on primary container

    secondary = MySecondaryPink,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFD8E4), // Light pink container
    onSecondaryContainer = Color(0xFF3E001D), // Dark pink text for on secondary container

    tertiary = Color(0xFF7D52B3), // A slightly desaturated, darker purple
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEFDBFF),
    onTertiaryContainer = Color(0xFF320049),

    background = Color(0xFFFFFBFF), // Very light, almost white
    onBackground = Color.White, // MODIFIED: White text on background
    surface = Color.White, // MODIFIED: Explicitly white for component backgrounds
    onSurface = MyPrimaryPurple, // MODIFIED: Purple text on white surfaces
    surfaceVariant = Color(0xFFEDDEE8), // A light gray-pinkish tone
    onSurfaceVariant = Color(0xFF4D444B), // Medium gray for text
    outline = MyPrimaryPurple, // MODIFIED: Purple outline for components
    outlineVariant = Color(0xFFD2C2CB), // Lighter gray for outlines

    error = Color(0xFFBA1A1A), // Standard Material Red
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

// Dark Color Scheme
private val DarkColors = darkColorScheme(
//    primary = Color(0xFFD0BCFF), // Lighter purple for dark theme
//    onPrimary = Color(0xFF381E72), // Darker purple for text on primary
//    primaryContainer = Color(0xFF4F378A), // Mid-dark purple container
//    onPrimaryContainer = Color(0xFFEADDFF), // Light purple text for on primary container
//
//    secondary = Color(0xFFFFB0C8), // Lighter pink for dark theme
//    onSecondary = Color(0xFF650033), // Darker pink for text on secondary
//    secondaryContainer = Color(0xFF832253), // Mid-dark pink container
//    onSecondaryContainer = Color(0xFFFFD8E4), // Light pink text for on secondary container
//
//    tertiary = Color(0xFFEFB8C8), // Lighter version of the tertiary for dark theme
//    onTertiary = Color(0xFF4A255E),
//    tertiaryContainer = Color(0xFF633B76),
//    onTertiaryContainer = Color(0xFFEFDBFF),
//
//    background = Color(0xFF1F1A1C), // Dark gray background
//    onBackground = Color.White, // MODIFIED: White text on dark background
//    surface = Color.White, // MODIFIED: White background for components like TextField, Card
//    onSurface = MyPrimaryPurple, // MODIFIED: Purple text on white surfaces
//    surfaceVariant = Color(0xFF4D444B), // A mid-dark gray-pinkish tone
//    onSurfaceVariant = Color(0xFFD2C2CB), // Light gray for text
//    outline = MyPrimaryPurple, // MODIFIED: Purple outline for components like TextField
//    outlineVariant = Color(0xFF4D444B), // Darker gray for outlines
//
//    error = Color(0xFFFFB4AB),
//    onError = Color(0xFF690005),
//    errorContainer = Color(0xFF93000A),
//    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun LifelineTheme(
    content: @Composable () -> Unit
) {
    val darkScheme =
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//        dynamicDarkColorScheme(LocalContext.current)
//    } else
        DarkColors

    val lightScheme =
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//        dynamicLightColorScheme(LocalContext.current)
//    } else
        LightColors

    val colorScheme = if (isSystemInDarkTheme()) darkScheme else lightScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
//            window.statusBarColor = Color.Transparent.toArgb() // Or Color.Transparent.toArgb()
//            window.navigationBarColor = Color.Transparent.toArgb() // Or Color.Transparent.toArgb()

            // Set status bar icons to light or dark based on theme
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            // Set navigation bar icons to light or dark based on theme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
