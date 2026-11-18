package com.example.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Brightness6
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DoNotDisturbOn
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Opacity
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.AppLanguage
import com.example.data.ControlCenterConfig
import com.example.data.ResolvedVisualPalette
import com.example.data.UiStrings
import com.example.system.DeviceSystemState
import com.example.system.PermissionStatus
import com.example.ui.components.FloatingButtonIcons
import com.example.ui.components.FloatingControlButton
import com.example.ui.components.GlassSectionCard
import com.example.ui.components.OneXSegmentedPills
import com.example.ui.components.OneXSliderRow
import com.example.ui.components.OneXToggleRow
import com.example.ui.components.OneXTopHeader
import com.example.ui.components.formatPercent
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.ControlCenterViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun FloatingButtonSettingsScreen(
    config: ControlCenterConfig,
    palette: ResolvedVisualPalette,
    strings: UiStrings,
    isOverlayRunning: Boolean,
    viewModel: ControlCenterViewModel,
    onBack: () -> Unit,
    contentPadding: PaddingValues
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp),
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding() + 4.dp,
                bottom = contentPadding.calculateBottomPadding() + 28.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OneXTopHeader(
                    title = strings.secFloatingBtn,
                    backContentDescription = strings.backLabel,
                    palette = palette,
                    onBack = onBack
                )
            }

            // Live Floating Button Preview Stage
            item {
                GlassSectionCard(palette = palette) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 110.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        FloatingControlButton(
                            config = config,
                            palette = palette,
                            contentDescription = strings.pullDownControlCenter,
                            onClick = { viewModel.openInAppControlCenter() }
                        )
                    }
                }
            }

            item {
                GlassSectionCard(palette = palette) {
                    OneXToggleRow(
                        title = strings.floatingBtnEnable,
                        icon = Icons.Rounded.RadioButtonChecked,
                        checked = isOverlayRunning || config.floatingButtonEnabled,
                        palette = palette,
                        onCheckedChange = { enable -> viewModel.toggleSystemFloatingOverlay(enable) },
                        testTag = "floating_enable_switch"
                    )
                    OneXToggleRow(
                        title = strings.floatingBtnVisibility,
                        icon = Icons.Rounded.Visibility,
                        checked = config.floatingButtonVisible,
                        palette = palette,
                        onCheckedChange = { v -> viewModel.updateConfig { it.copy(floatingButtonVisible = v) } },
                        testTag = "floating_visible_switch"
                    )
                    OneXToggleRow(
                        title = strings.floatingBtnTempHide,
                        icon = Icons.Rounded.VisibilityOff,
                        checked = config.floatingButtonTemporarilyHidden,
                        palette = palette,
                        onCheckedChange = { v ->
                            viewModel.updateConfig { it.copy(floatingButtonTemporarilyHidden = v) }
                        },
                        testTag = "floating_temp_hide_switch"
                    )
                    OneXToggleRow(
                        title = strings.floatingBtnSelectedOnly,
                        checked = config.floatingShowOnSelectedScreensOnly,
                        palette = palette,
                        onCheckedChange = { v ->
                            viewModel.updateConfig { it.copy(floatingShowOnSelectedScreensOnly = v) }
                        },
                        testTag = "floating_selected_only_switch"
                    )
                }
            }

            item {
                GlassSectionCard(palette = palette) {
                    OneXSliderRow(
                        title = strings.floatingBtnSize,
                        valueLabel = "${config.floatingButtonSizeDp} dp",
                        value = config.floatingButtonSizeDp.toFloat(),
                        valueRange = 48f..72f,
                        palette = palette,
                        onValueChange = { v -> viewModel.updateConfig { it.copy(floatingButtonSizeDp = v.roundToInt()) } },
                        testTag = "floating_size_slider"
                    )
                    OneXSliderRow(
                        title = strings.floatingBtnTransparency,
                        valueLabel = formatPercent(config.floatingButtonAlpha),
                        value = config.floatingButtonAlpha,
                        valueRange = 0.35f..1.0f,
                        icon = Icons.Rounded.Opacity,
                        palette = palette,
                        onValueChange = { v -> viewModel.updateConfig { it.copy(floatingButtonAlpha = v) } },
                        testTag = "floating_alpha_slider"
                    )

                    Text(
                        text = strings.floatingBtnIcon,
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.textPrimary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FloatingButtonIcons.forEachIndexed { idx, iconVec ->
                            val selected = config.floatingButtonIconIndex == idx
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(if (selected) palette.accent else palette.inactiveTile)
                                    .border(
                                        width = if (selected) 2.dp else 1.dp,
                                        color = if (selected) Color.White else Color.White.copy(alpha = 0.16f),
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        viewModel.updateConfig { it.copy(floatingButtonIconIndex = idx) }
                                    }
                                    .testTag("floating_icon_choice_$idx"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(iconVec, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.resetFloatingPosition() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("floating_reset_pos_btn"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Rounded.RestartAlt, contentDescription = null, tint = palette.accentSecondary)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(strings.floatingBtnResetPos, color = palette.textPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionsCompatibilityScreen(
    palette: ResolvedVisualPalette,
    strings: UiStrings,
    systemState: DeviceSystemState,
    permissions: PermissionStatus,
    viewModel: ControlCenterViewModel,
    onBack: () -> Unit,
    contentPadding: PaddingValues
) {
    val notifLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { viewModel.deviceManager.refreshAll() }

    val btLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { viewModel.deviceManager.refreshAll() }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp),
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding() + 4.dp,
                bottom = contentPadding.calculateBottomPadding() + 28.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OneXTopHeader(
                    title = strings.secPermissions,
                    backContentDescription = strings.backLabel,
                    palette = palette,
                    onBack = onBack
                )
            }

            item {
                PermissionCard(
                    icon = Icons.Rounded.Layers,
                    title = strings.permOverlayTitle,
                    description = strings.permOverlayDesc,
                    granted = permissions.overlayGranted,
                    badge = if (permissions.overlayGranted) strings.granted else strings.requiredForFloating,
                    actionLabel = strings.grantPermissionBtn,
                    palette = palette,
                    onAction = { viewModel.deviceManager.openOverlayPermission() },
                    testTag = "perm_overlay_btn"
                )
            }
            item {
                PermissionCard(
                    icon = Icons.Rounded.Brightness6,
                    title = strings.permWriteSettingsTitle,
                    description = strings.permWriteSettingsDesc,
                    granted = permissions.writeSettingsGranted,
                    badge = if (permissions.writeSettingsGranted) strings.granted else strings.optional,
                    actionLabel = strings.grantPermissionBtn,
                    palette = palette,
                    onAction = { viewModel.deviceManager.openWriteSettingsPermission() },
                    testTag = "perm_write_settings_btn"
                )
            }
            item {
                PermissionCard(
                    icon = Icons.Rounded.DoNotDisturbOn,
                    title = strings.permDndTitle,
                    description = strings.permDndDesc,
                    granted = permissions.dndAccessGranted,
                    badge = if (permissions.dndAccessGranted) strings.granted else strings.optional,
                    actionLabel = strings.grantPermissionBtn,
                    palette = palette,
                    onAction = { viewModel.deviceManager.openDndAccessPermission() },
                    testTag = "perm_dnd_btn"
                )
            }
            item {
                PermissionCard(
                    icon = Icons.Rounded.Notifications,
                    title = strings.permNotifTitle,
                    description = strings.permNotifDesc,
                    granted = permissions.notificationsGranted,
                    badge = if (permissions.notificationsGranted) strings.granted else strings.optional,
                    actionLabel = strings.grantPermissionBtn,
                    palette = palette,
                    onAction = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.deviceManager.openAppNotificationSettings()
                        }
                    },
                    testTag = "perm_notif_btn"
                )
            }
            item {
                PermissionCard(
                    icon = Icons.Rounded.Bluetooth,
                    title = strings.permBluetoothTitle,
                    description = strings.permBluetoothDesc,
                    granted = permissions.bluetoothConnectGranted,
                    badge = if (permissions.bluetoothConnectGranted) strings.granted else strings.optional,
                    actionLabel = strings.grantPermissionBtn,
                    palette = palette,
                    onAction = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            btLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
                        }
                    },
                    testTag = "perm_bt_btn"
                )
            }

            // Real Dynamic Hardware & HyperOS Feature Audit
            item {
                val avail = systemState.availability
                GlassSectionCard(palette = palette) {
                    Text(
                        text = strings.hardwareCompatTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.textPrimary
                    )
                    HardwareStatusRow("Camera Flashlight Torch", avail.hasFlashlight, strings, palette)
                    HardwareStatusRow("NFC Hardware", avail.hasNfc, strings, palette)
                    HardwareStatusRow("Bluetooth Adapter", avail.hasBluetooth, strings, palette)
                    HardwareStatusRow("Cellular / Mobile Data", avail.hasTelephony, strings, palette)
                    HardwareStatusRow("Xiaomi Mi Share", avail.hasMiShare, strings, palette)
                    HardwareStatusRow("Link to Windows", avail.hasLinkToWindows, strings, palette)
                    HardwareStatusRow("Xiaomi Home", avail.hasXiaomiHome, strings, palette)
                    HardwareStatusRow("Xiaomi Security Cleaner", avail.hasXiaomiCleaner, strings, palette)
                }
            }
        }
    }
}

