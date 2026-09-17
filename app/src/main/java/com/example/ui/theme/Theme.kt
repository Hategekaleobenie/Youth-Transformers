package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldLight,
    onPrimary = Color(0xFF003822),
    primaryContainer = DeepForestGreen,
    onPrimaryContainer = Color(0xFFA5F2CA),
    secondary = GoldLight,
    onSecondary = Color(0xFF412D00),
    secondaryContainer = Color(0xFF5E4200),
    onSecondaryContainer = Color(0xFFFFDEA1),
    tertiary = GoldLight,
    background = DarkBackground,
    onBackground = Color(0xFFE1E3DF),
    surface = DarkSurface,
    onSurface = Color(0xFFE1E3DF),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC1C9C1),
    outline = Color(0xFF8B938B)
)

private val LightColorScheme = lightColorScheme(
    primary = DeepForestGreen,
    onPrimary = Color.White,
    primaryContainer = SageContainer,
    onPrimaryContainer = OnSageContainer,
    secondary = DarkCharcoal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE7ECE8),
    onSecondaryContainer = Color(0xFF131A16),
    tertiary = SubtleGold,
    onTertiary = Color.White,
    tertiaryContainer = GoldContainer,
    onTertiaryContainer = OnGoldContainer,
    background = SoftBackground,
    onBackground = Color(0xFF191C1A),
    surface = CleanWhite,
    onSurface = Color(0xFF191C1A),
    surfaceVariant = Color(0xFFEEF3EE),
    onSurfaceVariant = CoolGrey,
    outline = BorderSubtle
)

@Composable
fun YouthTransformersTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We intentionally enforce our distinct calm Christian identity (Deep Green, White, Charcoal, Gold)
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) = YouthTransformersTheme(darkTheme = darkTheme, content = content)
