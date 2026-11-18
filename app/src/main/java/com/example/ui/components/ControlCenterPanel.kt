package com.example.ui.components

import android.view.KeyEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DevicesOther
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ControlCenterConfig
import com.example.data.QuickTileId
import com.example.data.ResolvedVisualPalette
import com.example.data.SliderPosition
import com.example.data.SliderStyle
import com.example.data.TileAlignment
import com.example.data.UiStrings
import com.example.system.DeviceSystemState
import com.example.ui.theme.IceSliderEnd
import com.example.ui.theme.IceSliderStart
import com.example.ui.theme.OutfitFontFamily
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * Full-screen scrim + TOP-ANCHORED Control Center that slides smoothly down from the TOP EDGE.
 * Never rendered as a centered dialog or bottom sheet.
 */
@Composable
fun TopAnchoredControlCenterOverlay(
    visible: Boolean,
    expanded: Boolean,
    config: ControlCenterConfig,
    palette: ResolvedVisualPalette,
    strings: UiStrings,
    systemState: DeviceSystemState,
    onExpandedChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onTileClick: (QuickTileId) -> Unit,
    onBrightnessChange: (Float) -> Unit,
    onBrightnessChangeFinished: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onMediaKey: (Int) -> Unit,
    onOpenMusicApp: () -> Unit,
    onOpenVolumePanel: () -> Unit,
    onOpenDisplaySettings: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    onOpenDeviceSettings: () -> Unit,
    onOpenAppSettings: () -> Unit,
    onOpenCustomize: () -> Unit,
    modifier: Modifier = Modifier
) {
    var verticalDragOffset by remember { mutableFloatStateOf(0f) }

    Box(modifier = modifier.fillMaxSize()) {
        // Dimmed / frosted backdrop scrim
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(config.openSpeedMs)),
            exit = fadeOut(animationSpec = tween(config.closeSpeedMs))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color(0xFF030712).copy(
                            alpha = (0.48f + config.blurIntensity * 0.34f).coerceIn(0.35f, 0.85f)
                        )
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { onDismiss() })
                    }
                    .testTag("control_center_scrim")
            )
        }

        // Top-anchored sliding shade
        AnimatedVisibility(
            visible = visible,
            modifier = Modifier.align(Alignment.TopCenter),
            enter = slideInVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = when {
                        config.openSpeedMs <= 220 -> Spring.StiffnessMedium
                        config.openSpeedMs >= 420 -> Spring.StiffnessVeryLow
                        else -> Spring.StiffnessLow
                    }
                ),
                initialOffsetY = { fullHeight -> -fullHeight }
            ) + fadeIn(animationSpec = tween(config.openSpeedMs / 2)),
            exit = slideOutVertically(
                animationSpec = tween(durationMillis = config.closeSpeedMs),
                targetOffsetY = { fullHeight -> -fullHeight }
            ) + fadeOut(animationSpec = tween(config.closeSpeedMs))
        ) {
            val statusTopPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .pointerInput(expanded) {
                        detectVerticalDragGestures(
                            onDragEnd = {
                                when {
                                    verticalDragOffset > 65f && !expanded -> onExpandedChange(true)
                                    verticalDragOffset < -65f && expanded -> onExpandedChange(false)
                                    verticalDragOffset < -75f && !expanded -> onDismiss()
                                }
                                verticalDragOffset = 0f
                            },
                            onDragCancel = { verticalDragOffset = 0f },
                            onVerticalDrag = { _, dragAmount ->
                                verticalDragOffset += dragAmount
                            }
                        )
                    }
            ) {
                ControlCenterCardSurface(
                    expanded = expanded,
                    config = config,
                    palette = palette,
                    strings = strings,
                    systemState = systemState,
                    topInsetPadding = statusTopPadding + 8.dp,
                    isPreview = false,
                    onExpandedChange = onExpandedChange,
                    onTileClick = onTileClick,
                    onBrightnessChange = onBrightnessChange,
                    onBrightnessChangeFinished = onBrightnessChangeFinished,
                    onVolumeChange = onVolumeChange,
                    onMediaKey = onMediaKey,
                    onOpenMusicApp = onOpenMusicApp,
                    onOpenVolumePanel = onOpenVolumePanel,
                    onOpenDisplaySettings = onOpenDisplaySettings,
                    onOpenBatterySettings = onOpenBatterySettings,
                    onOpenDeviceSettings = onOpenDeviceSettings,
                    onOpenAppSettings = onOpenAppSettings,
                    onOpenCustomize = onOpenCustomize
                )
            }
        }
    }
}