@Composable
private fun PermissionCard(
    icon: ImageVector,
    title: String,
    description: String,
    granted: Boolean,
    badge: String,
    actionLabel: String,
    palette: ResolvedVisualPalette,
    onAction: () -> Unit,
    testTag: String
) {
    GlassSectionCard(palette = palette) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(palette.accent.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = palette.accentSecondary, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = palette.textPrimary)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (granted) Icons.Rounded.CheckCircle else Icons.Rounded.WarningAmber,
                        contentDescription = null,
                        tint = if (granted) SuccessGreen else WarningAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (granted) SuccessGreen else WarningAmber
                    )
                }
            }
        }
        Text(description, style = MaterialTheme.typography.bodyMedium, color = palette.textSecondary)
        if (!granted) {
            Button(
                onClick = onAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .testTag(testTag),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = palette.accent)
            ) {
                Text(actionLabel, color = Color.White)
            }
        }
    }
}

@Composable
private fun HardwareStatusRow(
    name: String,
    available: Boolean,
    strings: UiStrings,
    palette: ResolvedVisualPalette
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(name, style = MaterialTheme.typography.bodyMedium, color = palette.textPrimary)
        Text(
            text = if (available) strings.available else strings.notInstalled,
            style = MaterialTheme.typography.labelMedium,
            color = if (available) SuccessGreen else palette.textSecondary
        )
    }
}

