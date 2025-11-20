package com.example.proyecto1_plataformasmoviles_domingazo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    secondary = AquaAccent,
    onSecondary = Color.White,
    tertiary = AmberAccent,
    onTertiary = Color(0xFF5B3200),
    background = SurfaceGray,
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = SurfaceGray,
    onSurfaceVariant = Color(0xFF475569),
    outline = OutlineLight,
    error = ErrorRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = AquaAccent,
    onPrimary = Color.White,
    secondary = IndigoSecondary,
    onSecondary = Color.White,
    tertiary = AmberAccent,
    onTertiary = Color(0xFF2F2000),
    background = DarkSurface,
    onBackground = Color(0xFFE2E8F0),
    surface = Color(0xFF161B27),
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = DarkOutline,
    onSurfaceVariant = Color(0xFFD0D7E2),
    outline = DarkOutline,
    error = ErrorRed,
    onError = Color.White
)

private val AppTypography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 32.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp)
)

@Composable
fun Proyecto1PlataformasMovilesDomingazoTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val settingsRepo = remember { SettingsRepository(context) }
    val systemDark = isSystemInDarkTheme()
    val coroutineScope = rememberCoroutineScope()

    // Leer tema guardado (se actualiza automáticamente)
    val savedDark by settingsRepo.isDarkMode.collectAsState(initial = systemDark)

    val colorScheme = if (savedDark) DarkColorScheme else LightColorScheme

    // Función para cambiar tema
    val toggleTheme: (Boolean) -> Unit = { enabled ->
        coroutineScope.launch {
            settingsRepo.setDarkMode(enabled)
        }
    }

    CompositionLocalProvider(LocalDarkMode provides toggleTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

val LocalDarkMode = staticCompositionLocalOf<(Boolean) -> Unit> {
    error("No theme toggle function provided")
}
