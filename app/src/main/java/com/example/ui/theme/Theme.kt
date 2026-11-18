package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.ControlCenterConfig
import com.example.data.LocalizationProvider
import com.example.data.ResolvedVisualPalette
import com.example.data.UiStrings

val LocalOneXPalette = staticCompositionLocalOf {
    ResolvedVisualPalette.fromConfig(ControlCenterConfig())
}

val LocalOneXStrings = staticCompositionLocalOf {
    LocalizationProvider.resolve(ControlCenterConfig().language)
}

@Composable
fun OneXControlTheme(
    config: ControlCenterConfig = ControlCenterConfig(),
    content: @Composable () -> Unit
) {
    val palette = ResolvedVisualPalette.fromConfig(config)
    val strings = LocalizationProvider.resolve(config.language)
    val layoutDirection = if (strings.isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    val colorScheme = darkColorScheme(
        primary = palette.accent,
        onPrimary = Color.White,
        primaryContainer = palette.activeTile.copy(alpha = 0.30f),
        onPrimaryContainer = palette.textPrimary,
        secondary = palette.accentSecondary,
        onSecondary = Color(0xFF041024),
        secondaryContainer = palette.cardSurface,
        onSecondaryContainer = palette.textPrimary,
        background = palette.bgTop,
        onBackground = palette.textPrimary,
        surface = palette.panelSurface,
        onSurface = palette.textPrimary,
        surfaceVariant = palette.cardSurface,
        onSurfaceVariant = palette.textSecondary,
        outline = palette.accent.copy(alpha = 0.32f),
        outlineVariant = Color.White.copy(alpha = 0.12f)
    )

    CompositionLocalProvider(
        LocalOneXPalette provides palette,
        LocalOneXStrings provides strings,
        LocalLayoutDirection provides layoutDirection
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = OneXTypography,
            content = content
        )
    }
}
