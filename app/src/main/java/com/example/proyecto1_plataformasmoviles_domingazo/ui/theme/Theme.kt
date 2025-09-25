package com.example.proyecto1_plataformasmoviles_domingazo.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorPalette = darkColorScheme(
    primary = IndigoSecondary,
    onPrimary = ColorTokens.white,
    primaryContainer = DarkSurface,
    onPrimaryContainer = ColorTokens.white,
    secondary = AquaAccent,
    onSecondary = ColorTokens.black,
    tertiary = AmberAccent,
    onTertiary = ColorTokens.black,
    background = DarkSurface,
    onBackground = ColorTokens.white,
    surface = DarkSurface,
    onSurface = ColorTokens.white,
    surfaceVariant = DarkOutline,
    onSurfaceVariant = ColorTokens.white,
)

private val LightColorPalette = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = ColorTokens.white,
    primaryContainer = IndigoPrimary.copy(alpha = 0.12f),
    onPrimaryContainer = IndigoPrimary,
    secondary = AquaAccent,
    onSecondary = ColorTokens.black,
    tertiary = AmberAccent,
    onTertiary = ColorTokens.black,
    background = ColorTokens.white,
    onBackground = ColorTokens.black,
    surface = ColorTokens.white,
    onSurface = ColorTokens.black,
    surfaceVariant = SurfaceGray,
    onSurfaceVariant = ColorTokens.black,
)

@Composable
fun ProyectoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }
        darkTheme -> DarkColorPalette
        else -> LightColorPalette
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
