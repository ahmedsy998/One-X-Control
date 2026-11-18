package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.ControlCenterConfig
import com.example.data.ResolvedVisualPalette

@Composable
fun FloatingControlButton(
    config: ControlCenterConfig,
    palette: ResolvedVisualPalette,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sizeDp = config.floatingButtonSizeDp.coerceIn(48, 72).dp
    val iconDp = (config.floatingButtonSizeDp * 0.44f).coerceIn(20f, 32f).dp
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(sizeDp)
            .alpha(config.floatingButtonAlpha.coerceIn(0.3f, 1f))
            .drawBehind {
                // Soft ambient electric glow ring matching the reference floating trigger
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            palette.accent.copy(alpha = 0.45f * config.shadowIntensity.coerceAtLeast(0.35f)),
                            palette.accentSecondary.copy(alpha = 0.14f),
                            Color.Transparent
                        ),
                        radius = size.minDimension * 0.80f
                    ),
                    radius = size.minDimension * 0.80f
                )
            }
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        palette.panelSurface.copy(alpha = 0.86f),
                        palette.cardSurface.copy(alpha = 0.78f)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        palette.accentSecondary.copy(alpha = 0.92f),
                        palette.accent.copy(alpha = 0.75f),
                        Color.White.copy(alpha = 0.35f)
                    )
                ),
                shape = CircleShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = palette.accentSecondary),
                onClick = onClick
            )
            .testTag("floating_control_button"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = floatingButtonIcon(config.floatingButtonIconIndex),
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(iconDp)
        )
    }
}
