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
  primary = Color(0xFFA8C7FA),
  onPrimary = Color(0xFF003063),
  primaryContainer = QuestPrimaryDark,
  onPrimaryContainer = Color(0xFFD3E4FF),
  secondary = Color(0xFFBCC7DB),
  onSecondary = Color(0xFF263140),
  secondaryContainer = Color(0xFF3C4858),
  onSecondaryContainer = Color(0xFFD8E3F8),
  tertiary = XpGoldLight,
  onTertiary = Color(0xFF452B00),
  background = DarkBackground,
  onBackground = DarkOnBackground,
  surface = DarkSurface,
  onSurface = DarkOnSurface,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = DarkOnSurfaceVariant,
  outline = DarkBorder,
  error = Color(0xFFFFB4AB),
  onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
  primary = QuestPrimary,
  onPrimary = Color.White,
  primaryContainer = QuestPrimaryContainer,
  onPrimaryContainer = QuestOnPrimaryContainer,
  secondary = QuestSecondary,
  onSecondary = Color.White,
  secondaryContainer = QuestSecondaryContainer,
  onSecondaryContainer = QuestOnSecondaryContainer,
  tertiary = XpGold,
  onTertiary = Color.White,
  background = LightBackground,
  onBackground = LightOnBackground,
  surface = LightSurface,
  onSurface = LightOnSurface,
  surfaceVariant = LightSurfaceVariant,
  onSurfaceVariant = LightOnSurfaceVariant,
  outline = LightBorder,
  error = HeartRose,
  onError = Color.White
)

@Composable
fun CodeQuestTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Preserve brand identity by default
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

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
