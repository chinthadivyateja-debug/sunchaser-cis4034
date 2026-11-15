// =============================================================
// Theme.kt
// Wraps the whole app in a Material 3 light/dark colour scheme.
// =============================================================

package uk.ac.tees.mad.sunchaser.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColours = lightColorScheme(
    primary = SunOrange,
    secondary = SunYellow,
    background = WarmWhite,
    onBackground = DeepCharcoal,
    surface = WarmWhite,
    onSurface = DeepCharcoal
)

private val DarkColours = darkColorScheme(
    primary = SunYellow,
    secondary = SunOrange,
    background = NightBlack,
    onBackground = MoonSilver,
    surface = NightBlack,
    onSurface = MoonSilver
)

@Composable
fun SunChaserTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColours
        else -> LightColours
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
