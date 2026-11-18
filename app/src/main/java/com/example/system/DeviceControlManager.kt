package com.example.system

import android.Manifest
import android.app.NotificationManager
import android.bluetooth.BluetoothManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.location.LocationManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.wifi.WifiManager
import android.nfc.NfcAdapter
import android.os.Build
import android.os.PowerManager
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import com.example.data.QuickTileId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HardwareAvailability(
    val hasFlashlight: Boolean = false,
    val hasNfc: Boolean = false,
    val hasBluetooth: Boolean = true,
    val hasTelephony: Boolean = true,
    val hasMiShare: Boolean = false,
    val hasLinkToWindows: Boolean = false,
    val hasXiaomiHome: Boolean = false,
    val hasXiaomiCleaner: Boolean = false
)

data class PermissionStatus(
    val overlayGranted: Boolean = false,
    val writeSettingsGranted: Boolean = false,
    val dndAccessGranted: Boolean = false,
    val notificationsGranted: Boolean = true,
    val bluetoothConnectGranted: Boolean = true
)

data class DeviceSystemState(
    val isWifiEnabled: Boolean = false,
    val wifiConnected: Boolean = false,
    val wifiSsid: String? = null,
    val isBluetoothEnabled: Boolean = false,
    val connectedAudioDeviceName: String? = null,
    val isMobileDataActive: Boolean = false,
    val isAirplaneModeOn: Boolean = false,
    val isLocationEnabled: Boolean = false,
    val isNfcEnabled: Boolean = false,
    val isVpnActive: Boolean = false,
    val isAutoRotateOn: Boolean = false,
    val isDarkModeOn: Boolean = true,
    val isFlashlightOn: Boolean = false,
    val isDndOn: Boolean = false,
    val isBatterySaverOn: Boolean = false,
    val ringerMode: Int = AudioManager.RINGER_MODE_NORMAL,
    val brightness: Float = 0.65f,
    val mediaVolume: Float = 0.55f,
    val isMediaPlaying: Boolean = false,
    val audioOutputLabel: String = "Speaker",
    val availability: HardwareAvailability = HardwareAvailability(),
    val deviceModel: String = "${Build.MANUFACTURER} ${Build.MODEL}".trim(),
    val androidVersion: String = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
) {
    fun isTileAvailable(tile: QuickTileId): Boolean = when (tile) {
        QuickTileId.FLASHLIGHT -> availability.hasFlashlight
        QuickTileId.NFC -> availability.hasNfc
        QuickTileId.BLUETOOTH, QuickTileId.BT_AUDIO -> availability.hasBluetooth
        QuickTileId.MOBILE_DATA -> availability.hasTelephony
        QuickTileId.MI_SHARE -> availability.hasMiShare
        QuickTileId.LINK_TO_WINDOWS -> availability.hasLinkToWindows
        QuickTileId.STORAGE_CLEANER -> availability.hasXiaomiCleaner
        else -> true
    }

    fun isTileActive(tile: QuickTileId): Boolean = when (tile) {
        QuickTileId.WIFI -> isWifiEnabled || wifiConnected
        QuickTileId.BLUETOOTH -> isBluetoothEnabled
        QuickTileId.MOBILE_DATA -> isMobileDataActive
        QuickTileId.AIRPLANE_MODE -> isAirplaneModeOn
        QuickTileId.LOCATION -> isLocationEnabled
        QuickTileId.NFC -> isNfcEnabled
        QuickTileId.VPN -> isVpnActive
        QuickTileId.AUTO_ROTATE -> isAutoRotateOn
        QuickTileId.DARK_MODE -> isDarkModeOn
        QuickTileId.FLASHLIGHT -> isFlashlightOn
        QuickTileId.DND -> isDndOn
        QuickTileId.BATTERY_SAVER -> isBatterySaverOn
        QuickTileId.SOUND_MODE -> ringerMode == AudioManager.RINGER_MODE_NORMAL
        QuickTileId.VIBRATE_MODE -> ringerMode == AudioManager.RINGER_MODE_VIBRATE
        QuickTileId.SILENT_MODE -> ringerMode == AudioManager.RINGER_MODE_SILENT
        else -> false
    }
}

sealed class ActionFeedback {
    data object None : ActionFeedback()
    data class Message(val english: String, val arabic: String, val timestamp: Long = System.currentTimeMillis()) : ActionFeedback()
}

