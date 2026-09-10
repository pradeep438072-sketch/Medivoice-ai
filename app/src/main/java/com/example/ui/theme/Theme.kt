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

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFF4DB6AC),
    onPrimary = Color(0xFF003731),
    primaryContainer = Color(0xFF004D40),
    onPrimaryContainer = Color(0xFF80CBC4),
    secondary = Color(0xFF4FC3F7),
    onSecondary = Color(0xFF003549),
    secondaryContainer = Color(0xFF01579B),
    onSecondaryContainer = Color(0xFFB3E5FC),
    tertiary = Color(0xFFFF8A65),
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = Color(0xFFE2E8F0),
    onSurface = Color(0xFFE2E8F0),
    outline = OutlineDark
  )

private val LightColorScheme =
  lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    primaryContainer = TealContainer,
    onPrimaryContainer = TealOnContainer,
    secondary = CyanSecondary,
    onSecondary = Color.White,
    secondaryContainer = CyanSecondaryContainer,
    onSecondaryContainer = CyanOnSecondary,
    tertiary = CoralTertiary,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = Color(0xFF1E293B),
    onSurface = Color(0xFF0F172A),
    outline = OutlineLight
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
