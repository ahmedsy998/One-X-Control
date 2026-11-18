package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.Brightness6
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.data.ResolvedVisualPalette
import com.example.data.UiStrings
import com.example.ui.components.OneXTopHeader

enum class SettingsSection(val route: String) {
    LAYOUT("settings_layout"),
    TILES("settings_tiles"),
    SLIDERS("settings_sliders"),
    MEDIA("settings_media"),
    ICONS("settings_icons"),
    BACKGROUND("settings_background"),
    ANIMATIONS("settings_animations"),
    FLOATING_BUTTON("settings_floating"),
    PERMISSIONS("settings_permissions"),
    BACKUP("settings_backup"),
    ABOUT("settings_about")
}

private data class SettingsMenuItem(
    val section: SettingsSection,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val highlighted: Boolean = false
)

@Composable
fun SettingsHubScreen(
    palette: ResolvedVisualPalette,
    strings: UiStrings,
    onSelectSection: (SettingsSection) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        SettingsMenuItem(SettingsSection.LAYOUT, strings.secLayout, strings.secLayoutSub, Icons.Rounded.GridView, highlighted = true),
        SettingsMenuItem(SettingsSection.TILES, strings.secTiles, strings.secTilesSub, Icons.Rounded.Tune),
        SettingsMenuItem(SettingsSection.SLIDERS, strings.secSliders, strings.secSlidersSub, Icons.Rounded.Brightness6),
        SettingsMenuItem(SettingsSection.MEDIA, strings.secMedia, strings.secMediaSub, Icons.Rounded.MusicNote),
        SettingsMenuItem(SettingsSection.ICONS, strings.secIcons, strings.secIconsSub, Icons.Rounded.FormatSize),
        SettingsMenuItem(SettingsSection.BACKGROUND, strings.secBackground, strings.secBackgroundSub, Icons.Rounded.Palette),
        SettingsMenuItem(SettingsSection.ANIMATIONS, strings.secAnimations, strings.secAnimationsSub, Icons.Rounded.Animation),
        SettingsMenuItem(SettingsSection.FLOATING_BUTTON, strings.secFloatingBtn, strings.secFloatingBtnSub, Icons.Rounded.RadioButtonChecked),
        SettingsMenuItem(SettingsSection.PERMISSIONS, strings.secPermissions, strings.secPermissionsSub, Icons.Rounded.Security),
        SettingsMenuItem(SettingsSection.BACKUP, strings.secBackup, strings.secBackupSub, Icons.Rounded.CloudUpload),
        SettingsMenuItem(SettingsSection.ABOUT, strings.secAbout, strings.secAboutSub, Icons.Rounded.Info)
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp),
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding() + 6.dp,
                bottom = contentPadding.calculateBottomPadding() + 28.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OneXTopHeader(
                    title = strings.settingsHubTitle,
                    backContentDescription = strings.backLabel,
                    palette = palette
                )
            }

            item {
                // Single frosted One UI Settings Group Container matching the bottom-left reference image
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    palette.cardSurface.copy(alpha = 0.72f),
                                    palette.panelSurface.copy(alpha = 0.80f)
                                )
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(26.dp))
                        .padding(vertical = 6.dp)
                ) {
                    items.forEachIndexed { idx, item ->
                        SettingsMenuRow(
                            item = item,
                            palette = palette,
                            onClick = { onSelectSection(item.section) }
                        )
                        if (idx < items.lastIndex && !item.highlighted) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 18.dp),
                                color = Color.White.copy(alpha = 0.08f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsMenuRow(
    item: SettingsMenuItem,
    palette: ResolvedVisualPalette,
    onClick: () -> Unit
) {
    val rowModifier = if (item.highlighted) {
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        palette.accent.copy(alpha = 0.85f),
                        palette.activeTile.copy(alpha = 0.45f)
                    )
                )
            )
            .border(1.dp, palette.accentSecondary.copy(alpha = 0.65f), RoundedCornerShape(20.dp))
    } else {
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(16.dp))
    }

    Row(
        modifier = rowModifier
            .heightIn(min = 60.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("settings_row_${item.section.route}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = if (item.highlighted) Color.White else palette.accentSecondary,
            modifier = Modifier.size(23.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = if (item.highlighted) FontWeight.Bold else FontWeight.SemiBold
                ),
                color = if (item.highlighted) Color.White else palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = if (item.highlighted) Color.White.copy(alpha = 0.85f) else palette.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = if (item.highlighted) Color.White else palette.textSecondary,
            modifier = Modifier.size(22.dp)
        )
    }
}
