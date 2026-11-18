package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

val Context.oneXDataStore: DataStore<Preferences> by preferencesDataStore(name = "one_x_control_prefs")

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val INITIAL_VISIBLE_TILES = intPreferencesKey("initial_visible_tiles")
        val GRID_COLUMNS = intPreferencesKey("grid_columns")
        val ACTIVE_TILE_IDS = stringPreferencesKey("active_tile_ids")
        val FAVORITE_TILE_IDS = stringPreferencesKey("favorite_tile_ids")
        val HIDE_UNAVAILABLE_TILES = booleanPreferencesKey("hide_unavailable_tiles")
        val REMEMBER_LAST_STATE = booleanPreferencesKey("remember_last_state")
        val LAST_WAS_EXPANDED = booleanPreferencesKey("last_was_expanded")

        val SHOW_BRIGHTNESS = booleanPreferencesKey("show_brightness")
        val SHOW_VOLUME = booleanPreferencesKey("show_volume")
        val SLIDER_THICKNESS = intPreferencesKey("slider_thickness")
        val SHOW_SLIDER_ICONS = booleanPreferencesKey("show_slider_icons")
        val SLIDER_POSITION = stringPreferencesKey("slider_position")
        val SLIDER_STYLE = stringPreferencesKey("slider_style")

        val SHOW_MEDIA_PLAYER = booleanPreferencesKey("show_media_player")
        val MEDIA_POSITION = stringPreferencesKey("media_position")
        val SHOW_CONNECTED_DEVICES = booleanPreferencesKey("show_connected_devices")

        val ICON_SIZE = stringPreferencesKey("icon_size")
        val TILE_SIZE_DP = intPreferencesKey("tile_size_dp")
        val TILE_CORNER_RADIUS = intPreferencesKey("tile_corner_radius")
        val TILE_SPACING = intPreferencesKey("tile_spacing")
        val SHOW_TILE_LABELS = booleanPreferencesKey("show_tile_labels")
        val ICON_ALIGNMENT = stringPreferencesKey("icon_alignment")
        val CUSTOM_ACTIVE_COLOR = longPreferencesKey("custom_active_color")
        val CUSTOM_INACTIVE_COLOR = longPreferencesKey("custom_inactive_color")
        val CUSTOM_ICON_COLOR = longPreferencesKey("custom_icon_color")
        val LABEL_SCALE = floatPreferencesKey("label_scale")

        val THEME_PRESET = stringPreferencesKey("theme_preset")
        val BG_ALPHA = floatPreferencesKey("bg_alpha")
        val BLUR_INTENSITY = floatPreferencesKey("blur_intensity")
        val GLASS_INTENSITY = floatPreferencesKey("glass_intensity")
        val PANEL_CORNER_RADIUS = intPreferencesKey("panel_corner_radius")
        val SHADOW_INTENSITY = floatPreferencesKey("shadow_intensity")
        val CUSTOM_ACCENT_COLOR = longPreferencesKey("custom_accent_color")

        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val OPEN_SPEED_MS = intPreferencesKey("open_speed_ms")
        val CLOSE_SPEED_MS = intPreferencesKey("close_speed_ms")
        val AUTO_SAVE = booleanPreferencesKey("auto_save")
        val LANGUAGE = stringPreferencesKey("language")

        val FLOATING_ENABLED = booleanPreferencesKey("floating_enabled")
        val FLOATING_VISIBLE = booleanPreferencesKey("floating_visible")
        val FLOATING_TEMP_HIDDEN = booleanPreferencesKey("floating_temp_hidden")
        val FLOATING_SIZE = intPreferencesKey("floating_size")
        val FLOATING_ALPHA = floatPreferencesKey("floating_alpha")
        val FLOATING_ICON_INDEX = intPreferencesKey("floating_icon_index")
        val FLOATING_EDGE_RIGHT = booleanPreferencesKey("floating_edge_right")
        val FLOATING_Y_FRACTION = floatPreferencesKey("floating_y_fraction")
        val FLOATING_SELECTED_ONLY = booleanPreferencesKey("floating_selected_only")
    }

    val configFlow: Flow<ControlCenterConfig> = context.oneXDataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> mapPreferences(prefs) }

    private fun mapPreferences(prefs: Preferences): ControlCenterConfig {
        val defaults = ControlCenterConfig()
        val activeTilesRaw = prefs[Keys.ACTIVE_TILE_IDS]
        val activeTiles = if (activeTilesRaw.isNullOrBlank()) {
            defaults.activeTileIds
        } else {
            activeTilesRaw.split(",")
                .mapNotNull { QuickTileId.fromId(it.trim()) }
                .distinct()
                .ifEmpty { defaults.activeTileIds }
        }

        val favRaw = prefs[Keys.FAVORITE_TILE_IDS]
        val favTiles = if (favRaw == null) {
            defaults.favoriteTileIds
        } else if (favRaw.isBlank()) {
            emptySet()
        } else {
            favRaw.split(",").mapNotNull { QuickTileId.fromId(it.trim()) }.toSet()
        }

        return ControlCenterConfig(
            initialVisibleTiles = (prefs[Keys.INITIAL_VISIBLE_TILES] ?: defaults.initialVisibleTiles)
                .coerceIn(4, 12),
            gridColumns = (prefs[Keys.GRID_COLUMNS] ?: defaults.gridColumns).coerceIn(3, 6),
            activeTileIds = activeTiles,
            favoriteTileIds = favTiles,
            hideUnavailableTiles = prefs[Keys.HIDE_UNAVAILABLE_TILES] ?: defaults.hideUnavailableTiles,
            rememberLastExpandedState = prefs[Keys.REMEMBER_LAST_STATE] ?: defaults.rememberLastExpandedState,
            lastWasExpanded = prefs[Keys.LAST_WAS_EXPANDED] ?: defaults.lastWasExpanded,

            showBrightnessSlider = prefs[Keys.SHOW_BRIGHTNESS] ?: defaults.showBrightnessSlider,
            showVolumeSlider = prefs[Keys.SHOW_VOLUME] ?: defaults.showVolumeSlider,
            sliderThicknessDp = (prefs[Keys.SLIDER_THICKNESS] ?: defaults.sliderThicknessDp).coerceIn(28, 56),
            showSliderIcons = prefs[Keys.SHOW_SLIDER_ICONS] ?: defaults.showSliderIcons,
            sliderPosition = SliderPosition.fromId(prefs[Keys.SLIDER_POSITION]),
            sliderStyle = SliderStyle.fromId(prefs[Keys.SLIDER_STYLE]),

            showMediaPlayer = prefs[Keys.SHOW_MEDIA_PLAYER] ?: defaults.showMediaPlayer,
            mediaPlayerPosition = SliderPosition.fromId(prefs[Keys.MEDIA_POSITION] ?: SliderPosition.TOP.id),
            showConnectedDevicesRow = prefs[Keys.SHOW_CONNECTED_DEVICES] ?: defaults.showConnectedDevicesRow,

            iconSize = IconSizeOption.fromId(prefs[Keys.ICON_SIZE]),
            tileSizeDp = (prefs[Keys.TILE_SIZE_DP] ?: defaults.tileSizeDp).coerceIn(46, 72),
            tileCornerRadiusDp = (prefs[Keys.TILE_CORNER_RADIUS] ?: defaults.tileCornerRadiusDp).coerceIn(10, 36),
            tileSpacingDp = (prefs[Keys.TILE_SPACING] ?: defaults.tileSpacingDp).coerceIn(6, 18),
            showTileLabels = prefs[Keys.SHOW_TILE_LABELS] ?: defaults.showTileLabels,
            iconAlignment = TileAlignment.fromId(prefs[Keys.ICON_ALIGNMENT]),
            customActiveColorArgb = prefs[Keys.CUSTOM_ACTIVE_COLOR] ?: defaults.customActiveColorArgb,
            customInactiveColorArgb = prefs[Keys.CUSTOM_INACTIVE_COLOR] ?: defaults.customInactiveColorArgb,
            customIconColorArgb = prefs[Keys.CUSTOM_ICON_COLOR] ?: defaults.customIconColorArgb,
            labelScale = (prefs[Keys.LABEL_SCALE] ?: defaults.labelScale).coerceIn(0.85f, 1.25f),

            themePreset = ThemePreset.fromId(prefs[Keys.THEME_PRESET]),
            backgroundAlpha = (prefs[Keys.BG_ALPHA] ?: defaults.backgroundAlpha).coerceIn(0.35f, 1.0f),
            blurIntensity = (prefs[Keys.BLUR_INTENSITY] ?: defaults.blurIntensity).coerceIn(0f, 1f),
            glassIntensity = (prefs[Keys.GLASS_INTENSITY] ?: defaults.glassIntensity).coerceIn(0f, 1f),
            panelCornerRadiusDp = (prefs[Keys.PANEL_CORNER_RADIUS] ?: defaults.panelCornerRadiusDp).coerceIn(14, 40),
            shadowIntensity = (prefs[Keys.SHADOW_INTENSITY] ?: defaults.shadowIntensity).coerceIn(0f, 1f),
            customAccentColorArgb = prefs[Keys.CUSTOM_ACCENT_COLOR] ?: defaults.customAccentColorArgb,

            hapticsEnabled = prefs[Keys.HAPTICS_ENABLED] ?: defaults.hapticsEnabled,
            openSpeedMs = (prefs[Keys.OPEN_SPEED_MS] ?: defaults.openSpeedMs).coerceIn(150, 550),
            closeSpeedMs = (prefs[Keys.CLOSE_SPEED_MS] ?: defaults.closeSpeedMs).coerceIn(120, 450),
            autoSaveEnabled = prefs[Keys.AUTO_SAVE] ?: defaults.autoSaveEnabled,
            language = AppLanguage.fromId(prefs[Keys.LANGUAGE]),

            floatingButtonEnabled = prefs[Keys.FLOATING_ENABLED] ?: defaults.floatingButtonEnabled,
            floatingButtonVisible = prefs[Keys.FLOATING_VISIBLE] ?: defaults.floatingButtonVisible,
            floatingButtonTemporarilyHidden = prefs[Keys.FLOATING_TEMP_HIDDEN] ?: defaults.floatingButtonTemporarilyHidden,
            floatingButtonSizeDp = (prefs[Keys.FLOATING_SIZE] ?: defaults.floatingButtonSizeDp).coerceIn(44, 72),
            floatingButtonAlpha = (prefs[Keys.FLOATING_ALPHA] ?: defaults.floatingButtonAlpha).coerceIn(0.30f, 1.0f),
            floatingButtonIconIndex = (prefs[Keys.FLOATING_ICON_INDEX] ?: defaults.floatingButtonIconIndex).coerceIn(0, 4),
            floatingButtonEdgeRight = prefs[Keys.FLOATING_EDGE_RIGHT] ?: defaults.floatingButtonEdgeRight,
            floatingButtonYFraction = (prefs[Keys.FLOATING_Y_FRACTION] ?: defaults.floatingButtonYFraction).coerceIn(0.08f, 0.86f),
            floatingShowOnSelectedScreensOnly = prefs[Keys.FLOATING_SELECTED_ONLY] ?: defaults.floatingShowOnSelectedScreensOnly
        )
    }

    suspend fun saveConfig(config: ControlCenterConfig) {
        context.oneXDataStore.edit { prefs ->
            prefs[Keys.INITIAL_VISIBLE_TILES] = config.initialVisibleTiles
            prefs[Keys.GRID_COLUMNS] = config.gridColumns
            prefs[Keys.ACTIVE_TILE_IDS] = config.activeTileIds.joinToString(",") { it.id }
            prefs[Keys.FAVORITE_TILE_IDS] = config.favoriteTileIds.joinToString(",") { it.id }
            prefs[Keys.HIDE_UNAVAILABLE_TILES] = config.hideUnavailableTiles
            prefs[Keys.REMEMBER_LAST_STATE] = config.rememberLastExpandedState
            prefs[Keys.LAST_WAS_EXPANDED] = config.lastWasExpanded

            prefs[Keys.SHOW_BRIGHTNESS] = config.showBrightnessSlider
            prefs[Keys.SHOW_VOLUME] = config.showVolumeSlider
            prefs[Keys.SLIDER_THICKNESS] = config.sliderThicknessDp
            prefs[Keys.SHOW_SLIDER_ICONS] = config.showSliderIcons
            prefs[Keys.SLIDER_POSITION] = config.sliderPosition.id
            prefs[Keys.SLIDER_STYLE] = config.sliderStyle.id

            prefs[Keys.SHOW_MEDIA_PLAYER] = config.showMediaPlayer
            prefs[Keys.MEDIA_POSITION] = config.mediaPlayerPosition.id
            prefs[Keys.SHOW_CONNECTED_DEVICES] = config.showConnectedDevicesRow

            prefs[Keys.ICON_SIZE] = config.iconSize.id
            prefs[Keys.TILE_SIZE_DP] = config.tileSizeDp
            prefs[Keys.TILE_CORNER_RADIUS] = config.tileCornerRadiusDp
            prefs[Keys.TILE_SPACING] = config.tileSpacingDp
            prefs[Keys.SHOW_TILE_LABELS] = config.showTileLabels
            prefs[Keys.ICON_ALIGNMENT] = config.iconAlignment.id
            prefs[Keys.CUSTOM_ACTIVE_COLOR] = config.customActiveColorArgb
            prefs[Keys.CUSTOM_INACTIVE_COLOR] = config.customInactiveColorArgb
            prefs[Keys.CUSTOM_ICON_COLOR] = config.customIconColorArgb
            prefs[Keys.LABEL_SCALE] = config.labelScale

            prefs[Keys.THEME_PRESET] = config.themePreset.id
            prefs[Keys.BG_ALPHA] = config.backgroundAlpha
            prefs[Keys.BLUR_INTENSITY] = config.blurIntensity
            prefs[Keys.GLASS_INTENSITY] = config.glassIntensity
            prefs[Keys.PANEL_CORNER_RADIUS] = config.panelCornerRadiusDp
            prefs[Keys.SHADOW_INTENSITY] = config.shadowIntensity
            prefs[Keys.CUSTOM_ACCENT_COLOR] = config.customAccentColorArgb

            prefs[Keys.HAPTICS_ENABLED] = config.hapticsEnabled
            prefs[Keys.OPEN_SPEED_MS] = config.openSpeedMs
            prefs[Keys.CLOSE_SPEED_MS] = config.closeSpeedMs
            prefs[Keys.AUTO_SAVE] = config.autoSaveEnabled
            prefs[Keys.LANGUAGE] = config.language.id

            prefs[Keys.FLOATING_ENABLED] = config.floatingButtonEnabled
            prefs[Keys.FLOATING_VISIBLE] = config.floatingButtonVisible
            prefs[Keys.FLOATING_TEMP_HIDDEN] = config.floatingButtonTemporarilyHidden
            prefs[Keys.FLOATING_SIZE] = config.floatingButtonSizeDp
            prefs[Keys.FLOATING_ALPHA] = config.floatingButtonAlpha
            prefs[Keys.FLOATING_ICON_INDEX] = config.floatingButtonIconIndex
            prefs[Keys.FLOATING_EDGE_RIGHT] = config.floatingButtonEdgeRight
            prefs[Keys.FLOATING_Y_FRACTION] = config.floatingButtonYFraction
            prefs[Keys.FLOATING_SELECTED_ONLY] = config.floatingShowOnSelectedScreensOnly
        }
    }

    suspend fun update(transform: (ControlCenterConfig) -> ControlCenterConfig) {
        val current = configFlow.first()
        saveConfig(transform(current))
    }

    suspend fun updateFloatingPosition(edgeRight: Boolean, yFraction: Float) {
        context.oneXDataStore.edit { prefs ->
            prefs[Keys.FLOATING_EDGE_RIGHT] = edgeRight
            prefs[Keys.FLOATING_Y_FRACTION] = yFraction.coerceIn(0.08f, 0.86f)
        }
    }

    suspend fun resetToDefaults() {
        val currentLang = configFlow.first().language
        saveConfig(ControlCenterConfig(language = currentLang))
    }

    suspend fun exportConfigJson(): String {
        val c = configFlow.first()
        val json = JSONObject().apply {
            put("version", 1)
            put("app", "ONE X CONTROL")
            put("initialVisibleTiles", c.initialVisibleTiles)
            put("gridColumns", c.gridColumns)
            put("activeTileIds", JSONArray(c.activeTileIds.map { it.id }))
            put("favoriteTileIds", JSONArray(c.favoriteTileIds.map { it.id }))
            put("hideUnavailableTiles", c.hideUnavailableTiles)
            put("rememberLastExpandedState", c.rememberLastExpandedState)
            put("showBrightnessSlider", c.showBrightnessSlider)
            put("showVolumeSlider", c.showVolumeSlider)
            put("sliderThicknessDp", c.sliderThicknessDp)
            put("showSliderIcons", c.showSliderIcons)
            put("sliderPosition", c.sliderPosition.id)
            put("sliderStyle", c.sliderStyle.id)
            put("showMediaPlayer", c.showMediaPlayer)
            put("mediaPlayerPosition", c.mediaPlayerPosition.id)
            put("showConnectedDevicesRow", c.showConnectedDevicesRow)
            put("iconSize", c.iconSize.id)
            put("tileSizeDp", c.tileSizeDp)
            put("tileCornerRadiusDp", c.tileCornerRadiusDp)
            put("tileSpacingDp", c.tileSpacingDp)
            put("showTileLabels", c.showTileLabels)
            put("iconAlignment", c.iconAlignment.id)
            put("customActiveColorArgb", c.customActiveColorArgb)
            put("customInactiveColorArgb", c.customInactiveColorArgb)
            put("customIconColorArgb", c.customIconColorArgb)
            put("labelScale", c.labelScale.toDouble())
            put("themePreset", c.themePreset.id)
            put("backgroundAlpha", c.backgroundAlpha.toDouble())
            put("blurIntensity", c.blurIntensity.toDouble())
            put("glassIntensity", c.glassIntensity.toDouble())
            put("panelCornerRadiusDp", c.panelCornerRadiusDp)
            put("shadowIntensity", c.shadowIntensity.toDouble())
            put("customAccentColorArgb", c.customAccentColorArgb)
            put("hapticsEnabled", c.hapticsEnabled)
            put("openSpeedMs", c.openSpeedMs)
            put("closeSpeedMs", c.closeSpeedMs)
            put("autoSaveEnabled", c.autoSaveEnabled)
            put("language", c.language.id)
            put("floatingButtonSizeDp", c.floatingButtonSizeDp)
            put("floatingButtonAlpha", c.floatingButtonAlpha.toDouble())
            put("floatingButtonIconIndex", c.floatingButtonIconIndex)
            put("floatingButtonEdgeRight", c.floatingButtonEdgeRight)
            put("floatingButtonYFraction", c.floatingButtonYFraction.toDouble())
        }
        return json.toString(2)
    }

    suspend fun importConfigJson(rawJson: String): Result<Unit> = runCatching {
        val obj = JSONObject(rawJson)
        val current = configFlow.first()

        val tilesArr = obj.optJSONArray("activeTileIds")
        val importedTiles = if (tilesArr != null) {
            (0 until tilesArr.length())
                .mapNotNull { QuickTileId.fromId(tilesArr.optString(it)) }
                .distinct()
                .ifEmpty { current.activeTileIds }
        } else current.activeTileIds

        val favArr = obj.optJSONArray("favoriteTileIds")
        val importedFavs = if (favArr != null) {
            (0 until favArr.length())
                .mapNotNull { QuickTileId.fromId(favArr.optString(it)) }
                .toSet()
        } else current.favoriteTileIds

        val updated = current.copy(
            initialVisibleTiles = obj.optInt("initialVisibleTiles", current.initialVisibleTiles).coerceIn(4, 12),
            gridColumns = obj.optInt("gridColumns", current.gridColumns).coerceIn(3, 6),
            activeTileIds = importedTiles,
            favoriteTileIds = importedFavs,
            hideUnavailableTiles = obj.optBoolean("hideUnavailableTiles", current.hideUnavailableTiles),
            rememberLastExpandedState = obj.optBoolean("rememberLastExpandedState", current.rememberLastExpandedState),
            showBrightnessSlider = obj.optBoolean("showBrightnessSlider", current.showBrightnessSlider),
            showVolumeSlider = obj.optBoolean("showVolumeSlider", current.showVolumeSlider),
            sliderThicknessDp = obj.optInt("sliderThicknessDp", current.sliderThicknessDp).coerceIn(28, 56),
            showSliderIcons = obj.optBoolean("showSliderIcons", current.showSliderIcons),
            sliderPosition = SliderPosition.fromId(obj.optString("sliderPosition", current.sliderPosition.id)),
            sliderStyle = SliderStyle.fromId(obj.optString("sliderStyle", current.sliderStyle.id)),
            showMediaPlayer = obj.optBoolean("showMediaPlayer", current.showMediaPlayer),
            mediaPlayerPosition = SliderPosition.fromId(obj.optString("mediaPlayerPosition", current.mediaPlayerPosition.id)),
            showConnectedDevicesRow = obj.optBoolean("showConnectedDevicesRow", current.showConnectedDevicesRow),
            iconSize = IconSizeOption.fromId(obj.optString("iconSize", current.iconSize.id)),
            tileSizeDp = obj.optInt("tileSizeDp", current.tileSizeDp).coerceIn(46, 72),
            tileCornerRadiusDp = obj.optInt("tileCornerRadiusDp", current.tileCornerRadiusDp).coerceIn(10, 36),
            tileSpacingDp = obj.optInt("tileSpacingDp", current.tileSpacingDp).coerceIn(6, 18),
            showTileLabels = obj.optBoolean("showTileLabels", current.showTileLabels),
            iconAlignment = TileAlignment.fromId(obj.optString("iconAlignment", current.iconAlignment.id)),
            customActiveColorArgb = obj.optLong("customActiveColorArgb", current.customActiveColorArgb),
            customInactiveColorArgb = obj.optLong("customInactiveColorArgb", current.customInactiveColorArgb),
            customIconColorArgb = obj.optLong("customIconColorArgb", current.customIconColorArgb),
            labelScale = obj.optDouble("labelScale", current.labelScale.toDouble()).toFloat().coerceIn(0.85f, 1.25f),
            themePreset = ThemePreset.fromId(obj.optString("themePreset", current.themePreset.id)),
            backgroundAlpha = obj.optDouble("backgroundAlpha", current.backgroundAlpha.toDouble()).toFloat().coerceIn(0.35f, 1f),
            blurIntensity = obj.optDouble("blurIntensity", current.blurIntensity.toDouble()).toFloat().coerceIn(0f, 1f),
            glassIntensity = obj.optDouble("glassIntensity", current.glassIntensity.toDouble()).toFloat().coerceIn(0f, 1f),
            panelCornerRadiusDp = obj.optInt("panelCornerRadiusDp", current.panelCornerRadiusDp).coerceIn(14, 40),
            shadowIntensity = obj.optDouble("shadowIntensity", current.shadowIntensity.toDouble()).toFloat().coerceIn(0f, 1f),
            customAccentColorArgb = obj.optLong("customAccentColorArgb", current.customAccentColorArgb),
            hapticsEnabled = obj.optBoolean("hapticsEnabled", current.hapticsEnabled),
            openSpeedMs = obj.optInt("openSpeedMs", current.openSpeedMs).coerceIn(150, 550),
            closeSpeedMs = obj.optInt("closeSpeedMs", current.closeSpeedMs).coerceIn(120, 450),
            autoSaveEnabled = obj.optBoolean("autoSaveEnabled", current.autoSaveEnabled),
            language = AppLanguage.fromId(obj.optString("language", current.language.id)),
            floatingButtonSizeDp = obj.optInt("floatingButtonSizeDp", current.floatingButtonSizeDp).coerceIn(44, 72),
            floatingButtonAlpha = obj.optDouble("floatingButtonAlpha", current.floatingButtonAlpha.toDouble()).toFloat().coerceIn(0.30f, 1f),
            floatingButtonIconIndex = obj.optInt("floatingButtonIconIndex", current.floatingButtonIconIndex).coerceIn(0, 4),
            floatingButtonEdgeRight = obj.optBoolean("floatingButtonEdgeRight", current.floatingButtonEdgeRight),
            floatingButtonYFraction = obj.optDouble("floatingButtonYFraction", current.floatingButtonYFraction.toDouble()).toFloat().coerceIn(0.08f, 0.86f)
        )
        saveConfig(updated)
    }
}
