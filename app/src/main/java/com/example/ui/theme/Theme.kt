package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BeastDarkColorScheme = darkColorScheme(
    primary = BeastGoldPrimary,
    onPrimary = Color.Black,
    primaryContainer = BeastGoldDark,
    onPrimaryContainer = Color.White,
    secondary = BeastGoldBright,
    onSecondary = Color.Black,
    secondaryContainer = BeastSurfaceVariant,
    onSecondaryContainer = BeastTextPrimary,
    tertiary = BeastRatingGold,
    onTertiary = Color.Black,
    background = BeastBackground,
    onBackground = BeastTextPrimary,
    surface = BeastSurface,
    onSurface = BeastTextPrimary,
    surfaceVariant = BeastSurfaceVariant,
    onSurfaceVariant = BeastTextSecondary,
    outline = BeastCardBorder,
    error = BeastAccentRed,
    onError = Color.White
)

@Composable
fun BeastMoviesTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BeastDarkColorScheme,
        typography = Typography,
        content = content
    )
}

// Alias for default template compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    BeastMoviesTheme(content = content)
}
