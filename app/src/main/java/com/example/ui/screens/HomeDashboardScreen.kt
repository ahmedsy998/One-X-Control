package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.FlashlightOn
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.KeyboardDoubleArrowDown
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ControlCenterConfig
import com.example.data.ResolvedVisualPalette
import com.example.data.UiStrings
import com.example.system.DeviceSystemState
import com.example.system.PermissionStatus
import com.example.ui.components.GlassSectionCard
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

@Composable
fun HomeDashboardScreen(
    config: ControlCenterConfig,
    palette: ResolvedVisualPalette,
    strings: UiStrings,
    systemState: DeviceSystemState,
    permissions: PermissionStatus,
    isOverlayRunning: Boolean,
    onOpenControlCenter: () -> Unit,
    onToggleOverlay: (Boolean) -> Unit,
    onOpenStudio: () -> Unit,
    onOpenTiles: () -> Unit,
    onOpenThemes: () -> Unit,
    onOpenPermissions: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp),
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding() + 12.dp,
                bottom = contentPadding.calculateBottomPadding() + 28.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Brand Hero Card with direct Top-Edge Pull-Down trigger
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    palette.accent.copy(alpha = 0.42f),
                                    palette.cardSurface.copy(alpha = 0.88f),
                                    palette.bgBottom.copy(alpha = 0.92f)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    palette.accentSecondary.copy(alpha = 0.65f),
                                    Color.White.copy(alpha = 0.10f)
                                )
                            ),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.controlCenterHeader,
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        fontFamily = OutfitFontFamily,
                                        fontSize = 24.sp,
                                        letterSpacing = 1.2.sp
                                    ),
                                    color = palette.textPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = strings.subtitleTagline,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = palette.textSecondary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(palette.accent.copy(alpha = 0.25f))
                                    .border(1.5.dp, palette.accentSecondary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.GridView,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        Button(
                            onClick = onOpenControlCenter,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp)
                                .testTag("home_open_control_center_btn"),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = palette.accent,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.KeyboardDoubleArrowDown,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = strings.pullDownControlCenter,
                                style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp)
                            )
                        }

                        Text(
                            text = strings.dragTipBanner,
                            style = MaterialTheme.typography.labelMedium,
                            color = palette.accentSecondary.copy(alpha = 0.92f)
                        )
                    }
                }
            }

            // Floating Control Button Overlay Service Card
            item {
                GlassSectionCard(palette = palette) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isOverlayRunning -> SuccessGreen
                                                !permissions.overlayGranted -> WarningAmber
                                                else -> palette.textSecondary.copy(alpha = 0.5f)
                                            }
                                        )
                                )
                                Text(
                                    text = strings.floatingOverlayTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = palette.textPrimary
                                )
                            }
                            Text(
                                text = when {
                                    !permissions.overlayGranted -> strings.floatingOverlayNeedsPerm
                                    isOverlayRunning -> strings.floatingOverlayRunning
                                    else -> strings.floatingOverlayStopped
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.textSecondary
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (!permissions.overlayGranted) {
                            Button(
                                onClick = { onToggleOverlay(true) },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("home_grant_overlay_btn"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = palette.accent)
                            ) {
                                Text(strings.grantPermissionBtn, color = Color.White)
                            }
                        } else if (!isOverlayRunning) {
                            Button(
                                onClick = { onToggleOverlay(true) },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("home_enable_overlay_btn"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = palette.accent)
                            ) {
                                Text(strings.enableOverlayBtn, color = Color.White)
                            }
                        } else {
                            OutlinedButton(
                                onClick = { onToggleOverlay(false) },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("home_disable_overlay_btn"),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(strings.disableOverlayBtn, color = palette.textPrimary)
                            }
                        }
                    }
                }
            }

            // Live Device Telemetry Card (Real Hardware Reads)
            item {
                GlassSectionCard(palette = palette) {
                    Text(
                        text = strings.deviceStatusTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.textPrimary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TelemetryChip(
                            icon = Icons.Rounded.Wifi,
                            label = strings.wifiLabel,
                            value = if (systemState.wifiConnected) strings.connected else strings.disconnected,
                            active = systemState.wifiConnected || systemState.isWifiEnabled,
                            palette = palette,
                            modifier = Modifier.weight(1f)
                        )
                        TelemetryChip(
                            icon = Icons.Rounded.Bluetooth,
                            label = strings.bluetoothLabel,
                            value = if (systemState.isBluetoothEnabled) strings.turnedOn else strings.turnedOff,
                            active = systemState.isBluetoothEnabled,
                            palette = palette,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TelemetryChip(
                            icon = Icons.Rounded.FlashlightOn,
                            label = strings.tileTitle(com.example.data.QuickTileId.FLASHLIGHT),
                            value = if (systemState.isFlashlightOn) strings.turnedOn else strings.turnedOff,
                            active = systemState.isFlashlightOn,
                            palette = palette,
                            modifier = Modifier.weight(1f)
                        )
                        TelemetryChip(
                            icon = Icons.Rounded.Headphones,
                            label = strings.audioOutput,
                            value = systemState.audioOutputLabel,
                            active = systemState.connectedAudioDeviceName != null,
                            palette = palette,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Quick Customization 2x2 Grid
            item {
                Text(
                    text = strings.quickLaunchSection,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionTileCard(
                            icon = Icons.Rounded.Tune,
                            title = strings.quickCardStudio,
                            subtitle = strings.quickCardStudioSub,
                            palette = palette,
                            onClick = onOpenStudio,
                            testTag = "home_card_studio",
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionTileCard(
                            icon = Icons.Rounded.GridView,
                            title = strings.quickCardTiles,
                            subtitle = strings.quickCardTilesSub,
                            palette = palette,
                            onClick = onOpenTiles,
                            testTag = "home_card_tiles",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionTileCard(
                            icon = Icons.Rounded.Palette,
                            title = strings.quickCardThemes,
                            subtitle = strings.quickCardThemesSub,
                            palette = palette,
                            onClick = onOpenThemes,
                            testTag = "home_card_themes",
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionTileCard(
                            icon = Icons.Rounded.Security,
                            title = strings.quickCardPermissions,
                            subtitle = strings.quickCardPermissionsSub,
                            palette = palette,
                            onClick = onOpenPermissions,
                            testTag = "home_card_permissions",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Honest Non-Root Compatibility Banner
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(palette.panelSurface.copy(alpha = 0.72f))
                        .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(18.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.VerifiedUser,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${strings.nonRootNotice} (${systemState.deviceModel} • ${systemState.androidVersion})",
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun TelemetryChip(
    icon: ImageVector,
    label: String,
    value: String,
    active: Boolean,
    palette: ResolvedVisualPalette,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (active) palette.activeTile.copy(alpha = 0.24f)
                else palette.inactiveTile.copy(alpha = 0.50f)
            )
            .border(
                width = 1.dp,
                color = if (active) palette.accentSecondary.copy(alpha = 0.45f)
                else Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (active) palette.accentSecondary else palette.textSecondary,
            modifier = Modifier.size(18.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun QuickActionTileCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    palette: ResolvedVisualPalette,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(palette.cardSurface.copy(alpha = 0.72f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag(testTag),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(palette.accent.copy(alpha = 0.20f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = palette.accentSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = palette.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = palette.textSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