/**
 * Reusable One X Control glassmorphic panel body used in:
 * 1) Real System Overlay Window
 * 2) In-App Top-Edge Shade
 * 3) Live Interactive Studio Preview
 */
@Composable
fun ControlCenterCardSurface(
    expanded: Boolean,
    config: ControlCenterConfig,
    palette: ResolvedVisualPalette,
    strings: UiStrings,
    systemState: DeviceSystemState,
    topInsetPadding: Dp = 12.dp,
    isPreview: Boolean = false,
    onExpandedChange: (Boolean) -> Unit,
    onTileClick: (QuickTileId) -> Unit,
    onBrightnessChange: (Float) -> Unit,
    onBrightnessChangeFinished: () -> Unit = {},
    onVolumeChange: (Float) -> Unit,
    onMediaKey: (Int) -> Unit = {},
    onOpenMusicApp: () -> Unit = {},
    onOpenVolumePanel: () -> Unit = {},
    onOpenDisplaySettings: () -> Unit = {},
    onOpenBatterySettings: () -> Unit = {},
    onOpenDeviceSettings: () -> Unit = {},
    onOpenAppSettings: () -> Unit = {},
    onOpenCustomize: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchOpen by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showPowerDialog by remember { mutableStateOf(false) }

    val panelCorner = config.panelCornerRadiusDp.dp
    val panelShape = RoundedCornerShape(
        topStart = if (isPreview) panelCorner else 0.dp,
        topEnd = if (isPreview) panelCorner else 0.dp,
        bottomStart = panelCorner,
        bottomEnd = panelCorner
    )

    val filteredTiles = remember(config.activeTileIds, config.hideUnavailableTiles, systemState.availability, searchQuery) {
        config.activeTileIds
            .filter { tile ->
                val availOk = !config.hideUnavailableTiles || systemState.isTileAvailable(tile)
                val queryOk = searchQuery.isBlank() ||
                    strings.tileTitle(tile).contains(searchQuery, ignoreCase = true) ||
                    tile.id.contains(searchQuery, ignoreCase = true)
                availOk && queryOk
            }
    }

    val compactTiles = remember(filteredTiles, config.favoriteTileIds, config.initialVisibleTiles) {
        val sorted = filteredTiles.sortedByDescending { config.favoriteTileIds.contains(it) }
        sorted.take(config.initialVisibleTiles)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(panelShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        palette.bgTop.copy(alpha = config.backgroundAlpha),
                        palette.panelSurface.copy(alpha = (config.backgroundAlpha * 0.96f).coerceIn(0.35f, 1f)),
                        palette.bgBottom.copy(alpha = (config.backgroundAlpha * 0.92f).coerceIn(0.35f, 1f))
                    )
                )
            )
            .drawBehind {
                // Subtle ambient purple/blue top-edge glow like the Samsung/HyperOS reference
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            palette.accent.copy(alpha = 0.22f * config.glassIntensity),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.82f, size.height * 0.12f),
                        radius = size.width * 0.65f
                    ),
                    radius = size.width * 0.65f,
                    center = Offset(size.width * 0.82f, size.height * 0.12f)
                )
            }
            .border(
                width = (0.8f + config.glassIntensity * 0.9f).dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        palette.accentSecondary.copy(alpha = 0.12f + config.glassIntensity * 0.22f),
                        Color.White.copy(alpha = 0.08f + config.glassIntensity * 0.16f)
                    )
                ),
                shape = panelShape
            )
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
            .testTag("control_center_panel")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (!isPreview) Modifier.verticalScroll(rememberScrollState()) else Modifier
                )
                .padding(
                    top = topInsetPadding,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 10.dp
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Header Row: ONE X CONTROL + Search / Settings / Power / Edit
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = strings.controlCenterHeader,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.2.sp,
                        fontSize = if (isPreview) 15.sp else 18.sp
                    ),
                    color = palette.textPrimary
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HeaderIconAction(
                        icon = if (searchOpen) Icons.Rounded.Close else Icons.Rounded.Search,
                        contentDescription = strings.searchTilesPlaceholder,
                        tint = palette.textPrimary,
                        onClick = {
                            searchOpen = !searchOpen
                            if (!searchOpen) searchQuery = ""
                        },
                        tag = "cc_header_search"
                    )
                    HeaderIconAction(
                        icon = Icons.Rounded.Settings,
                        contentDescription = strings.settingsHubTitle,
                        tint = palette.textPrimary,
                        onClick = onOpenAppSettings,
                        tag = "cc_header_settings"
                    )
                    HeaderIconAction(
                        icon = Icons.Rounded.PowerSettingsNew,
                        contentDescription = strings.powerMenuTitle,
                        tint = palette.textPrimary,
                        onClick = { showPowerDialog = true },
                        tag = "cc_header_power"
                    )
                    HeaderIconAction(
                        icon = Icons.Rounded.Edit,
                        contentDescription = strings.navStudio,
                        tint = palette.textPrimary,
                        onClick = onOpenCustomize,
                        tag = "cc_header_edit"
                    )
                }
            }

            // Optional Quick Filter Search Bar
            AnimatedVisibility(visible = searchOpen) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(palette.cardSurface.copy(alpha = 0.85f))
                        .border(1.dp, palette.accent.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = strings.searchTilesPlaceholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.textSecondary.copy(alpha = 0.7f)
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = palette.textPrimary),
                        cursorBrush = SolidColor(palette.accent),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 2. Two Large Connectivity Pill Cards (Wi-Fi & Bluetooth — One UI Signature)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LargeConnectivityPill(
                    title = strings.wifiLabel,
                    subtitle = when {
                        systemState.wifiSsid != null -> systemState.wifiSsid
                        systemState.wifiConnected -> strings.connected
                        systemState.isWifiEnabled -> strings.turnedOn
                        else -> strings.disconnected
                    },
                    icon = Icons.Rounded.Wifi,
                    isActive = systemState.isWifiEnabled || systemState.wifiConnected,
                    palette = palette,
                    config = config,
                    onClick = { onTileClick(QuickTileId.WIFI) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("cc_tile_wifi_large")
                )
                LargeConnectivityPill(
                    title = strings.bluetoothLabel,
                    subtitle = when {
                        systemState.connectedAudioDeviceName != null -> systemState.connectedAudioDeviceName
                        systemState.isBluetoothEnabled -> strings.turnedOn
                        else -> strings.turnedOff
                    },
                    icon = Icons.Rounded.Bluetooth,
                    isActive = systemState.isBluetoothEnabled,
                    palette = palette,
                    config = config,
                    onClick = { onTileClick(QuickTileId.BLUETOOTH) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("cc_tile_bluetooth_large")
                )
            }

            // Optional Top-positioned Sliders
            if (config.sliderPosition == SliderPosition.TOP) {
                SlidersSection(
                    config = config,
                    palette = palette,
                    strings = strings,
                    systemState = systemState,
                    expanded = expanded,
                    onBrightnessChange = onBrightnessChange,
                    onBrightnessChangeFinished = onBrightnessChangeFinished,
                    onVolumeChange = onVolumeChange,
                    onOpenDisplaySettings = onOpenDisplaySettings,
                    onOpenVolumePanel = onOpenVolumePanel
                )
            }

            // Media Player at TOP position (compact mode or expanded when configured)
            if (config.showMediaPlayer && (!expanded || config.mediaPlayerPosition == SliderPosition.TOP)) {
                MediaPlayerGlassCard(
                    palette = palette,
                    strings = strings,
                    systemState = systemState,
                    glassIntensity = config.glassIntensity,
                    onPrev = { onMediaKey(KeyEvent.KEYCODE_MEDIA_PREVIOUS) },
                    onPlayPause = { onMediaKey(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) },
                    onNext = { onMediaKey(KeyEvent.KEYCODE_MEDIA_NEXT) },
                    onOutputClick = onOpenVolumePanel,
                    onCardClick = onOpenMusicApp
                )
            }

            // Compact Quick Tiles Strip OR Expanded Full Multi-Page Grid
            if (!expanded) {
                if (compactTiles.isNotEmpty()) {
                    CompactTilesGlassCard(
                        tiles = compactTiles,
                        config = config,
                        palette = palette,
                        strings = strings,
                        systemState = systemState,
                        onTileClick = onTileClick
                    )
                }
            } else {
                ExpandedTilesPagerCard(
                    tiles = filteredTiles,
                    config = config,
                    palette = palette,
                    strings = strings,
                    systemState = systemState,
                    onTileClick = onTileClick
                )
            }

            // Middle-positioned Media Player in Expanded mode
            if (expanded && config.showMediaPlayer && config.mediaPlayerPosition == SliderPosition.MIDDLE) {
                MediaPlayerGlassCard(
                    palette = palette,
                    strings = strings,
                    systemState = systemState,
                    glassIntensity = config.glassIntensity,
                    onPrev = { onMediaKey(KeyEvent.KEYCODE_MEDIA_PREVIOUS) },
                    onPlayPause = { onMediaKey(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) },
                    onNext = { onMediaKey(KeyEvent.KEYCODE_MEDIA_NEXT) },
                    onOutputClick = onOpenVolumePanel,
                    onCardClick = onOpenMusicApp
                )
            }

            // Bottom or Middle Sliders
            if (config.sliderPosition != SliderPosition.TOP) {
                SlidersSection(
                    config = config,
                    palette = palette,
                    strings = strings,
                    systemState = systemState,
                    expanded = expanded,
                    onBrightnessChange = onBrightnessChange,
                    onBrightnessChangeFinished = onBrightnessChangeFinished,
                    onVolumeChange = onVolumeChange,
                    onOpenDisplaySettings = onOpenDisplaySettings,
                    onOpenVolumePanel = onOpenVolumePanel
                )
            }

            // Bottom-positioned Media Player in Expanded mode
            if (expanded && config.showMediaPlayer && config.mediaPlayerPosition == SliderPosition.BOTTOM) {
                MediaPlayerGlassCard(
                    palette = palette,
                    strings = strings,
                    systemState = systemState,
                    glassIntensity = config.glassIntensity,
                    onPrev = { onMediaKey(KeyEvent.KEYCODE_MEDIA_PREVIOUS) },
                    onPlayPause = { onMediaKey(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) },
                    onNext = { onMediaKey(KeyEvent.KEYCODE_MEDIA_NEXT) },
                    onOutputClick = onOpenVolumePanel,
                    onCardClick = onOpenMusicApp
                )
            }

            // HyperOS-style Connected Devices & Smart Devices Footer Cards (in Expanded mode)
            if (expanded && config.showConnectedDevicesRow) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FooterDevicePillCard(
                        title = strings.connectedDevicesCard,
                        subtitle = systemState.connectedAudioDeviceName ?: strings.noConnectedDevice,
                        icon = Icons.Rounded.Headphones,
                        palette = palette,
                        glassIntensity = config.glassIntensity,
                        onClick = { onTileClick(QuickTileId.CONNECTED_DEVICES) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("cc_connected_devices_card")
                    )
                    FooterDevicePillCard(
                        title = strings.smartDevicesCard,
                        subtitle = strings.manageSmartDevices,
                        icon = Icons.Rounded.DevicesOther,
                        palette = palette,
                        glassIntensity = config.glassIntensity,
                        onClick = { onTileClick(QuickTileId.SMART_DEVICES) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("cc_smart_devices_card")
                    )
                }
            }

            // Bottom Expand / Collapse Chevron Handle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 34.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onExpandedChange(!expanded) }
                    .testTag("cc_expand_collapse_handle"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = if (expanded) strings.dragToCollapse else strings.dragToExpand,
                    tint = palette.textSecondary.copy(alpha = 0.85f),
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }

    if (showPowerDialog) {
        AlertDialog(
            onDismissRequest = { showPowerDialog = false },
            containerColor = palette.panelSurface,
            titleContentColor = palette.textPrimary,
            textContentColor = palette.textSecondary,
            title = { Text(strings.powerMenuTitle, style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(strings.powerMenuDesc, style = MaterialTheme.typography.bodyMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(onClick = {
                            showPowerDialog = false
                            onOpenBatterySettings()
                        }) {
                            Text(strings.openBatterySettings, color = palette.accentSecondary)
                        }
                        TextButton(onClick = {
                            showPowerDialog = false
                            onOpenDeviceSettings()
                        }) {
                            Text(strings.openDeviceSettings, color = palette.accentSecondary)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPowerDialog = false }) {
                    Text(strings.closeLabel, color = palette.accent)
                }
            }
        )
    }
}

@Composable
private fun HeaderIconAction(
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    onClick: () -> Unit,
    tag: String
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .testTag(tag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(21.dp)
        )
    }
}

