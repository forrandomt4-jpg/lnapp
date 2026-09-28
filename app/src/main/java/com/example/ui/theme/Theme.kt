package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CleanWhiteColorScheme = lightColorScheme(
    primary = SlatePrimary,
    onPrimary = Color.White,
    primaryContainer = SlatePrimaryContainer,
    onPrimaryContainer = OnSlatePrimaryContainer,
    secondary = SlateSecondary,
    onSecondary = Color.White,
    secondaryContainer = SlateSecondaryContainer,
    onSecondaryContainer = OnSlateSecondaryContainer,
    tertiary = AccentIndigo,
    onTertiary = Color.White,
    tertiaryContainer = AccentIndigoContainer,
    onTertiaryContainer = OnAccentIndigoContainer,
    background = PureWhite,
    onBackground = TextPrimary,
    surface = CardSurface,
    onSurface = TextPrimary,
    surfaceVariant = SlateCardBg,
    onSurfaceVariant = TextSecondary,
    outline = SlateBorder,
    outlineVariant = Color(0xFFCBD5E1)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Clean white background requested by user
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = CleanWhiteColorScheme,
        typography = Typography,
        content = content
    )
}
