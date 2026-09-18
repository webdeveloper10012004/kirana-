package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6CDAA0),
    onPrimary = Color(0xFF00381F),
    primaryContainer = Color(0xFF005230),
    onPrimaryContainer = Color(0xFF88F7BC),
    secondary = Color(0xFFFFB77D),
    onSecondary = Color(0xFF4D2600),
    secondaryContainer = Color(0xFF6D3900),
    onSecondaryContainer = Color(0xFFFFDCBE),
    background = Color(0xFF191C1A),
    surface = Color(0xFF191C1A),
    onBackground = Color(0xFFE1E3DF),
    onSurface = Color(0xFFE1E3DF),
    surfaceVariant = Color(0xFF404943),
    onSurfaceVariant = Color(0xFFBFC9C2),
    outline = Color(0xFF89938C)
)

private val LightColorScheme = lightColorScheme(
    primary = KiranaGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB8F5D2),
    onPrimaryContainer = Color(0xFF002111),
    secondary = KiranaSaffronSecondary,
    onSecondary = Color.White,
    secondaryContainer = KiranaSaffronLight,
    onSecondaryContainer = Color(0xFF331200),
    background = SurfaceWarm,
    surface = CardBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFE8EFE9),
    onSurfaceVariant = TextSecondary,
    outline = OutlineBorder,
    error = StockOutColor,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our branded Kirana palette consistently
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
