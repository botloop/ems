package com.ems.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light — Salt and Pepper: white ground, pepper accents
private val LightColorScheme = lightColorScheme(
    primary            = Pepper,
    onPrimary          = Salt,
    primaryContainer   = PepperMid,
    onPrimaryContainer = PepperDark,
    secondary          = PepperLight,
    onSecondary        = Salt,
    secondaryContainer = PepperMid,
    onSecondaryContainer = Pepper,
    error              = EmergencyRed,
    onError            = Salt,
    errorContainer     = EmergencyRedContainer,
    onErrorContainer   = Color(0xFF8B1A10),
    background         = Salt,
    onBackground       = Pepper,
    surface            = Salt,
    onSurface          = Pepper,
    surfaceVariant     = PepperMid,
    onSurfaceVariant   = Pepper,
    outline            = PepperLight,
)

// Dark — inverted: pepper ground, salt accents
private val DarkColorScheme = darkColorScheme(
    primary            = PepperMid,
    onPrimary          = PepperDark,
    primaryContainer   = PepperDark,
    onPrimaryContainer = PepperMid,
    secondary          = PepperLight,
    onSecondary        = PepperDark,
    error              = EmergencyRedLight,
    onError            = Color.Black,
    background         = DarkBackground,
    onBackground       = Salt,
    surface            = DarkSurface,
    onSurface          = Salt,
    surfaceVariant     = DarkCard,
    onSurfaceVariant   = PepperMid,
    outline            = DarkDivider,
)

@Composable
fun EmsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = EmsTypography,
        shapes = EmsShapes,
        content = content
    )
}
