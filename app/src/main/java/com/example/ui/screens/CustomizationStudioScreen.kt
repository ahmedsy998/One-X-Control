package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BlurOn
import androidx.compose.material.icons.rounded.Brightness6
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Label
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Opacity
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.RoundedCorner
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.ControlCenterConfig
import com.example.data.IconSizeOption
import com.example.data.QuickTileId
import com.example.data.ResolvedVisualPalette
import com.example.data.SliderPosition
import com.example.data.SliderStyle
import com.example.data.ThemePreset
import com.example.data.TileAlignment
import com.example.data.UiStrings
import com.example.system.DeviceSystemState
import com.example.ui.components.ControlCenterCardSurface
import com.example.ui.components.GlassSectionCard
import com.example.ui.components.OneXColorSwatchRow
import com.example.ui.components.OneXSegmentedPills
import com.example.ui.components.OneXSliderRow
import com.example.ui.components.OneXToggleRow
import com.example.ui.components.OneXTopHeader
import com.example.ui.components.formatPercent
import com.example.ui.theme.InactiveColorSwatches
import com.example.ui.theme.PresetColorSwatches
import com.example.viewmodel.ControlCenterViewModel
import kotlin.math.roundToInt

enum class StudioTab {
    LAYOUT,
    TILES,
    SLIDERS,
    MEDIA,
    ICONS,
    BACKGROUND,
    ANIMATIONS
}