@Composable
private fun LargeConnectivityPill(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isActive: Boolean,
    palette: ResolvedVisualPalette,
    config: ControlCenterConfig,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val circleColor by animateColorAsState(
        targetValue = if (isActive) palette.activeTile else palette.inactiveTile,
        label = "large_tile_circle"
    )
    val cardBorderColor = if (isActive) {
        palette.accentSecondary.copy(alpha = 0.45f)
    } else {
        Color.White.copy(alpha = 0.10f + config.glassIntensity * 0.12f)
    }

    Row(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (isActive) {
                        listOf(
                            palette.cardSurface.copy(alpha = 0.88f),
                            palette.activeTile.copy(alpha = 0.22f)
                        )
                    } else {
                        listOf(
                            palette.cardSurface.copy(alpha = 0.72f),
                            palette.cardSurface.copy(alpha = 0.60f)
                        )
                    }
                )
            )
            .border(1.dp, cardBorderColor, RoundedCornerShape(28.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .drawBehind {
                    if (isActive && config.shadowIntensity > 0.05f) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    palette.accent.copy(alpha = 0.55f * config.shadowIntensity),
                                    Color.Transparent
                                ),
                                radius = size.minDimension * 0.85f
                            ),
                            radius = size.minDimension * 0.85f
                        )
                    }
                }
                .clip(CircleShape)
                .background(circleColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = if (isActive) palette.accentSecondary else palette.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MediaPlayerGlassCard(
    palette: ResolvedVisualPalette,
    strings: UiStrings,
    systemState: DeviceSystemState,
    glassIntensity: Float,
    onPrev: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onOutputClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        palette.cardSurface.copy(alpha = 0.82f),
                        palette.panelSurface.copy(alpha = 0.72f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.12f + glassIntensity * 0.14f),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onCardClick)
            .padding(12.dp)
            .testTag("cc_media_player_card"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Stylized waveform / artwork badge (honest, never fake album cover)
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            palette.accent.copy(alpha = 0.90f),
                            Color(0xFF4F46E5),
                            palette.bgBottom
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.MusicNote,
                contentDescription = strings.mediaPlaying,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Headphones,
                    contentDescription = null,
                    tint = palette.accentSecondary,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = systemState.audioOutputLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.accentSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = if (systemState.isMediaPlaying) strings.mediaPlaying else strings.mediaIdle,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = strings.mediaOpenPlayer,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            IconButton(
                onClick = onOutputClick,
                modifier = Modifier
                    .size(28.dp)
                    .testTag("cc_media_output_btn")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Cast,
                    contentDescription = strings.audioOutput,
                    tint = palette.textSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onPrev,
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("cc_media_prev")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipPrevious,
                        contentDescription = "Previous",
                        tint = palette.textPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                IconButton(
                    onClick = onPlayPause,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("cc_media_play_pause")
                ) {
                    Icon(
                        imageVector = if (systemState.isMediaPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = "Play Pause",
                        tint = palette.textPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(
                    onClick = onNext,
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("cc_media_next")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipNext,
                        contentDescription = "Next",
                        tint = palette.textPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactTilesGlassCard(
    tiles: List<QuickTileId>,
    config: ControlCenterConfig,
    palette: ResolvedVisualPalette,
    strings: UiStrings,
    systemState: DeviceSystemState,
    onTileClick: (QuickTileId) -> Unit
) {
    val cols = config.gridColumns.coerceIn(3, 6)
    val rows = tiles.chunked(cols)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(palette.cardSurface.copy(alpha = 0.62f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.10f + config.glassIntensity * 0.14f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(config.tileSpacingDp.dp)
    ) {
        rows.forEach { rowTiles ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowTiles.forEach { tile ->
                    QuickTileItem(
                        tile = tile,
                        isActive = systemState.isTileActive(tile),
                        isAvailable = systemState.isTileAvailable(tile),
                        config = config,
                        palette = palette,
                        strings = strings,
                        onClick = { onTileClick(tile) },
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(cols - rowTiles.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ExpandedTilesPagerCard(
    tiles: List<QuickTileId>,
    config: ControlCenterConfig,
    palette: ResolvedVisualPalette,
    strings: UiStrings,
    systemState: DeviceSystemState,
    onTileClick: (QuickTileId) -> Unit
) {
    val cols = config.gridColumns.coerceIn(3, 6)
    val rowsPerPage = 4
    val pageSize = cols * rowsPerPage
    val pageCount = ceil(tiles.size.coerceAtLeast(1) / pageSize.toFloat()).toInt().coerceAtLeast(1)
    val pagerState = rememberPagerState(pageCount = { pageCount })

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(palette.cardSurface.copy(alpha = 0.64f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.11f + config.glassIntensity * 0.15f),
                shape = RoundedCornerShape(26.dp)
            )
            .padding(top = 14.dp, bottom = 10.dp, start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val start = page * pageSize
            val pageTiles = tiles.drop(start).take(pageSize)
            val rows = pageTiles.chunked(cols)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(config.tileSpacingDp.dp)
            ) {
                rows.forEach { rowTiles ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowTiles.forEach { tile ->
                            QuickTileItem(
                                tile = tile,
                                isActive = systemState.isTileActive(tile),
                                isAvailable = systemState.isTileAvailable(tile),
                                config = config,
                                palette = palette,
                                strings = strings,
                                onClick = { onTileClick(tile) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(cols - rowTiles.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        if (pageCount > 1) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pageCount) { index ->
                    val active = pagerState.currentPage == index
                    val width by animateDpAsState(if (active) 16.dp else 6.dp, label = "pager_dot")
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(
                                if (active) palette.textPrimary else palette.textSecondary.copy(alpha = 0.4f)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickTileItem(
    tile: QuickTileId,
    isActive: Boolean,
    isAvailable: Boolean,
    config: ControlCenterConfig,
    palette: ResolvedVisualPalette,
    strings: UiStrings,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = when {
            !isAvailable -> palette.inactiveTile.copy(alpha = 0.35f)
            isActive -> palette.activeTile
            else -> palette.inactiveTile.copy(alpha = 0.85f)
        },
        label = "quick_tile_bg"
    )
    val tileSize = config.tileSizeDp.coerceIn(48, 70).dp
    val cornerRadius = config.tileCornerRadiusDp.coerceAtMost(config.tileSizeDp / 2).dp
    val iconSize = config.iconSize.iconDp.dp
    val alignment = if (config.iconAlignment == TileAlignment.START) Alignment.Start else Alignment.CenterHorizontally

    Column(
        modifier = modifier
            .padding(horizontal = 2.dp)
            .alpha(if (isAvailable) 1f else 0.48f)
            .testTag("cc_tile_${tile.id}"),
        horizontalAlignment = alignment,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(tileSize)
                .drawBehind {
                    if (isActive && isAvailable && config.shadowIntensity > 0.05f) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    palette.accent.copy(alpha = 0.50f * config.shadowIntensity),
                                    Color.Transparent
                                ),
                                radius = size.minDimension * 0.78f
                            ),
                            radius = size.minDimension * 0.78f
                        )
                    }
                }
                .clip(RoundedCornerShape(cornerRadius))
                .background(bgColor)
                .border(
                    width = if (isActive) 1.dp else 0.7.dp,
                    color = if (isActive) palette.accentSecondary.copy(alpha = 0.6f)
                    else Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(cornerRadius)
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = quickTileIcon(tile),
                contentDescription = strings.tileTitle(tile),
                tint = if (isActive) Color.White else palette.iconColor.copy(alpha = 0.92f),
                modifier = Modifier.size(iconSize)
            )
        }

        if (config.showTileLabels) {
            Text(
                text = if (isAvailable) strings.tileTitle(tile) else "${strings.tileTitle(tile)}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = (10.5f * config.labelScale).sp,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (isActive) palette.textPrimary else palette.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = if (config.iconAlignment == TileAlignment.START) TextAlign.Start else TextAlign.Center
            )
        }
    }
}

@Composable
private fun SlidersSection(
    config: ControlCenterConfig,
    palette: ResolvedVisualPalette,
    strings: UiStrings,
    systemState: DeviceSystemState,
    expanded: Boolean,
    onBrightnessChange: (Float) -> Unit,
    onBrightnessChangeFinished: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onOpenDisplaySettings: () -> Unit,
    onOpenVolumePanel: () -> Unit
) {
    if (!config.showBrightnessSlider && !config.showVolumeSlider) return

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (config.showBrightnessSlider) {
            OneUIPillSlider(
                value = systemState.brightness,
                icon = Icons.Rounded.WbSunny,
                contentDescription = strings.brightnessLabel,
                heightDp = config.sliderThicknessDp.dp,
                showIcon = config.showSliderIcons,
                showOverflow = expanded,
                sliderStyle = config.sliderStyle,
                palette = palette,
                onValueChange = onBrightnessChange,
                onValueChangeFinished = onBrightnessChangeFinished,
                onOverflowClick = onOpenDisplaySettings,
                modifier = Modifier.testTag("cc_brightness_slider")
            )
        }
        if (config.showVolumeSlider) {
            OneUIPillSlider(
                value = systemState.mediaVolume,
                icon = Icons.AutoMirrored.Rounded.VolumeUp,
                contentDescription = strings.volumeLabel,
                heightDp = config.sliderThicknessDp.dp,
                showIcon = config.showSliderIcons,
                showOverflow = expanded,
                sliderStyle = config.sliderStyle,
                palette = palette,
                onValueChange = onVolumeChange,
                onValueChangeFinished = {},
                onOverflowClick = onOpenVolumePanel,
                modifier = Modifier.testTag("cc_volume_slider")
            )
        }
    }
}

@Composable
fun OneUIPillSlider(
    value: Float,
    icon: ImageVector,
    contentDescription: String,
    heightDp: Dp,
    showIcon: Boolean,
    showOverflow: Boolean,
    sliderStyle: SliderStyle,
    palette: ResolvedVisualPalette,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    onOverflowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var widthPx by remember { mutableFloatStateOf(1f) }
    val clamped = value.coerceIn(0f, 1f)
    val safeHeight = heightDp.coerceIn(30.dp, 56.dp)
    val shape = RoundedCornerShape(safeHeight / 2)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(safeHeight)
            .clip(shape)
            .background(palette.cardSurface.copy(alpha = 0.72f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.14f),
                shape = shape
            )
            .onSizeChanged { widthPx = it.width.toFloat().coerceAtLeast(1f) }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val fraction = (offset.x / widthPx).coerceIn(0.02f, 1f)
                    onValueChange(fraction)
                    onValueChangeFinished()
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = { onValueChangeFinished() },
                    onHorizontalDrag = { change, _ ->
                        change.consume()
                        val fraction = (change.position.x / widthPx).coerceIn(0.02f, 1f)
                        onValueChange(fraction)
                    }
                )
            }
    ) {
        // Fill Track Canvas matching the luminous One UI white-to-icy-blue pill slider
        Canvas(modifier = Modifier.fillMaxSize()) {
            val fillWidth = (size.width * clamped).coerceAtLeast(size.height * 0.9f).coerceAtMost(size.width)
            val fillBrush = when (sliderStyle) {
                SliderStyle.ONE_UI_GLOW -> Brush.horizontalGradient(
                    colors = listOf(
                        palette.accent.copy(alpha = 0.35f),
                        IceSliderStart,
                        IceSliderEnd
                    ),
                    startX = 0f,
                    endX = fillWidth
                )
                SliderStyle.FROSTED_GLASS -> Brush.horizontalGradient(
                    colors = listOf(
                        palette.accent.copy(alpha = 0.65f),
                        palette.accentSecondary.copy(alpha = 0.92f)
                    ),
                    startX = 0f,
                    endX = fillWidth
                )
                SliderStyle.MINIMAL_PILL -> Brush.horizontalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.86f),
                        Color.White
                    ),
                    startX = 0f,
                    endX = fillWidth
                )
            }
            drawRoundRect(
                brush = fillBrush,
                topLeft = Offset.Zero,
                size = Size(fillWidth, size.height),
                cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (showIcon) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = if (clamped > 0.16f && sliderStyle != SliderStyle.FROSTED_GLASS) {
                        Color(0xFF0E1B3E)
                    } else {
                        Color.White
                    },
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            if (showOverflow) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onOverflowClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = contentDescription,
                        tint = if (clamped > 0.9f) Color(0xFF0E1B3E) else palette.textPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FooterDevicePillCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    palette: ResolvedVisualPalette,
    glassIntensity: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(palette.cardSurface.copy(alpha = 0.72f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.10f + glassIntensity * 0.14f),
                shape = RoundedCornerShape(22.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = palette.textPrimary,
            modifier = Modifier.size(20.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                color = palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
