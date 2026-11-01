package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Beast Movies Theme Colors (Refined Cinematic Dark & Gold Palette)
val BeastBackground = Color(0xFF08090D)
val BeastSurface = Color(0xFF14151B)
val BeastSurfaceVariant = Color(0xFF1E2029)
val BeastSurfaceElevated = Color(0xFF262833)
val BeastCardBorder = Color(0xFF2A2B36)

val BeastGoldPrimary = Color(0xFFD6AC58)
val BeastGoldBright = Color(0xFFE5B864)
val BeastGoldDark = Color(0xFFB88E3E)
val BeastGoldLight = Color(0xFFF3D283)

val BeastTextPrimary = Color(0xFFFFFFFF)
val BeastTextSecondary = Color(0xFFA1A1AA)
val BeastTextMuted = Color(0xFF71717A)

val BeastRatingGold = Color(0xFFFFB800)
val BeastAccentRed = Color(0xFFEF4444)
val BeastSuccessGreen = Color(0xFF10B981)

val GoldButtonGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFFE5B864),
        Color(0xFFD6AC58),
        Color(0xFFB88E3E)
    )
)

val HeroCardGradient = Brush.verticalGradient(
    colors = listOf(
        Color.Transparent,
        Color(0x8008090D),
        Color(0xF008090D)
    )
)

val DetailsBackdropGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0x33000000),
        Color(0x9908090D),
        Color(0xFF08090D)
    )
)