@Composable
fun CustomizationStudioScreen(
    initialTab: StudioTab,
    showBackButton: Boolean,
    config: ControlCenterConfig,
    palette: ResolvedVisualPalette,
    strings: UiStrings,
    systemState: DeviceSystemState,
    viewModel: ControlCenterViewModel,
    onBack: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    var activeTab by remember(initialTab) { mutableStateOf(initialTab) }
    var previewExpanded by remember { mutableStateOf(false) }

    val screenTitle = when (activeTab) {
        StudioTab.LAYOUT -> strings.secLayout
        StudioTab.TILES -> strings.secTiles
        StudioTab.SLIDERS -> strings.secSliders
        StudioTab.MEDIA -> strings.secMedia
        StudioTab.ICONS -> strings.secIcons
        StudioTab.BACKGROUND -> strings.secBackground
        StudioTab.ANIMATIONS -> strings.secAnimations
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp),
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding() + 4.dp,
                bottom = contentPadding.calculateBottomPadding() + 32.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OneXTopHeader(
                    title = screenTitle,
                    backContentDescription = strings.backLabel,
                    palette = palette,
                    onBack = if (showBackButton) onBack else null
                )
            }

            // Studio Category Switcher Bar
            item {
                val tabs = listOf(
                    StudioTab.LAYOUT to strings.secLayout,
                    StudioTab.TILES to strings.secTiles,
                    StudioTab.SLIDERS to strings.secSliders,
                    StudioTab.MEDIA to strings.secMedia,
                    StudioTab.ICONS to strings.secIcons,
                    StudioTab.BACKGROUND to strings.secBackground,
                    StudioTab.ANIMATIONS to strings.secAnimations
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tabs.forEach { (tab, label) ->
                        val selected = tab == activeTab
                        Box(
                            modifier = Modifier
                                .heightIn(min = 40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (selected) palette.accent else palette.cardSurface.copy(alpha = 0.72f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (selected) palette.accentSecondary else Color.White.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable { activeTab = tab }
                                .padding(horizontal = 14.dp, vertical = 9.dp)
                                .testTag("studio_tab_${tab.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (selected) Color.White else palette.textSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Real-Time Interactive Live Preview Card (NOT a static screenshot!)
            item {
                GlassSectionCard(palette = palette) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = strings.livePreview,
                            style = MaterialTheme.typography.titleMedium,
                            color = palette.textPrimary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            PreviewModePill(
                                label = strings.compactMode,
                                selected = !previewExpanded,
                                palette = palette,
                                onClick = { previewExpanded = false },
                                testTag = "preview_mode_compact"
                            )
                            PreviewModePill(
                                label = strings.expandedMode,
                                selected = previewExpanded,
                                palette = palette,
                                onClick = { previewExpanded = true },
                                testTag = "preview_mode_expanded"
                            )
                        }
                    }

                    // Live interactive ControlCenterCardSurface responding immediately to every customization!
                    ControlCenterCardSurface(
                        expanded = previewExpanded,
                        config = config,
                        palette = palette,
                        strings = strings,
                        systemState = systemState,
                        topInsetPadding = 12.dp,
                        isPreview = true,
                        onExpandedChange = { previewExpanded = it },
                        onTileClick = { tile -> viewModel.onTileTapped(tile, closeInAppIfExternal = false) },
                        onBrightnessChange = { viewModel.onBrightnessChanged(it) },
                        onVolumeChange = { viewModel.onVolumeChanged(it) },
                        onMediaKey = { viewModel.deviceManager.dispatchMediaKey(it) },
                        onOpenMusicApp = { viewModel.deviceManager.openMusicPlayer() },
                        onOpenVolumePanel = { viewModel.deviceManager.openVolumePanel() },
                        onOpenDisplaySettings = { viewModel.deviceManager.openDisplaySettings() },
                        onOpenBatterySettings = { viewModel.deviceManager.openBatterySettings() },
                        onOpenDeviceSettings = { viewModel.deviceManager.openSystemSettings() }
                    )
                }
            }

            // Section-Specific Controls
            item {
                when (activeTab) {
                    StudioTab.LAYOUT -> LayoutCustomizationSection(
                        config = config,
                        palette = palette,
                        strings = strings,
                        viewModel = viewModel
                    )
                    StudioTab.TILES -> TileManagerSection(
                        config = config,
                        palette = palette,
                        strings = strings,
                        systemState = systemState,
                        onMoveTile = { from, to -> viewModel.moveTile(from, to) },
                        onToggleTile = { viewModel.toggleTileEnabled(it) },
                        onToggleFavorite = { viewModel.toggleFavoriteTile(it) },
                        onToggleHideUnavailable = { hide ->
                            viewModel.updateConfig { it.copy(hideUnavailableTiles = hide) }
                        }
                    )
                    StudioTab.SLIDERS -> SlidersCustomizationSection(
                        config = config,
                        palette = palette,
                        strings = strings,
                        viewModel = viewModel
                    )
                    StudioTab.MEDIA -> MediaCustomizationSection(
                        config = config,
                        palette = palette,
                        strings = strings,
                        viewModel = viewModel
                    )
                    StudioTab.ICONS -> IconsAndTilesAppearanceSection(
                        config = config,
                        palette = palette,
                        strings = strings,
                        viewModel = viewModel
                    )
                    StudioTab.BACKGROUND -> BackgroundAndThemeSection(
                        config = config,
                        palette = palette,
                        strings = strings,
                        viewModel = viewModel
                    )
                    StudioTab.ANIMATIONS -> AnimationsAndHapticsSection(
                        config = config,
                        palette = palette,
                        strings = strings,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun PreviewModePill(
    label: String,
    selected: Boolean,
    palette: ResolvedVisualPalette,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) palette.accent else palette.inactiveTile.copy(alpha = 0.7f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) Color.White else palette.textSecondary
        )
    }
}

@Composable
private fun LayoutCustomizationSection(
    config: ControlCenterConfig,
    palette: ResolvedVisualPalette,
    strings: UiStrings,
    viewModel: ControlCenterViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Quick Layout Density Selector Cards matching the bottom-middle reference
        GlassSectionCard(palette = palette) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LayoutPresetCard(
                    label = strings.compactMode,
                    selected = config.initialVisibleTiles == 4 && config.gridColumns == 4,
                    rowsCount = 1,
                    palette = palette,
                    onClick = {
                        viewModel.updateConfig {
                            it.copy(initialVisibleTiles = 4, gridColumns = 4)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                LayoutPresetCard(
                    label = strings.mediumMode,
                    selected = config.initialVisibleTiles == 6 && config.gridColumns == 4,
                    rowsCount = 2,
                    palette = palette,
                    onClick = {
                        viewModel.updateConfig {
                            it.copy(initialVisibleTiles = 6, gridColumns = 4)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                LayoutPresetCard(
                    label = strings.expandedMode,
                    selected = config.initialVisibleTiles >= 8,
                    rowsCount = 3,
                    palette = palette,
                    onClick = {
                        viewModel.updateConfig {
                            it.copy(initialVisibleTiles = 12, gridColumns = 4)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        GlassSectionCard(palette = palette) {
            OneXSegmentedPills(
                title = strings.initialTileCount,
                options = listOf(4 to "4", 6 to "6", 8 to "8", 12 to "12"),
                selected = config.initialVisibleTiles,
                palette = palette,
                icon = Icons.Rounded.GridView,
                onSelect = { viewModel.setInitialVisibleTiles(it) },
                testTagPrefix = "initial_tiles"
            )
            OneXSegmentedPills(
                title = strings.gridColumnsLabel,
                options = listOf(3 to "3", 4 to "4", 5 to "5", 6 to "6"),
                selected = config.gridColumns,
                palette = palette,
                icon = Icons.Rounded.Tune,
                onSelect = { viewModel.setGridColumns(it) },
                testTagPrefix = "grid_columns"
            )
        }

        GlassSectionCard(palette = palette) {
            OneXToggleRow(
                title = strings.showBrightnessSlider,
                icon = Icons.Rounded.Brightness6,
                checked = config.showBrightnessSlider,
                palette = palette,
                onCheckedChange = { show -> viewModel.updateConfig { it.copy(showBrightnessSlider = show) } },
                testTag = "layout_toggle_brightness"
            )
            OneXToggleRow(
                title = strings.showVolumeSlider,
                icon = Icons.AutoMirrored.Rounded.VolumeUp,
                checked = config.showVolumeSlider,
                palette = palette,
                onCheckedChange = { show -> viewModel.updateConfig { it.copy(showVolumeSlider = show) } },
                testTag = "layout_toggle_volume"
            )
            OneXToggleRow(
                title = strings.showMediaPlayer,
                icon = Icons.Rounded.MusicNote,
                checked = config.showMediaPlayer,
                palette = palette,
                onCheckedChange = { show -> viewModel.updateConfig { it.copy(showMediaPlayer = show) } },
                testTag = "layout_toggle_media"
            )
            OneXToggleRow(
                title = strings.showConnectedDevices,
                icon = Icons.Rounded.Devices,
                checked = config.showConnectedDevicesRow,
                palette = palette,
                onCheckedChange = { show -> viewModel.updateConfig { it.copy(showConnectedDevicesRow = show) } },
                testTag = "layout_toggle_devices"
            )
        }
    }
}

@Composable
private fun LayoutPresetCard(
    label: String,
    selected: Boolean,
    rowsCount: Int,
    palette: ResolvedVisualPalette,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(palette.panelSurface.copy(alpha = 0.85f))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) palette.accent else Color.White.copy(alpha = 0.14f),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(54.dp)) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(Modifier.weight(1f).height(10.dp).clip(CircleShape).background(palette.accent.copy(alpha = 0.8f)))
                    Box(Modifier.weight(1f).height(10.dp).clip(CircleShape).background(palette.accent.copy(alpha = 0.5f)))
                }
                repeat(rowsCount) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        repeat(4) {
                            Box(Modifier.size(8.dp).clip(CircleShape).background(palette.textSecondary.copy(alpha = 0.55f)))
                        }
                    }
                }
            }
            if (selected) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = palette.accent,
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.BottomEnd)
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = if (selected) palette.textPrimary else palette.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SlidersCustomizationSection(
    config: ControlCenterConfig,
    palette: ResolvedVisualPalette,
    strings: UiStrings,
    viewModel: ControlCenterViewModel
) {
    GlassSectionCard(palette = palette) {
        OneXToggleRow(
            title = strings.showBrightnessSlider,
            icon = Icons.Rounded.Brightness6,
            checked = config.showBrightnessSlider,
            palette = palette,
            onCheckedChange = { v -> viewModel.updateConfig { it.copy(showBrightnessSlider = v) } },
            testTag = "sliders_show_brightness"
        )
        OneXToggleRow(
            title = strings.showVolumeSlider,
            icon = Icons.AutoMirrored.Rounded.VolumeUp,
            checked = config.showVolumeSlider,
            palette = palette,
            onCheckedChange = { v -> viewModel.updateConfig { it.copy(showVolumeSlider = v) } },
            testTag = "sliders_show_volume"
        )
        OneXToggleRow(
            title = strings.showSliderIcons,
            icon = Icons.Rounded.AutoAwesome,
            checked = config.showSliderIcons,
            palette = palette,
            onCheckedChange = { v -> viewModel.updateConfig { it.copy(showSliderIcons = v) } },
            testTag = "sliders_show_icons"
        )
        OneXSliderRow(
            title = strings.sliderThickness,
            valueLabel = "${config.sliderThicknessDp} dp",
            value = config.sliderThicknessDp.toFloat(),
            valueRange = 30f..56f,
            palette = palette,
            onValueChange = { v -> viewModel.updateConfig { it.copy(sliderThicknessDp = v.roundToInt()) } },
            testTag = "sliders_thickness"
        )
        OneXSegmentedPills(
            title = strings.sliderPosition,
            options = listOf(
                SliderPosition.TOP to strings.posTop,
                SliderPosition.BOTTOM to strings.posBottom
            ),
            selected = if (config.sliderPosition == SliderPosition.TOP) SliderPosition.TOP else SliderPosition.BOTTOM,
            palette = palette,
            onSelect = { viewModel.setSliderPosition(it) },
            testTagPrefix = "slider_pos"
        )
        OneXSegmentedPills(
            title = strings.sliderStyleLabel,
            options = listOf(
                SliderStyle.ONE_UI_GLOW to "One UI Glow",
                SliderStyle.FROSTED_GLASS to "Accent Glass",
                SliderStyle.MINIMAL_PILL to "Pure White"
            ),
            selected = config.sliderStyle,
            palette = palette,
            onSelect = { viewModel.setSliderStyle(it) },
            testTagPrefix = "slider_style"
        )
    }
}

@Composable
private fun MediaCustomizationSection(
    config: ControlCenterConfig,
    palette: ResolvedVisualPalette,
    strings: UiStrings,
    viewModel: ControlCenterViewModel
) {
    GlassSectionCard(palette = palette) {
        OneXToggleRow(
            title = strings.showMediaPlayer,
            icon = Icons.Rounded.MusicNote,
            checked = config.showMediaPlayer,
            palette = palette,
            onCheckedChange = { v -> viewModel.updateConfig { it.copy(showMediaPlayer = v) } },
            testTag = "media_show_player"
        )
        OneXToggleRow(
            title = strings.showConnectedDevices,
            icon = Icons.Rounded.Devices,
            checked = config.showConnectedDevicesRow,
            palette = palette,
            onCheckedChange = { v -> viewModel.updateConfig { it.copy(showConnectedDevicesRow = v) } },
            testTag = "media_show_devices"
        )
        OneXSegmentedPills(
            title = strings.mediaPosition,
            options = listOf(
                SliderPosition.TOP to strings.posTop,
                SliderPosition.MIDDLE to strings.posMiddle,
                SliderPosition.BOTTOM to strings.posBottom
            ),
            selected = config.mediaPlayerPosition,
            palette = palette,
            onSelect = { viewModel.setMediaPosition(it) },
            testTagPrefix = "media_pos"
        )
    }
}

@Composable
private fun IconsAndTilesAppearanceSection(
    config: ControlCenterConfig,
    palette: ResolvedVisualPalette,
    strings: UiStrings,
    viewModel: ControlCenterViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GlassSectionCard(palette = palette) {
            OneXSegmentedPills(
                title = strings.iconSizeLabel,
                options = IconSizeOption.entries.map { it to strings.iconSizeName(it) },
                selected = config.iconSize,
                palette = palette,
                icon = Icons.Rounded.FormatSize,
                onSelect = { viewModel.setIconSize(it) },
                testTagPrefix = "icon_size"
            )
            OneXSliderRow(
                title = strings.tileSizeLabel,
                valueLabel = "${config.tileSizeDp} dp",
                value = config.tileSizeDp.toFloat(),
                valueRange = 48f..68f,
                palette = palette,
                onValueChange = { v -> viewModel.updateConfig { it.copy(tileSizeDp = v.roundToInt()) } },
                testTag = "tile_size_slider"
            )
            OneXSliderRow(
                title = strings.tileCornerRadius,
                valueLabel = "${config.tileCornerRadiusDp} dp",
                value = config.tileCornerRadiusDp.toFloat(),
                valueRange = 10f..34f,
                icon = Icons.Rounded.RoundedCorner,
                palette = palette,
                onValueChange = { v -> viewModel.updateConfig { it.copy(tileCornerRadiusDp = v.roundToInt()) } },
                testTag = "tile_corner_slider"
            )
            OneXSliderRow(
                title = strings.tileSpacing,
                valueLabel = "${config.tileSpacingDp} dp",
                value = config.tileSpacingDp.toFloat(),
                valueRange = 6f..18f,
                palette = palette,
                onValueChange = { v -> viewModel.updateConfig { it.copy(tileSpacingDp = v.roundToInt()) } },
                testTag = "tile_spacing_slider"
            )
            OneXSegmentedPills(
                title = strings.iconAlignmentLabel,
                options = listOf(
                    TileAlignment.CENTER to strings.alignCenter,
                    TileAlignment.START to strings.alignStart
                ),
                selected = config.iconAlignment,
                palette = palette,
                onSelect = { viewModel.setIconAlignment(it) },
                testTagPrefix = "icon_align"
            )
        }

        GlassSectionCard(palette = palette) {
            OneXToggleRow(
                title = strings.showTileLabels,
                subtitle = strings.showTileLabelsSub,
                icon = Icons.Rounded.Label,
                checked = config.showTileLabels,
                palette = palette,
                onCheckedChange = { v -> viewModel.updateConfig { it.copy(showTileLabels = v) } },
                testTag = "icons_show_labels"
            )
            OneXSliderRow(
                title = strings.labelScale,
                valueLabel = formatPercent(config.labelScale),
                value = config.labelScale,
                valueRange = 0.85f..1.25f,
                palette = palette,
                onValueChange = { v -> viewModel.updateConfig { it.copy(labelScale = v) } },
                testTag = "icons_label_scale"
            )
            OneXColorSwatchRow(
                title = strings.activeTileColor,
                swatches = PresetColorSwatches,
                selectedArgb = config.customActiveColorArgb,
                palette = palette,
                onSelect = { argb ->
                    viewModel.updateConfig {
                        it.copy(themePreset = ThemePreset.CUSTOM, customActiveColorArgb = argb)
                    }
                },
                testTagPrefix = "active_color"
            )
            OneXColorSwatchRow(
                title = strings.inactiveTileColor,
                swatches = InactiveColorSwatches,
                selectedArgb = config.customInactiveColorArgb,
                palette = palette,
                onSelect = { argb ->
                    viewModel.updateConfig {
                        it.copy(themePreset = ThemePreset.CUSTOM, customInactiveColorArgb = argb)
                    }
                },
                testTagPrefix = "inactive_color"
            )
        }
    }
}

@Composable
private fun BackgroundAndThemeSection(
    config: ControlCenterConfig,
    palette: ResolvedVisualPalette,
    strings: UiStrings,
    viewModel: ControlCenterViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 8 Premium Theme Presets
        GlassSectionCard(palette = palette) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Rounded.Palette, contentDescription = null, tint = palette.accentSecondary)
                Text(strings.themePresets, style = MaterialTheme.typography.titleMedium, color = palette.textPrimary)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ThemePreset.entries.forEach { preset ->
                    val isSelected = config.themePreset == preset
                    Column(
                        modifier = Modifier
                            .width(96.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(preset.panelSurface))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(preset.accentSecondary) else Color.White.copy(alpha = 0.14f),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable { viewModel.setThemePreset(preset) }
                            .padding(10.dp)
                            .testTag("theme_preset_${preset.id}"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(preset.accent), Color(preset.bgBottom))
                                    )
                                )
                                .border(1.5.dp, Color(preset.accentSecondary), CircleShape)
                        )
                        Text(
                            text = strings.presetName(preset),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(preset.textPrimary),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        GlassSectionCard(palette = palette) {
            OneXSliderRow(
                title = strings.bgTransparency,
                valueLabel = formatPercent(config.backgroundAlpha),
                value = config.backgroundAlpha,
                valueRange = 0.40f..1.0f,
                icon = Icons.Rounded.Opacity,
                palette = palette,
                onValueChange = { v -> viewModel.updateConfig { it.copy(backgroundAlpha = v) } },
                testTag = "bg_transparency_slider"
            )
            OneXSliderRow(
                title = strings.bgBlurIntensity,
                valueLabel = formatPercent(config.blurIntensity),
                value = config.blurIntensity,
                valueRange = 0f..1f,
                icon = Icons.Rounded.BlurOn,
                palette = palette,
                onValueChange = { v -> viewModel.updateConfig { it.copy(blurIntensity = v) } },
                testTag = "bg_blur_slider"
            )
            OneXSliderRow(
                title = strings.glassEffectIntensity,
                valueLabel = formatPercent(config.glassIntensity),
                value = config.glassIntensity,
                valueRange = 0f..1f,
                icon = Icons.Rounded.AutoAwesome,
                palette = palette,
                onValueChange = { v -> viewModel.updateConfig { it.copy(glassIntensity = v) } },
                testTag = "bg_glass_slider"
            )
            OneXSliderRow(
                title = strings.panelCornerRadius,
                valueLabel = "${config.panelCornerRadiusDp} dp",
                value = config.panelCornerRadiusDp.toFloat(),
                valueRange = 14f..40f,
                icon = Icons.Rounded.RoundedCorner,
                palette = palette,
                onValueChange = { v -> viewModel.updateConfig { it.copy(panelCornerRadiusDp = v.roundToInt()) } },
                testTag = "bg_panel_corner_slider"
            )
            OneXSliderRow(
                title = strings.shadowIntensity,
                valueLabel = formatPercent(config.shadowIntensity),
                value = config.shadowIntensity,
                valueRange = 0f..1f,
                palette = palette,
                onValueChange = { v -> viewModel.updateConfig { it.copy(shadowIntensity = v) } },
                testTag = "bg_shadow_slider"
            )
            OneXColorSwatchRow(
                title = strings.accentColor,
                swatches = PresetColorSwatches,
                selectedArgb = config.customAccentColorArgb,
                palette = palette,
                onSelect = { argb ->
                    viewModel.updateConfig {
                        it.copy(
                            themePreset = ThemePreset.CUSTOM,
                            customAccentColorArgb = argb,
                            customActiveColorArgb = argb
                        )
                    }
                },
                testTagPrefix = "accent_color"
            )
        }
    }
}

@Composable
private fun AnimationsAndHapticsSection(
    config: ControlCenterConfig,
    palette: ResolvedVisualPalette,
    strings: UiStrings,
    viewModel: ControlCenterViewModel
) {
    GlassSectionCard(palette = palette) {
        OneXToggleRow(
            title = strings.hapticsFeedback,
            icon = Icons.Rounded.Vibration,
            checked = config.hapticsEnabled,
            palette = palette,
            onCheckedChange = { v ->
                viewModel.deviceManager.performHaptic(v)
                viewModel.updateConfig { it.copy(hapticsEnabled = v) }
            },
            testTag = "anim_haptics"
        )
        OneXToggleRow(
            title = strings.autoSaveChanges,
            icon = Icons.Rounded.Save,
            checked = config.autoSaveEnabled,
            palette = palette,
            onCheckedChange = { v -> viewModel.updateConfig { it.copy(autoSaveEnabled = v) } },
            testTag = "anim_autosave"
        )
        OneXToggleRow(
            title = strings.rememberLastState,
            icon = Icons.Rounded.Animation,
            checked = config.rememberLastExpandedState,
            palette = palette,
            onCheckedChange = { v -> viewModel.updateConfig { it.copy(rememberLastExpandedState = v) } },
            testTag = "anim_remember_state"
        )
        OneXSliderRow(
            title = strings.openAnimSpeed,
            valueLabel = "${config.openSpeedMs} ms",
            value = config.openSpeedMs.toFloat(),
            valueRange = 160f..520f,
            icon = Icons.Rounded.Speed,
            palette = palette,
            onValueChange = { v -> viewModel.updateConfig { it.copy(openSpeedMs = v.roundToInt()) } },
            testTag = "anim_open_speed"
        )
        OneXSliderRow(
            title = strings.closeAnimSpeed,
            valueLabel = "${config.closeSpeedMs} ms",
            value = config.closeSpeedMs.toFloat(),
            valueRange = 120f..440f,
            icon = Icons.Rounded.Speed,
            palette = palette,
            onValueChange = { v -> viewModel.updateConfig { it.copy(closeSpeedMs = v.roundToInt()) } },
            testTag = "anim_close_speed"
        )
        OutlinedButton(
            onClick = { viewModel.resetToDefaults() },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .testTag("anim_restore_defaults_btn"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171))
        ) {
            Icon(Icons.Rounded.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text(strings.restoreDefaults)
        }
    }
}
