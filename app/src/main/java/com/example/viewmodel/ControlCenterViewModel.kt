package com.example.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppLanguage
import com.example.data.ControlCenterConfig
import com.example.data.IconSizeOption
import com.example.data.PreferencesRepository
import com.example.data.QuickTileId
import com.example.data.SliderPosition
import com.example.data.SliderStyle
import com.example.data.ThemePreset
import com.example.data.TileAlignment
import com.example.overlay.FloatingControlService
import com.example.system.ActionFeedback
import com.example.system.DeviceControlManager
import com.example.system.DeviceSystemState
import com.example.system.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ControlCenterViewModel(application: Application) : AndroidViewModel(application) {

    private val appContext = application.applicationContext
    val repository = PreferencesRepository(appContext)
    val deviceManager = DeviceControlManager(appContext)

    val config: StateFlow<ControlCenterConfig> = repository.configFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ControlCenterConfig()
    )

    val systemState: StateFlow<DeviceSystemState> = deviceManager.systemState
    val permissions: StateFlow<PermissionStatus> = deviceManager.permissions
    val feedback: StateFlow<ActionFeedback> = deviceManager.feedback

    private val _isInAppPanelOpen = MutableStateFlow(false)
    val isInAppPanelOpen: StateFlow<Boolean> = _isInAppPanelOpen.asStateFlow()

    private val _isInAppPanelExpanded = MutableStateFlow(false)
    val isInAppPanelExpanded: StateFlow<Boolean> = _isInAppPanelExpanded.asStateFlow()

    val isOverlayServiceRunning: StateFlow<Boolean> = FloatingControlService.isRunning

    fun onAppResumed() {
        deviceManager.startObservingHardware()
    }

    fun onAppPaused() {
        deviceManager.stopObservingHardware()
    }

    fun openInAppControlCenter(startExpanded: Boolean? = null) {
        deviceManager.refreshAll()
        val cfg = config.value
        deviceManager.performHaptic(cfg.hapticsEnabled)
        val expanded = startExpanded ?: if (cfg.rememberLastExpandedState) cfg.lastWasExpanded else false
        _isInAppPanelExpanded.value = expanded
        _isInAppPanelOpen.value = true
    }

    fun closeInAppControlCenter() {
        _isInAppPanelOpen.value = false
    }

    fun setInAppPanelExpanded(expanded: Boolean) {
        _isInAppPanelExpanded.value = expanded
        if (config.value.rememberLastExpandedState) {
            updateConfig { it.copy(lastWasExpanded = expanded) }
        }
    }

    fun updateConfig(transform: (ControlCenterConfig) -> ControlCenterConfig) {
        viewModelScope.launch {
            repository.update(transform)
        }
    }

    fun setInitialVisibleTiles(count: Int) = updateConfig {
        deviceManager.performHaptic(it.hapticsEnabled)
        it.copy(initialVisibleTiles = count.coerceIn(4, 12))
    }

    fun setGridColumns(columns: Int) = updateConfig {
        deviceManager.performHaptic(it.hapticsEnabled)
        it.copy(gridColumns = columns.coerceIn(3, 6))
    }

    fun setThemePreset(preset: ThemePreset) = updateConfig {
        deviceManager.performHaptic(it.hapticsEnabled)
        it.copy(themePreset = preset)
    }

    fun setIconSize(size: IconSizeOption) = updateConfig {
        deviceManager.performHaptic(it.hapticsEnabled)
        it.copy(iconSize = size)
    }

    fun setLanguage(language: AppLanguage) = updateConfig {
        it.copy(language = language)
    }

    fun setSliderPosition(pos: SliderPosition) = updateConfig { it.copy(sliderPosition = pos) }
    fun setSliderStyle(style: SliderStyle) = updateConfig { it.copy(sliderStyle = style) }
    fun setMediaPosition(pos: SliderPosition) = updateConfig { it.copy(mediaPlayerPosition = pos) }
    fun setIconAlignment(align: TileAlignment) = updateConfig { it.copy(iconAlignment = align) }

    fun toggleTileEnabled(tile: QuickTileId) = updateConfig { current ->
        val list = current.activeTileIds.toMutableList()
        if (list.contains(tile)) {
            if (list.size > 4) list.remove(tile)
        } else {
            list.add(tile)
        }
        current.copy(activeTileIds = list)
    }

    fun toggleFavoriteTile(tile: QuickTileId) = updateConfig { current ->
        val favs = current.favoriteTileIds.toMutableSet()
        if (!favs.add(tile)) favs.remove(tile)
        current.copy(favoriteTileIds = favs)
    }

    fun moveTile(fromIndex: Int, toIndex: Int) = updateConfig { current ->
        val list = current.activeTileIds.toMutableList()
        if (fromIndex in list.indices && toIndex in list.indices && fromIndex != toIndex) {
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            deviceManager.performHaptic(current.hapticsEnabled)
        }
        current.copy(activeTileIds = list)
    }

    fun setFloatingPosition(edgeRight: Boolean, yFraction: Float) {
        viewModelScope.launch {
            repository.updateFloatingPosition(edgeRight, yFraction)
        }
    }

    fun resetFloatingPosition() {
        setFloatingPosition(edgeRight = true, yFraction = 0.34f)
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            repository.resetToDefaults()
            deviceManager.performHaptic(true)
        }
    }

    fun toggleSystemFloatingOverlay(enable: Boolean): Boolean {
        if (enable && !Settings.canDrawOverlays(appContext)) {
            deviceManager.openOverlayPermission()
            return false
        }
        updateConfig { it.copy(floatingButtonEnabled = enable, floatingButtonTemporarilyHidden = false) }
        val serviceIntent = Intent(appContext, FloatingControlService::class.java)
        if (enable) {
            runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    appContext.startForegroundService(serviceIntent)
                } else {
                    appContext.startService(serviceIntent)
                }
            }
        } else {
            runCatching {
                appContext.stopService(serviceIntent)
            }
        }
        return true
    }

    fun onTileTapped(tile: QuickTileId, closeInAppIfExternal: Boolean = true) {
        deviceManager.performHaptic(config.value.hapticsEnabled)
        val openedExternal = deviceManager.executeTileAction(tile)
        if (openedExternal && closeInAppIfExternal) {
            _isInAppPanelOpen.value = false
        }
    }

    fun onBrightnessChanged(value: Float) {
        deviceManager.setSystemBrightness(value)
    }

    fun onBrightnessChangeFinished() {
        deviceManager.promptBrightnessPermissionIfNeeded()
    }

    fun onVolumeChanged(value: Float) {
        deviceManager.setMediaVolume(value)
    }

    suspend fun exportBackupJson(): String = repository.exportConfigJson()

    suspend fun importBackupJson(raw: String): Result<Unit> = repository.importConfigJson(raw)

    override fun onCleared() {
        deviceManager.stopObservingHardware()
        super.onCleared()
    }
}
