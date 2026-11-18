package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AirplanemodeActive
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BatterySaver
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.DevicesOther
import androidx.compose.material.icons.rounded.DoNotDisturbOn
import androidx.compose.material.icons.rounded.FlashlightOn
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LaptopWindows
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Nfc
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RemoveRedEye
import androidx.compose.material.icons.rounded.ScreenRotation
import androidx.compose.material.icons.rounded.Screenshot
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.WifiTethering
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.data.QuickTileId

fun quickTileIcon(tile: QuickTileId): ImageVector = when (tile) {
    QuickTileId.WIFI -> Icons.Rounded.Wifi
    QuickTileId.BLUETOOTH -> Icons.Rounded.Bluetooth
    QuickTileId.MOBILE_DATA -> Icons.Rounded.SwapVert
    QuickTileId.AIRPLANE_MODE -> Icons.Rounded.AirplanemodeActive
    QuickTileId.HOTSPOT -> Icons.Rounded.WifiTethering
    QuickTileId.LOCATION -> Icons.Rounded.LocationOn
    QuickTileId.NFC -> Icons.Rounded.Nfc
    QuickTileId.VPN -> Icons.Rounded.VpnKey
    QuickTileId.AUTO_ROTATE -> Icons.Rounded.ScreenRotation
    QuickTileId.FLASHLIGHT -> Icons.Rounded.FlashlightOn
    QuickTileId.DND -> Icons.Rounded.DoNotDisturbOn
    QuickTileId.BATTERY_SAVER -> Icons.Rounded.BatterySaver
    QuickTileId.DARK_MODE -> Icons.Rounded.DarkMode
    QuickTileId.READING_MODE -> Icons.Rounded.MenuBook
    QuickTileId.EYE_COMFORT -> Icons.Rounded.RemoveRedEye
    QuickTileId.SCREEN_TIMEOUT -> Icons.Rounded.Timer
    QuickTileId.SCREENSHOT -> Icons.Rounded.Screenshot
    QuickTileId.SCREEN_RECORDING -> Icons.Rounded.Videocam
    QuickTileId.PERFORMANCE_MODE -> Icons.Rounded.Speed
    QuickTileId.SYNC_SETTINGS -> Icons.Rounded.Sync
    QuickTileId.MI_SHARE -> Icons.Rounded.Share
    QuickTileId.LINK_TO_WINDOWS -> Icons.Rounded.LaptopWindows
    QuickTileId.CAST_WIRELESS -> Icons.Rounded.Cast
    QuickTileId.SMART_DEVICES -> Icons.Rounded.DevicesOther
    QuickTileId.HOME_CONTROL -> Icons.Rounded.Home
    QuickTileId.CONNECTED_DEVICES -> Icons.Rounded.Devices
    QuickTileId.BT_AUDIO -> Icons.Rounded.Headphones
    QuickTileId.DEVICE_MANAGEMENT -> Icons.Rounded.Tune
    QuickTileId.STORAGE_CLEANER -> Icons.Rounded.CleaningServices
    QuickTileId.NEARBY_SHARE -> Icons.Rounded.Share
    QuickTileId.SOUND_MODE -> Icons.Rounded.VolumeUp
    QuickTileId.SILENT_MODE -> Icons.Rounded.NotificationsOff
    QuickTileId.VIBRATE_MODE -> Icons.Rounded.Vibration
    QuickTileId.APP_SETTINGS -> Icons.Rounded.Settings
}

val FloatingButtonIcons: List<ImageVector> = listOf(
    Icons.Rounded.GridView,
    Icons.Rounded.Widgets,
    Icons.Rounded.Tune,
    Icons.Rounded.AutoAwesome,
    Icons.Rounded.RadioButtonChecked
)

fun floatingButtonIcon(index: Int): ImageVector =
    FloatingButtonIcons.getOrElse(index) { Icons.Rounded.GridView }