class DeviceControlManager(private val context: Context) {

    private val appContext = context.applicationContext
    private val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val cameraManager = appContext.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    private val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    private val wifiManager = appContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
    private val bluetoothManager = appContext.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val locationManager = appContext.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    private val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    private val powerManager = appContext.getSystemService(Context.POWER_SERVICE) as? PowerManager

    private var torchCameraId: String? = null
    private var torchCallbackRegistered = false

    private val _systemState = MutableStateFlow(DeviceSystemState())
    val systemState: StateFlow<DeviceSystemState> = _systemState.asStateFlow()

    private val _permissions = MutableStateFlow(readPermissions())
    val permissions: StateFlow<PermissionStatus> = _permissions.asStateFlow()

    private val _feedback = MutableStateFlow<ActionFeedback>(ActionFeedback.None)
    val feedback: StateFlow<ActionFeedback> = _feedback.asStateFlow()

    private val torchCallback = object : CameraManager.TorchCallback() {
        override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
            if (cameraId == torchCameraId || torchCameraId == null) {
                _systemState.update { it.copy(isFlashlightOn = enabled) }
            }
        }
    }

    init {
        torchCameraId = findFlashCameraId()
        refreshAll()
    }

    fun startObservingHardware() {
        val cm = cameraManager
        if (!torchCallbackRegistered && cm != null) {
            runCatching {
                cm.registerTorchCallback(torchCallback, null)
                torchCallbackRegistered = true
            }
        }
        refreshAll()
    }

    fun stopObservingHardware() {
        val cm = cameraManager
        if (torchCallbackRegistered && cm != null) {
            runCatching {
                cm.unregisterTorchCallback(torchCallback)
            }
            torchCallbackRegistered = false
        }
    }

    fun clearFeedback() {
        _feedback.value = ActionFeedback.None
    }

    fun performHaptic(enabled: Boolean) {
        if (!enabled) return
        runCatching {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(14L, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(14L)
                }
            }
        }
    }

    fun readPermissions(): PermissionStatus {
        val overlay = Settings.canDrawOverlays(appContext)
        val writeSettings = Settings.System.canWrite(appContext)
        val dnd = notificationManager?.isNotificationPolicyAccessGranted == true
        val notif = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
        } else true
        val btConnect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.BLUETOOTH_CONNECT) ==
                PackageManager.PERMISSION_GRANTED
        } else true
        return PermissionStatus(
            overlayGranted = overlay,
            writeSettingsGranted = writeSettings,
            dndAccessGranted = dnd,
            notificationsGranted = notif,
            bluetoothConnectGranted = btConnect
        )
    }

    fun refreshAll() {
        _permissions.value = readPermissions()
        val availability = detectHardwareAvailability()

        val activeNetwork = connectivityManager?.activeNetwork
        val caps = connectivityManager?.getNetworkCapabilities(activeNetwork)
        val wifiConnected = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        val mobileActive = caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
        val vpnActive = caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true

        val wifiEnabled = runCatching { wifiManager?.isWifiEnabled == true }.getOrDefault(wifiConnected)
        val wifiSsid = readSafeWifiSsid(wifiConnected)

        val btEnabled = runCatching { bluetoothManager?.adapter?.isEnabled == true }.getOrDefault(false)
        val (connectedAudioName, outputLabel) = readActiveAudioOutput()

        val airplaneOn = runCatching {
            Settings.Global.getInt(appContext.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) == 1
        }.getOrDefault(false)

        val locationOn = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                locationManager?.isLocationEnabled == true
            } else {
                @Suppress("DEPRECATION")
                Settings.Secure.getInt(appContext.contentResolver, Settings.Secure.LOCATION_MODE, 0) != 0
            }
        }.getOrDefault(false)

        val nfcEnabled = runCatching {
            NfcAdapter.getDefaultAdapter(appContext)?.isEnabled == true
        }.getOrDefault(false)

        val autoRotate = runCatching {
            Settings.System.getInt(appContext.contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0) == 1
        }.getOrDefault(false)

        val darkMode = (appContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES

        val dndOn = runCatching {
            val filter = notificationManager?.currentInterruptionFilter ?: NotificationManager.INTERRUPTION_FILTER_ALL
            filter != NotificationManager.INTERRUPTION_FILTER_ALL &&
                filter != NotificationManager.INTERRUPTION_FILTER_UNKNOWN
        }.getOrDefault(false)

        val batterySaver = runCatching { powerManager?.isPowerSaveMode == true }.getOrDefault(false)
        val ringerMode = runCatching { audioManager.ringerMode }.getOrDefault(AudioManager.RINGER_MODE_NORMAL)

        val brightnessRaw = runCatching {
            Settings.System.getInt(appContext.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 165)
        }.getOrDefault(165)
        val brightnessFraction = (brightnessRaw.coerceIn(0, 255) / 255f)

        val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).coerceIn(0, maxVol)
        val volumeFraction = curVol.toFloat() / maxVol.toFloat()

        val isPlaying = runCatching { audioManager.isMusicActive }.getOrDefault(false)

        _systemState.update { current ->
            current.copy(
                isWifiEnabled = wifiEnabled,
                wifiConnected = wifiConnected,
                wifiSsid = wifiSsid,
                isBluetoothEnabled = btEnabled,
                connectedAudioDeviceName = connectedAudioName,
                isMobileDataActive = mobileActive,
                isAirplaneModeOn = airplaneOn,
                isLocationEnabled = locationOn,
                isNfcEnabled = nfcEnabled,
                isVpnActive = vpnActive,
                isAutoRotateOn = autoRotate,
                isDarkModeOn = darkMode,
                isDndOn = dndOn,
                isBatterySaverOn = batterySaver,
                ringerMode = ringerMode,
                brightness = brightnessFraction,
                mediaVolume = volumeFraction,
                isMediaPlaying = isPlaying,
                audioOutputLabel = outputLabel,
                availability = availability
            )
        }
    }

    private fun readSafeWifiSsid(wifiConnected: Boolean): String? {
        if (!wifiConnected) return null
        return runCatching {
            @Suppress("DEPRECATION")
            val raw = wifiManager?.connectionInfo?.ssid?.removeSurrounding("\"")
            if (raw.isNullOrBlank() || raw == "<unknown ssid>") null else raw
        }.getOrNull()
    }

    private fun readActiveAudioOutput(): Pair<String?, String> {
        return runCatching {
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            val btDevice = devices.firstOrNull { device ->
                device.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                    device.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO ||
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                        (device.type == AudioDeviceInfo.TYPE_BLE_HEADSET ||
                            device.type == AudioDeviceInfo.TYPE_BLE_SPEAKER))
            }
            if (btDevice != null) {
                val name = btDevice.productName?.toString()?.takeIf { it.isNotBlank() } ?: "Bluetooth Audio"
                return@runCatching Pair(name, name)
            }
            val wired = devices.firstOrNull {
                it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET ||
                    it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
                    it.type == AudioDeviceInfo.TYPE_USB_HEADSET
            }
            if (wired != null) {
                val name = wired.productName?.toString()?.takeIf { it.isNotBlank() } ?: "Wired Headset"
                return@runCatching Pair(name, name)
            }
            Pair(null, "Phone Speaker")
        }.getOrDefault(Pair(null, "Phone Speaker"))
    }

    private fun detectHardwareAvailability(): HardwareAvailability {
        val pm = appContext.packageManager
        return HardwareAvailability(
            hasFlashlight = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) && torchCameraId != null,
            hasNfc = pm.hasSystemFeature(PackageManager.FEATURE_NFC) && NfcAdapter.getDefaultAdapter(appContext) != null,
            hasBluetooth = pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH),
            hasTelephony = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY),
            hasMiShare = isPackageInstalled("com.miui.mishare.connectivity"),
            hasLinkToWindows = isPackageInstalled("com.microsoft.appmanager"),
            hasXiaomiHome = isPackageInstalled("com.xiaomi.smarthome"),
            hasXiaomiCleaner = isPackageInstalled("com.miui.securitycenter")
        )
    }

    private fun isPackageInstalled(packageName: String): Boolean {
        return runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                appContext.packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                appContext.packageManager.getPackageInfo(packageName, 0)
            }
            true
        }.getOrDefault(false)
    }

    private fun findFlashCameraId(): String? {
        return runCatching {
            cameraManager?.cameraIdList?.firstOrNull { id ->
                val chars = cameraManager.getCameraCharacteristics(id)
                val hasFlash = chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                val isBack = chars.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
                hasFlash && isBack
            } ?: cameraManager?.cameraIdList?.firstOrNull { id ->
                cameraManager.getCameraCharacteristics(id)
                    .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
        }.getOrNull()
    }

    fun setSystemBrightness(fraction: Float) {
        val clamped = fraction.coerceIn(0.02f, 1f)
        _systemState.update { it.copy(brightness = clamped) }
        if (Settings.System.canWrite(appContext)) {
            runCatching {
                Settings.System.putInt(
                    appContext.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
                )
                Settings.System.putInt(
                    appContext.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS,
                    (clamped * 255f).toInt().coerceIn(5, 255)
                )
            }
        }
    }

    fun promptBrightnessPermissionIfNeeded(): Boolean {
        if (!Settings.System.canWrite(appContext)) {
            emitFeedback(
                "Grant 'Modify System Settings' to adjust real screen brightness.",
                "امنح صلاحية تعديل إعدادات النظام لضبط سطوع الشاشة الفعلي."
            )
            openWriteSettingsPermission()
            return true
        }
        return false
    }

    fun setMediaVolume(fraction: Float) {
        val clamped = fraction.coerceIn(0f, 1f)
        _systemState.update { it.copy(mediaVolume = clamped) }
        runCatching {
            val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
            val target = (clamped * maxVol).toInt().coerceIn(0, maxVol)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
        }
    }

    fun dispatchMediaKey(keyCode: Int) {
        runCatching {
            val eventTime = SystemClock.uptimeMillis()
            audioManager.dispatchMediaKeyEvent(KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0))
            audioManager.dispatchMediaKeyEvent(KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, keyCode, 0))
        }
        refreshAll()
    }

    fun openMusicPlayer() {
        val musicIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_MUSIC)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (!launchSafeIntent(musicIntent)) {
            openVolumePanel()
        }
    }

    fun openVolumePanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!launchSafeIntent(Intent(Settings.Panel.ACTION_VOLUME))) {
                launchSafeIntent(Intent(Settings.ACTION_SOUND_SETTINGS))
            }
        } else {
            launchSafeIntent(Intent(Settings.ACTION_SOUND_SETTINGS))
        }
    }

    fun openDisplaySettings() {
        launchSafeIntent(Intent(Settings.ACTION_DISPLAY_SETTINGS))
    }

    fun openBatterySettings() {
        if (!launchSafeIntent(Intent(Intent.ACTION_POWER_USAGE_SUMMARY))) {
            launchSafeIntent(Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS))
        }
    }

    fun openSystemSettings() {
        launchSafeIntent(Intent(Settings.ACTION_SETTINGS))
    }

    fun openOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${appContext.packageName}")
        )
        launchSafeIntent(intent)
    }

    fun openWriteSettingsPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_WRITE_SETTINGS,
            Uri.parse("package:${appContext.packageName}")
        )
        launchSafeIntent(intent)
    }

    fun openDndAccessPermission() {
        launchSafeIntent(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
    }

    fun openAppNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, appContext.packageName)
        }
        if (!launchSafeIntent(intent)) {
            val details = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:${appContext.packageName}")
            )
            launchSafeIntent(details)
        }
    }

    fun executeTileAction(tile: QuickTileId): Boolean {
        val state = _systemState.value
        if (!state.isTileAvailable(tile)) {
            emitFeedback(
                "${tile.name.replace('_', ' ')} is unavailable on this device.",
                "هذه الميزة غير متوفرة أو غير مثبتة على هذا الجهاز."
            )
            return false
        }

        var closesOverlayPanel = true
        when (tile) {
            QuickTileId.WIFI -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (!launchSafeIntent(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))) {
                        launchSafeIntent(Intent(Settings.ACTION_WIFI_SETTINGS))
                    }
                } else {
                    launchSafeIntent(Intent(Settings.ACTION_WIFI_SETTINGS))
                }
            }
            QuickTileId.BLUETOOTH, QuickTileId.CONNECTED_DEVICES -> {
                launchSafeIntent(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
            }
            QuickTileId.MOBILE_DATA -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (!launchSafeIntent(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))) {
                        launchSafeIntent(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
                    }
                } else {
                    launchSafeIntent(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
                }
            }
            QuickTileId.AIRPLANE_MODE -> {
                launchSafeIntent(Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS))
            }
            QuickTileId.HOTSPOT -> {
                val tetherIntent = Intent().apply {
                    component = ComponentName("com.android.settings", "com.android.settings.TetherSettings")
                }
                if (!launchSafeIntent(tetherIntent)) {
                    launchSafeIntent(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                }
            }
            QuickTileId.LOCATION -> {
                launchSafeIntent(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            QuickTileId.NFC -> {
                launchSafeIntent(Intent(Settings.ACTION_NFC_SETTINGS))
            }
            QuickTileId.VPN -> {
                launchSafeIntent(Intent(Settings.ACTION_VPN_SETTINGS))
            }
            QuickTileId.AUTO_ROTATE -> {
                if (Settings.System.canWrite(appContext)) {
                    val next = if (state.isAutoRotateOn) 0 else 1
                    runCatching {
                        Settings.System.putInt(appContext.contentResolver, Settings.System.ACCELEROMETER_ROTATION, next)
                    }
                    refreshAll()
                    closesOverlayPanel = false
                } else {
                    emitFeedback(
                        "Opening Display Settings — grant Modify System Settings for 1-tap toggle.",
                        "فتح إعدادات الشاشة — امنح صلاحية تعديل الإعدادات للتبديل المباشر."
                    )
                    launchSafeIntent(Intent(Settings.ACTION_DISPLAY_SETTINGS))
                }
            }
            QuickTileId.FLASHLIGHT -> {
                val id = torchCameraId
                val cm = cameraManager
                if (id != null && cm != null) {
                    val next = !state.isFlashlightOn
                    val toggled = runCatching {
                        cm.setTorchMode(id, next)
                        true
                    }.getOrDefault(false)
                    if (toggled) {
                        _systemState.update { it.copy(isFlashlightOn = next) }
                    } else {
                        emitFeedback(
                            "Camera flash is currently in use by another app.",
                            "فلاش الكاميرا مستخدم حالياً بواسطة تطبيق آخر."
                        )
                    }
                    closesOverlayPanel = false
                }
            }
            QuickTileId.DND -> {
                val nm = notificationManager
                if (nm?.isNotificationPolicyAccessGranted == true) {
                    val nextFilter = if (state.isDndOn) {
                        NotificationManager.INTERRUPTION_FILTER_ALL
                    } else {
                        NotificationManager.INTERRUPTION_FILTER_PRIORITY
                    }
                    runCatching { nm.setInterruptionFilter(nextFilter) }
                    refreshAll()
                    closesOverlayPanel = false
                } else {
                    emitFeedback(
                        "Grant Do Not Disturb access for instant 1-tap toggling.",
                        "امنح صلاحية عدم الإزعاج للتبديل الفوري بضغطة واحدة."
                    )
                    openDndAccessPermission()
                }
            }
            QuickTileId.BATTERY_SAVER -> {
                launchSafeIntent(Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS))
            }
            QuickTileId.DARK_MODE, QuickTileId.SCREEN_TIMEOUT -> {
                launchSafeIntent(Intent(Settings.ACTION_DISPLAY_SETTINGS))
            }
            QuickTileId.READING_MODE, QuickTileId.EYE_COMFORT -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!launchSafeIntent(Intent(Settings.ACTION_NIGHT_DISPLAY_SETTINGS))) {
                        launchSafeIntent(Intent(Settings.ACTION_DISPLAY_SETTINGS))
                    }
                } else {
                    launchSafeIntent(Intent(Settings.ACTION_DISPLAY_SETTINGS))
                }
            }
            QuickTileId.SCREENSHOT -> {
                emitFeedback(
                    "Android 14 requires hardware keys (Power + Vol Down) or SystemUI for screenshots without root.",
                    "يتطلب أندرويد 14 أزرار الجهاز (الطاقة + خفض الصوت) أو شريط النظام لالتقاط الشاشة بدون روت."
                )
                closesOverlayPanel = false
            }
            QuickTileId.SCREEN_RECORDING -> {
                emitFeedback(
                    "Use Xiaomi SystemUI Screen Recorder or Display shortcut — Android 14 restricts background capture without confirmation.",
                    "استخدم مسجل الشاشة المدمج في HyperOS — يقيّد أندرويد 14 تسجيل الشاشة التلقائي بدون موافقة النظام."
                )
                closesOverlayPanel = false
            }
            QuickTileId.PERFORMANCE_MODE -> {
                openBatterySettings()
            }
            QuickTileId.SYNC_SETTINGS -> {
                launchSafeIntent(Intent(Settings.ACTION_SYNC_SETTINGS))
            }
            QuickTileId.MI_SHARE -> {
                launchPackageOrFallback("com.miui.mishare.connectivity", Settings.ACTION_WIRELESS_SETTINGS)
            }
            QuickTileId.LINK_TO_WINDOWS -> {
                launchPackageOrFallback("com.microsoft.appmanager", Settings.ACTION_CAST_SETTINGS)
            }
            QuickTileId.CAST_WIRELESS -> {
                if (!launchSafeIntent(Intent(Settings.ACTION_CAST_SETTINGS))) {
                    launchSafeIntent(Intent(Settings.ACTION_DISPLAY_SETTINGS))
                }
            }
            QuickTileId.SMART_DEVICES, QuickTileId.HOME_CONTROL -> {
                if (state.availability.hasXiaomiHome) {
                    launchPackageOrFallback("com.xiaomi.smarthome", Settings.ACTION_CAST_SETTINGS)
                } else {
                    if (!launchSafeIntent(Intent(Settings.ACTION_CAST_SETTINGS))) {
                        launchSafeIntent(Intent(Settings.ACTION_SETTINGS))
                    }
                }
            }
            QuickTileId.BT_AUDIO -> {
                openVolumePanel()
            }
            QuickTileId.DEVICE_MANAGEMENT -> {
                if (!launchSafeIntent(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS))) {
                    launchSafeIntent(Intent(Settings.ACTION_SETTINGS))
                }
            }
            QuickTileId.STORAGE_CLEANER -> {
                if (state.availability.hasXiaomiCleaner) {
                    launchPackageOrFallback("com.miui.securitycenter", Settings.ACTION_INTERNAL_STORAGE_SETTINGS)
                } else {
                    launchSafeIntent(Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS))
                }
            }
            QuickTileId.NEARBY_SHARE -> {
                val quickShare = Intent("com.google.android.gms.SHARE_NEARBY")
                if (!launchSafeIntent(quickShare)) {
                    launchSafeIntent(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                }
            }
            QuickTileId.SOUND_MODE -> {
                setRingerModeSafe(AudioManager.RINGER_MODE_NORMAL)
                closesOverlayPanel = false
            }
            QuickTileId.VIBRATE_MODE -> {
                setRingerModeSafe(AudioManager.RINGER_MODE_VIBRATE)
                closesOverlayPanel = false
            }
            QuickTileId.SILENT_MODE -> {
                setRingerModeSafe(AudioManager.RINGER_MODE_SILENT)
                closesOverlayPanel = false
            }
            QuickTileId.APP_SETTINGS -> {
                openSystemSettings()
            }
        }
        return closesOverlayPanel
    }

    private fun setRingerModeSafe(targetMode: Int) {
        if (targetMode == AudioManager.RINGER_MODE_SILENT &&
            notificationManager?.isNotificationPolicyAccessGranted != true
        ) {
            emitFeedback(
                "Grant Do Not Disturb access to switch directly to Silent mode.",
                "امنح صلاحية عدم الإزعاج للتبديل المباشر إلى الوضع الصامت."
            )
            openDndAccessPermission()
            return
        }
        runCatching {
            audioManager.ringerMode = targetMode
        }.onFailure {
            openVolumePanel()
        }
        refreshAll()
    }

    private fun launchPackageOrFallback(packageName: String, fallbackAction: String) {
        val launchIntent = appContext.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null && launchSafeIntent(launchIntent)) {
            return
        }
        launchSafeIntent(Intent(fallbackAction))
    }

    private fun launchSafeIntent(intent: Intent): Boolean {
        return try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            appContext.startActivity(intent)
            true
        } catch (_: ActivityNotFoundException) {
            false
        } catch (_: SecurityException) {
            false
        }
    }

    private fun emitFeedback(english: String, arabic: String) {
        _feedback.value = ActionFeedback.Message(english = english, arabic = arabic)
    }
}
