package com.example.proyecto1_plataformasmoviles_domingazo.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    secondary = AquaAccent,
    background = Color(0xFFF5F7FA),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    error = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = IndigoSecondary,
    secondary = AquaAccent,
    background = Color(0xFF121212),
    surface = DarkSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = ErrorRed
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

    // Color scheme según el tema
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (savedDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> if (savedDark) DarkColorScheme else LightColorScheme
    }

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