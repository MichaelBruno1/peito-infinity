package com.example.peitoinfinity.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PeitoInfinityDarkColorScheme = darkColorScheme(
    primary = PastelBlue,
    onPrimary = TextOnPrimary,
    primaryContainer = PastelBlueContainer,
    onPrimaryContainer = PastelBlue,

    secondary = PastelPurple,
    onSecondary = TextOnPrimary,
    secondaryContainer = PastelPurpleContainer,
    onSecondaryContainer = PastelPurple,

    tertiary = PastelMint,
    onTertiary = TextOnPrimary,
    tertiaryContainer = PastelMintContainer,
    onTertiaryContainer = PastelMint,

    error = PastelRed,
    onError = TextOnPrimary,
    errorContainer = PastelRedContainer,
    onErrorContainer = PastelRed,

    background = DarkBackground,
    onBackground = TextPrimary,

    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,

    outline = Color(0xFF3A3F52),
    outlineVariant = Color(0xFF2A2E3E),
    inverseSurface = Color(0xFFE8EAF0),
    inverseOnSurface = DarkBackground,
    inversePrimary = PastelBlueDark,
    surfaceTint = PastelBlue
)

@Composable
fun PeitoInfinityTheme(
    content: @Composable () -> Unit
) {
    // Sempre dark - sem opção de tema claro e sem cores dinâmicas
    val colorScheme = PeitoInfinityDarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PeitoTypography,
        shapes = PeitoShapes,
        content = content
    )
}
