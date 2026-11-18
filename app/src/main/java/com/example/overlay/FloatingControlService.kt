package com.example.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.MainActivity
import com.example.R
import com.example.data.ControlCenterConfig
import com.example.data.LocalizationProvider
import com.example.data.PreferencesRepository
import com.example.data.ResolvedVisualPalette
import com.example.system.DeviceControlManager
import com.example.ui.components.FloatingControlButton
import com.example.ui.components.TopAnchoredControlCenterOverlay
import com.example.ui.theme.OneXControlTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.abs

class FloatingControlService :
    Service(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    companion object {
        private const val CHANNEL_ID = "one_x_floating_channel"
        private const val NOTIFICATION_ID = 1042
        const val ACTION_OPEN_PANEL = "com.onex.control.ACTION_OPEN_PANEL"
        const val ACTION_STOP_SERVICE = "com.onex.control.ACTION_STOP_SERVICE"

        private val _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()
    }

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val store = ViewModelStore()

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore: ViewModelStore get() = store

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private lateinit var windowManager: WindowManager
    private lateinit var repository: PreferencesRepository
    private lateinit var deviceManager: DeviceControlManager

    private var floatingView: ComposeView? = null
    private var floatingParams: WindowManager.LayoutParams? = null
    private var panelView: ComposeView? = null

    private val configState = MutableStateFlow(ControlCenterConfig())
    private val panelVisibleState = MutableStateFlow(false)
    private val panelExpandedState = MutableStateFlow(false)

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        repository = PreferencesRepository(applicationContext)
        deviceManager = DeviceControlManager(applicationContext)

        startForegroundSafely()
        _isRunning.value = true

        serviceScope.launch {
            repository.configFlow.collect { cfg ->
                configState.value = cfg
                syncFloatingButtonVisibility(cfg)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP_SERVICE -> {
                serviceScope.launch {
                    repository.update { it.copy(floatingButtonEnabled = false) }
                    stopSelf()
                }
                return START_NOT_STICKY
            }
            ACTION_OPEN_PANEL -> {
                if (Settings.canDrawOverlays(this)) {
                    openTopControlCenter()
                }
            }
        }

        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return START_NOT_STICKY
        }

        serviceScope.launch {
            val cfg = repository.configFlow.first()
            configState.value = cfg
            syncFloatingButtonVisibility(cfg)
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startForegroundSafely() {
        createNotificationChannel()
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = getString(R.string.notification_channel_desc)
                setShowBadge(false)
            }
            nm.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val contentPi = PendingIntent.getActivity(
            this, 0, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val openPanelIntent = Intent(this, FloatingControlService::class.java).apply {
            action = ACTION_OPEN_PANEL
        }
        val openPanelPi = PendingIntent.getService(
            this, 1, openPanelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, FloatingControlService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }
        val stopPi = PendingIntent.getService(
            this, 2, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setContentIntent(contentPi)
            .addAction(0, getString(R.string.notification_action_open), openPanelPi)
            .addAction(0, getString(R.string.notification_action_stop), stopPi)
            .build()
    }

    private fun syncFloatingButtonVisibility(cfg: ControlCenterConfig) {
        if (!Settings.canDrawOverlays(this)) {
            removeFloatingButton()
            return
        }
        val shouldShow = cfg.floatingButtonEnabled &&
            cfg.floatingButtonVisible &&
            !cfg.floatingButtonTemporarilyHidden &&
            !panelVisibleState.value

        if (shouldShow) {
            if (floatingView == null) {
                attachFloatingButton(cfg)
            } else {
                updateFloatingButtonLayout(cfg)
            }
        } else {
            removeFloatingButton()
        }
    }

    private fun overlayWindowType(): Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    } else {
        @Suppress("DEPRECATION")
        WindowManager.LayoutParams.TYPE_PHONE
    }

    private fun attachFloatingButton(cfg: ControlCenterConfig) {
        if (floatingView != null) return
        val dm = resources.displayMetrics
        val btnPx = (cfg.floatingButtonSizeDp * dm.density).toInt()
        val marginPx = (10 * dm.density).toInt()
        val initialX = if (cfg.floatingButtonEdgeRight) dm.widthPixels - btnPx - marginPx else marginPx
        val initialY = (dm.heightPixels * cfg.floatingButtonYFraction).toInt()
            .coerceIn((48 * dm.density).toInt(), (dm.heightPixels * 0.85f).toInt())

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayWindowType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = initialX
            y = initialY
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }

        val view = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@FloatingControlService)
            setViewTreeViewModelStoreOwner(this@FloatingControlService)
            setViewTreeSavedStateRegistryOwner(this@FloatingControlService)
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val liveConfig by configState.collectAsState()
                OneXControlTheme(config = liveConfig) {
                    val palette = ResolvedVisualPalette.fromConfig(liveConfig)
                    val strings = LocalizationProvider.resolve(liveConfig.language)
                    FloatingControlButton(
                        config = liveConfig,
                        palette = palette,
                        contentDescription = strings.pullDownControlCenter,
                        onClick = { openTopControlCenter() }
                    )
                }
            }
        }

        // Smooth edge-snapping touch drag listener
        var downRawX = 0f
        var downRawY = 0f
        var startX = 0
        var startY = 0
        var moved = false
        val touchSlop = (10 * dm.density)

        view.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    downRawX = event.rawX
                    downRawY = event.rawY
                    startX = params.x
                    startY = params.y
                    moved = false
                    false
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - downRawX
                    val dy = event.rawY - downRawY
                    if (abs(dx) > touchSlop || abs(dy) > touchSlop) {
                        moved = true
                    }
                    if (moved) {
                        params.x = (startX + dx).toInt().coerceIn(0, dm.widthPixels - v.width)
                        params.y = (startY + dy).toInt()
                            .coerceIn((36 * dm.density).toInt(), (dm.heightPixels * 0.88f).toInt())
                        runCatching { windowManager.updateViewLayout(v, params) }
                        true
                    } else {
                        false
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (moved) {
                        val centerX = params.x + v.width / 2
                        val snapRight = centerX >= dm.widthPixels / 2
                        params.x = if (snapRight) dm.widthPixels - v.width - marginPx else marginPx
                        runCatching { windowManager.updateViewLayout(v, params) }
                        val yFrac = (params.y.toFloat() / dm.heightPixels.toFloat()).coerceIn(0.08f, 0.86f)
                        serviceScope.launch {
                            repository.updateFloatingPosition(snapRight, yFrac)
                        }
                        true
                    } else {
                        false
                    }
                }
                else -> false
            }
        }

        runCatching {
            windowManager.addView(view, params)
            floatingView = view
            floatingParams = params
        }
    }

    private fun updateFloatingButtonLayout(cfg: ControlCenterConfig) {
        val view = floatingView ?: return
        val params = floatingParams ?: return
        val dm = resources.displayMetrics
        val btnPx = (cfg.floatingButtonSizeDp * dm.density).toInt()
        val marginPx = (10 * dm.density).toInt()
        params.x = if (cfg.floatingButtonEdgeRight) dm.widthPixels - btnPx - marginPx else marginPx
        params.y = (dm.heightPixels * cfg.floatingButtonYFraction).toInt()
            .coerceIn((40 * dm.density).toInt(), (dm.heightPixels * 0.86f).toInt())
        runCatching { windowManager.updateViewLayout(view, params) }
    }

    private fun removeFloatingButton() {
        floatingView?.let { v ->
            runCatching { windowManager.removeView(v) }
        }
        floatingView = null
        floatingParams = null
    }

    private fun openTopControlCenter() {
        if (panelView != null) return
        if (!Settings.canDrawOverlays(this)) return

        val cfg = configState.value
        deviceManager.startObservingHardware()
        deviceManager.performHaptic(cfg.hapticsEnabled)

        panelExpandedState.value = if (cfg.rememberLastExpandedState) cfg.lastWasExpanded else false
        removeFloatingButton()

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            overlayWindowType(),
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.FILL_HORIZONTAL
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }

        val view = ComposeView(this).apply {
            isFocusableInTouchMode = true
            setViewTreeLifecycleOwner(this@FloatingControlService)
            setViewTreeViewModelStoreOwner(this@FloatingControlService)
            setViewTreeSavedStateRegistryOwner(this@FloatingControlService)
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    closeTopControlCenter()
                    true
                } else {
                    false
                }
            }

            setContent {
                val liveConfig by configState.collectAsState()
                val visible by panelVisibleState.collectAsState()
                val expanded by panelExpandedState.collectAsState()
                val sysState by deviceManager.systemState.collectAsState()

                OneXControlTheme(config = liveConfig) {
                    val palette = ResolvedVisualPalette.fromConfig(liveConfig)
                    val strings = LocalizationProvider.resolve(liveConfig.language)

                    TopAnchoredControlCenterOverlay(
                        visible = visible,
                        expanded = expanded,
                        config = liveConfig,
                        palette = palette,
                        strings = strings,
                        systemState = sysState,
                        onExpandedChange = { next ->
                            panelExpandedState.value = next
                            if (liveConfig.rememberLastExpandedState) {
                                serviceScope.launch {
                                    repository.update { it.copy(lastWasExpanded = next) }
                                }
                            }
                        },
                        onDismiss = { closeTopControlCenter() },
                        onTileClick = { tile ->
                            deviceManager.performHaptic(liveConfig.hapticsEnabled)
                            val openedExternal = deviceManager.executeTileAction(tile)
                            if (openedExternal) closeTopControlCenter()
                        },
                        onBrightnessChange = { deviceManager.setSystemBrightness(it) },
                        onBrightnessChangeFinished = {
                            if (deviceManager.promptBrightnessPermissionIfNeeded()) {
                                closeTopControlCenter()
                            }
                        },
                        onVolumeChange = { deviceManager.setMediaVolume(it) },
                        onMediaKey = { deviceManager.dispatchMediaKey(it) },
                        onOpenMusicApp = {
                            deviceManager.openMusicPlayer()
                            closeTopControlCenter()
                        },
                        onOpenVolumePanel = {
                            deviceManager.openVolumePanel()
                            closeTopControlCenter()
                        },
                        onOpenDisplaySettings = {
                            deviceManager.openDisplaySettings()
                            closeTopControlCenter()
                        },
                        onOpenBatterySettings = {
                            deviceManager.openBatterySettings()
                            closeTopControlCenter()
                        },
                        onOpenDeviceSettings = {
                            deviceManager.openSystemSettings()
                            closeTopControlCenter()
                        },
                        onOpenAppSettings = { launchMainApp("settings") },
                        onOpenCustomize = { launchMainApp("studio") }
                    )
                }
            }
        }

        runCatching {
            windowManager.addView(view, params)
            panelView = view
            view.requestFocus()
            panelVisibleState.value = true
        }.onFailure {
            syncFloatingButtonVisibility(configState.value)
        }
    }

    private fun closeTopControlCenter() {
        if (!panelVisibleState.value && panelView == null) return
        panelVisibleState.value = false
        val closeMs = configState.value.closeSpeedMs.toLong().coerceIn(140L, 450L)
        serviceScope.launch {
            delay(closeMs + 40L)
            removePanelOverlay()
            deviceManager.stopObservingHardware()
            syncFloatingButtonVisibility(configState.value)
        }
    }

    private fun removePanelOverlay() {
        panelView?.let { v ->
            runCatching { windowManager.removeView(v) }
        }
        panelView = null
    }

    private fun launchMainApp(route: String) {
        closeTopControlCenter()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("start_route", route)
        }
        runCatching { startActivity(intent) }
    }

    override fun onDestroy() {
        _isRunning.value = false
        removePanelOverlay()
        removeFloatingButton()
        deviceManager.stopObservingHardware()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
        serviceScope.cancel()
        super.onDestroy()
    }
}
