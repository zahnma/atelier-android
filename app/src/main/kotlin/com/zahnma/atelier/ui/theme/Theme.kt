package com.zahnma.atelier.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Burgundy = Color(0xFF6B1D2A)
private val BurgundyDark = Color(0xFFCF8A96)
private val OffWhite = Color(0xFFFAFAF8)
private val NearBlack = Color(0xFF141414)
private val DarkSurface = Color(0xFF121212)
private val DarkBackground = Color(0xFF0E0E0E)

private val LightColors = lightColorScheme(
    primary = Burgundy,
    onPrimary = Color.White,
    secondary = Color(0xFF4A4A4A),
    onSecondary = Color.White,
    background = OffWhite,
    onBackground = NearBlack,
    surface = Color.White,
    onSurface = NearBlack,
    surfaceVariant = Color(0xFFF0EFEB),
    onSurfaceVariant = Color(0xFF5C5C5C),
    outline = Color(0xFFD8D5CF),
)

private val DarkColors = darkColorScheme(
    primary = BurgundyDark,
    onPrimary = NearBlack,
    secondary = Color(0xFFB8B8B8),
    onSecondary = NearBlack,
    background = DarkBackground,
    onBackground = Color(0xFFF2F2F2),
    surface = DarkSurface,
    onSurface = Color(0xFFF2F2F2),
    surfaceVariant = Color(0xFF1E1E1E),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF3A3A3A),
)

@Composable
fun AtelierTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AtelierTypography,
        content = content,
    )
}
