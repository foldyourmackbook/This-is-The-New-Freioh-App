package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TikTokMagenta,
    onPrimary = Color.White,
    primaryContainer = TikTokDarkBg,
    onPrimaryContainer = Color.White,
    secondary = TikTokCyan,
    onSecondary = Color.Black,
    tertiary = TikTokBlueVerify,
    background = TikTokBlack,
    onBackground = TikTokWhite,
    surface = TikTokDarkBg,
    onSurface = TikTokWhite,
    surfaceVariant = TikTokCardDark,
    onSurfaceVariant = TikTokLightGray,
    outline = TikTokGray
)

private val LightColorScheme = lightColorScheme(
    primary = TikTokMagenta,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF1F1F2),
    onPrimaryContainer = Color(0xFF161823),
    secondary = TikTokCyan,
    onSecondary = Color.Black,
    tertiary = TikTokBlueVerify,
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF161823),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF161823),
    surfaceVariant = Color(0xFFF8F8F8),
    onSurfaceVariant = Color(0xFF555555),
    outline = Color(0xFFE3E3E4)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