@Composable
fun BackupRestoreScreen(
    palette: ResolvedVisualPalette,
    strings: UiStrings,
    viewModel: ControlCenterViewModel,
    onBack: () -> Unit,
    contentPadding: PaddingValues
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                runCatching {
                    val json = viewModel.exportBackupJson()
                    context.contentResolver.openOutputStream(uri)?.use { stream ->
                        stream.write(json.toByteArray(Charsets.UTF_8))
                    }
                    statusMessage = strings.backupSuccess
                }.onFailure {
                    statusMessage = strings.restoreFailed
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val raw = runCatching {
                    context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                }.getOrNull()
                if (raw != null) {
                    val result = viewModel.importBackupJson(raw)
                    statusMessage = if (result.isSuccess) strings.restoreSuccess else strings.restoreFailed
                } else {
                    statusMessage = strings.restoreFailed
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp),
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding() + 4.dp,
                bottom = contentPadding.calculateBottomPadding() + 28.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OneXTopHeader(
                    title = strings.secBackup,
                    backContentDescription = strings.backLabel,
                    palette = palette,
                    onBack = onBack
                )
            }

            item {
                GlassSectionCard(palette = palette) {
                    Button(
                        onClick = { exportLauncher.launch("one_x_control_backup.json") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 50.dp)
                            .testTag("backup_export_btn"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = palette.accent)
                    ) {
                        Icon(Icons.Rounded.Upload, contentDescription = null)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(strings.exportBackup, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = { importLauncher.launch(arrayOf("application/json", "text/plain")) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 50.dp)
                            .testTag("backup_import_btn"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Rounded.Download, contentDescription = null, tint = palette.accentSecondary)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(strings.importBackup, color = palette.textPrimary)
                    }

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                val json = viewModel.exportBackupJson()
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("ONE X CONTROL Backup", json))
                                statusMessage = strings.backupSuccess
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 50.dp)
                            .testTag("backup_copy_btn"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Rounded.ContentCopy, contentDescription = null, tint = palette.accentSecondary)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(strings.copyBackupJson, color = palette.textPrimary)
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.resetToDefaults()
                            statusMessage = strings.restoreSuccess
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .testTag("backup_reset_defaults_btn"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171))
                    ) {
                        Icon(Icons.Rounded.RestartAlt, contentDescription = null)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(strings.restoreDefaults)
                    }

                    if (!statusMessage.isNullOrBlank()) {
                        Text(
                            text = statusMessage!!,
                            style = MaterialTheme.typography.labelLarge,
                            color = SuccessGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AboutAndLanguageScreen(
    config: ControlCenterConfig,
    palette: ResolvedVisualPalette,
    strings: UiStrings,
    systemState: DeviceSystemState,
    viewModel: ControlCenterViewModel,
    onBack: () -> Unit,
    contentPadding: PaddingValues
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp),
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding() + 4.dp,
                bottom = contentPadding.calculateBottomPadding() + 28.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OneXTopHeader(
                    title = strings.secAbout,
                    backContentDescription = strings.backLabel,
                    palette = palette,
                    onBack = onBack
                )
            }

            item {
                GlassSectionCard(palette = palette) {
                    OneXSegmentedPills(
                        title = strings.languageLabel,
                        options = listOf(
                            AppLanguage.SYSTEM to strings.langSystem,
                            AppLanguage.ARABIC to strings.langArabic,
                            AppLanguage.ENGLISH to strings.langEnglish
                        ),
                        selected = config.language,
                        palette = palette,
                        icon = Icons.Rounded.Language,
                        onSelect = { viewModel.setLanguage(it) },
                        testTagPrefix = "lang_option"
                    )
                }
            }

            item {
                GlassSectionCard(palette = palette) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = palette.accentSecondary)
                        Column {
                            Text(
                                text = strings.controlCenterHeader,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = palette.textPrimary
                            )
                            Text(
                                text = "v1.0.0 • com.onex.control",
                                style = MaterialTheme.typography.labelMedium,
                                color = palette.accentSecondary
                            )
                        }
                    }
                    Text(
                        text = "${systemState.deviceModel} • ${systemState.androidVersion}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textSecondary
                    )
                    Text(
                        text = strings.aboutDisclaimerTitle,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = palette.textPrimary
                    )
                    Text(
                        text = strings.aboutDisclaimerBody,
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textSecondary
                    )
                }
            }
        }
    }
}
