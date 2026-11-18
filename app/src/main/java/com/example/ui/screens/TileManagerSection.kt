package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.data.ControlCenterConfig
import com.example.data.QuickTileId
import com.example.data.ResolvedVisualPalette
import com.example.data.UiStrings
import com.example.system.DeviceSystemState
import com.example.ui.components.GlassSectionCard
import com.example.ui.components.OneXToggleRow
import com.example.ui.components.quickTileIcon
import com.example.ui.theme.WarningAmber

@Composable
fun TileManagerSection(
    config: ControlCenterConfig,
    palette: ResolvedVisualPalette,
    strings: UiStrings,
    systemState: DeviceSystemState,
    onMoveTile: (fromIndex: Int, toIndex: Int) -> Unit,
    onToggleTile: (QuickTileId) -> Unit,
    onToggleFavorite: (QuickTileId) -> Unit,
    onToggleHideUnavailable: (Boolean) -> Unit
) {
    val activeTiles = config.activeTileIds
    val inactiveTiles = remember(activeTiles) {
        QuickTileId.entries.filter {
            it != QuickTileId.WIFI && it != QuickTileId.BLUETOOTH && !activeTiles.contains(it)
        }
    }

    var draggedIndex by remember { mutableIntStateOf(-1) }
    var dragAccumulatedY by remember { mutableFloatStateOf(0f) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GlassSectionCard(palette = palette) {
            OneXToggleRow(
                title = strings.hideUnavailableTiles,
                icon = Icons.Rounded.VisibilityOff,
                checked = config.hideUnavailableTiles,
                palette = palette,
                onCheckedChange = onToggleHideUnavailable,
                testTag = "toggle_hide_unavailable_tiles"
            )
            Text(
                text = "${strings.dragOrUseArrowsHint} • ${strings.favoritesOnlyHint}",
                style = MaterialTheme.typography.labelMedium,
                color = palette.textSecondary
            )
        }

        GlassSectionCard(palette = palette) {
            Text(
                text = strings.activeTilesSection,
                style = MaterialTheme.typography.titleMedium,
                color = palette.textPrimary
            )

            activeTiles.forEachIndexed { index, tile ->
                val isFav = config.favoriteTileIds.contains(tile)
                val isAvailable = systemState.isTileAvailable(tile)
                val isDragging = draggedIndex == index

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(if (isDragging) 2f else 0f)
                        .graphicsLayer {
                            translationY = if (isDragging) dragAccumulatedY else 0f
                            scaleX = if (isDragging) 1.03f else 1f
                            scaleY = if (isDragging) 1.03f else 1f
                        }
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (isDragging) palette.accent.copy(alpha = 0.28f)
                            else palette.inactiveTile.copy(alpha = 0.55f)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isDragging) palette.accentSecondary else Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .pointerInput(activeTiles.size, index) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    draggedIndex = index
                                    dragAccumulatedY = 0f
                                },
                                onDragEnd = {
                                    draggedIndex = -1
                                    dragAccumulatedY = 0f
                                },
                                onDragCancel = {
                                    draggedIndex = -1
                                    dragAccumulatedY = 0f
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    dragAccumulatedY += dragAmount.y
                                    val rowHeightPx = 140f
                                    if (dragAccumulatedY > rowHeightPx && draggedIndex in 0 until activeTiles.lastIndex) {
                                        val next = draggedIndex + 1
                                        onMoveTile(draggedIndex, next)
                                        draggedIndex = next
                                        dragAccumulatedY = 0f
                                    } else if (dragAccumulatedY < -rowHeightPx && draggedIndex > 0) {
                                        val prev = draggedIndex - 1
                                        onMoveTile(draggedIndex, prev)
                                        draggedIndex = prev
                                        dragAccumulatedY = 0f
                                    }
                                }
                            )
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("tile_mgr_active_${tile.id}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DragHandle,
                        contentDescription = "Drag to reorder",
                        tint = palette.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isAvailable) palette.activeTile.copy(alpha = 0.35f) else palette.inactiveTile),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = quickTileIcon(tile),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.tileTitle(tile),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = palette.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!isAvailable) {
                            Text(
                                text = strings.unavailable,
                                style = MaterialTheme.typography.labelSmall,
                                color = WarningAmber
                            )
                        }
                    }

                    IconButton(
                        onClick = { onToggleFavorite(tile) },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("tile_mgr_fav_${tile.id}")
                    ) {
                        Icon(
                            imageVector = if (isFav) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                            contentDescription = "Favorite",
                            tint = if (isFav) WarningAmber else palette.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = { if (index > 0) onMoveTile(index, index - 1) },
                        enabled = index > 0,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("tile_mgr_up_${tile.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowUpward,
                            contentDescription = "Move Up",
                            tint = if (index > 0) palette.textPrimary else palette.textSecondary.copy(alpha = 0.3f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = { if (index < activeTiles.lastIndex) onMoveTile(index, index + 1) },
                        enabled = index < activeTiles.lastIndex,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("tile_mgr_down_${tile.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowDownward,
                            contentDescription = "Move Down",
                            tint = if (index < activeTiles.lastIndex) palette.textPrimary else palette.textSecondary.copy(alpha = 0.3f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = { onToggleTile(tile) },
                        enabled = activeTiles.size > 4,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("tile_mgr_remove_${tile.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.RemoveCircleOutline,
                            contentDescription = "Remove tile",
                            tint = if (activeTiles.size > 4) Color(0xFFF87171) else palette.textSecondary.copy(alpha = 0.3f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        if (inactiveTiles.isNotEmpty()) {
            GlassSectionCard(palette = palette) {
                Text(
                    text = strings.availableToAddSection,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary
                )
                inactiveTiles.forEach { tile ->
                    val isAvailable = systemState.isTileAvailable(tile)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(palette.panelSurface.copy(alpha = 0.65f))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("tile_mgr_add_row_${tile.id}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = quickTileIcon(tile),
                            contentDescription = null,
                            tint = palette.accentSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.tileTitle(tile),
                                style = MaterialTheme.typography.titleSmall,
                                color = palette.textPrimary
                            )
                            if (!isAvailable) {
                                Text(
                                    text = strings.unavailable,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = WarningAmber
                                )
                            }
                        }
                        IconButton(
                            onClick = { onToggleTile(tile) },
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("tile_mgr_add_${tile.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddCircle,
                                contentDescription = "Add tile",
                                tint = palette.accent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
