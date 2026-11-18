package com.example.data

import androidx.compose.ui.graphics.Color

enum class ThemePreset(
    val id: String,
    val bgTop: Long,
    val bgBottom: Long,
    val panelSurface: Long,
    val cardSurface: Long,
    val accent: Long,
    val accentSecondary: Long,
    val activeTile: Long,
    val inactiveTile: Long,
    val textPrimary: Long,
    val textSecondary: Long
) {
    MIDNIGHT_BLUE(
        id = "midnight_blue",
        bgTop = 0xFF060C1E,
        bgBottom = 0xFF0E1B3E,
        panelSurface = 0xFF0C152B,
        cardSurface = 0xFF17233E,
        accent = 0xFF2B7FFF,
        accentSecondary = 0xFF56A8FF,
        activeTile = 0xFF2563EB,
        inactiveTile = 0xFF25324E,
        textPrimary = 0xFFF8FAFC,
        textSecondary = 0xFFA6B6D4
    ),
    PURPLE_GALAXY(
        id = "purple_galaxy",
        bgTop = 0xFF0E0720,
        bgBottom = 0xFF221147,
        panelSurface = 0xFF150D2D,
        cardSurface = 0xFF231747,
        accent = 0xFF9B5CFF,
        accentSecondary = 0xFFC084FC,
        activeTile = 0xFF7C3AED,
        inactiveTile = 0xFF2E224D,
        textPrimary = 0xFFFAF5FF,
        textSecondary = 0xFFC4B5FD
    ),
    ELECTRIC_CYAN(
        id = "electric_cyan",
        bgTop = 0xFF04131E,
        bgBottom = 0xFF09283B,
        panelSurface = 0xFF081D2B,
        cardSurface = 0xFF123042,
        accent = 0xFF06B6D4,
        accentSecondary = 0xFF38BDF8,
        activeTile = 0xFF0891B2,
        inactiveTile = 0xFF1D3A4C,
        textPrimary = 0xFFECFEFF,
        textSecondary = 0xFFA5D8E8
    ),
    EMERALD(
        id = "emerald",
        bgTop = 0xFF041712,
        bgBottom = 0xFF0B2E24,
        panelSurface = 0xFF09211A,
        cardSurface = 0xFF14352B,
        accent = 0xFF10B981,
        accentSecondary = 0xFF34D399,
        activeTile = 0xFF059669,
        inactiveTile = 0xFF1D4035,
        textPrimary = 0xFFECFDF5,
        textSecondary = 0xFFA7D9C6
    ),
    ROSE_GOLD(
        id = "rose_gold",
        bgTop = 0xFF1A0B14,
        bgBottom = 0xFF341626,
        panelSurface = 0xFF22101B,
        cardSurface = 0xFF361D2C,
        accent = 0xFFF472B6,
        accentSecondary = 0xFFFBBF24,
        activeTile = 0xFFDB2777,
        inactiveTile = 0xFF422636,
        textPrimary = 0xFFFFF1F2,
        textSecondary = 0xFFE6B8CC
    ),
    MINIMAL_BLACK(
        id = "minimal_black",
        bgTop = 0xFF030407,
        bgBottom = 0xFF0C0F16,
        panelSurface = 0xFF0B0D12,
        cardSurface = 0xFF181C24,
        accent = 0xFF3B82F6,
        accentSecondary = 0xFF60A5FA,
        activeTile = 0xFF2563EB,
        inactiveTile = 0xFF262B36,
        textPrimary = 0xFFF9FAFB,
        textSecondary = 0xFF9CA3AF
    ),
    ARCTIC_SILVER(
        id = "arctic_silver",
        bgTop = 0xFF0E1520,
        bgBottom = 0xFF1F2B3D,
        panelSurface = 0xFF182230,
        cardSurface = 0xFF273549,
        accent = 0xFF7DD3FC,
        accentSecondary = 0xFFE0F2FE,
        activeTile = 0xFF0284C7,
        inactiveTile = 0xFF34445B,
        textPrimary = 0xFFF8FAFC,
        textSecondary = 0xFFCBD5E1
    ),
    CUSTOM(
        id = "custom",
        bgTop = 0xFF070E21,
        bgBottom = 0xFF102042,
        panelSurface = 0xFF0D172E,
        cardSurface = 0xFF182745,
        accent = 0xFF2B7FFF,
        accentSecondary = 0xFF60A5FA,
        activeTile = 0xFF2563EB,
        inactiveTile = 0xFF25324E,
        textPrimary = 0xFFF8FAFC,
        textSecondary = 0xFFA6B6D4
    );

    companion object {
        fun fromId(id: String?): ThemePreset =
            entries.firstOrNull { it.id == id } ?: MIDNIGHT_BLUE
    }
}

enum class IconSizeOption(val id: String, val iconDp: Int) {
    EXTRA_SMALL("xs", 18),
    SMALL("sm", 21),
    MEDIUM("md", 24),
    LARGE("lg", 28),
    EXTRA_LARGE("xl", 32);

    companion object {
        fun fromId(id: String?): IconSizeOption =
            entries.firstOrNull { it.id == id } ?: MEDIUM
    }
}

enum class AppLanguage(val id: String) {
    SYSTEM("system"),
    ARABIC("ar"),
    ENGLISH("en");

    companion object {
        fun fromId(id: String?): AppLanguage =
            entries.firstOrNull { it.id == id } ?: SYSTEM
    }
}

enum class SliderPosition(val id: String) {
    TOP("top"),
    MIDDLE("middle"),
    BOTTOM("bottom");

    companion object {
        fun fromId(id: String?): SliderPosition =
            entries.firstOrNull { it.id == id } ?: BOTTOM
    }
}

enum class SliderStyle(val id: String) {
    ONE_UI_GLOW("one_ui_glow"),
    FROSTED_GLASS("frosted_glass"),
    MINIMAL_PILL("minimal_pill");

    companion object {
        fun fromId(id: String?): SliderStyle =
            entries.firstOrNull { it.id == id } ?: ONE_UI_GLOW
    }
}

enum class TileAlignment(val id: String) {
    CENTER("center"),
    START("start");

    companion object {
        fun fromId(id: String?): TileAlignment =
            entries.firstOrNull { it.id == id } ?: CENTER
    }
}

enum class TileCategory {
    CONNECTIVITY,
    DISPLAY_SYSTEM,
    XIAOMI_SMART,
    SOUND_UTILITIES
}

enum class QuickTileId(val id: String, val category: TileCategory) {
    // Connectivity
    WIFI("wifi", TileCategory.CONNECTIVITY),
    BLUETOOTH("bluetooth", TileCategory.CONNECTIVITY),
    MOBILE_DATA("mobile_data", TileCategory.CONNECTIVITY),
    AIRPLANE_MODE("airplane_mode", TileCategory.CONNECTIVITY),
    HOTSPOT("hotspot", TileCategory.CONNECTIVITY),
    LOCATION("location", TileCategory.CONNECTIVITY),
    NFC("nfc", TileCategory.CONNECTIVITY),
    VPN("vpn", TileCategory.CONNECTIVITY),

    // Display & System
    AUTO_ROTATE("auto_rotate", TileCategory.DISPLAY_SYSTEM),
    FLASHLIGHT("flashlight", TileCategory.DISPLAY_SYSTEM),
    DND("dnd", TileCategory.DISPLAY_SYSTEM),
    BATTERY_SAVER("battery_saver", TileCategory.DISPLAY_SYSTEM),
    DARK_MODE("dark_mode", TileCategory.DISPLAY_SYSTEM),
    READING_MODE("reading_mode", TileCategory.DISPLAY_SYSTEM),
    EYE_COMFORT("eye_comfort", TileCategory.DISPLAY_SYSTEM),
    SCREEN_TIMEOUT("screen_timeout", TileCategory.DISPLAY_SYSTEM),
    SCREENSHOT("screenshot", TileCategory.DISPLAY_SYSTEM),
    SCREEN_RECORDING("screen_recording", TileCategory.DISPLAY_SYSTEM),
    PERFORMANCE_MODE("performance_mode", TileCategory.DISPLAY_SYSTEM),
    SYNC_SETTINGS("sync_settings", TileCategory.DISPLAY_SYSTEM),

    // Xiaomi / HyperOS & Smart Ecosystem
    MI_SHARE("mi_share", TileCategory.XIAOMI_SMART),
    LINK_TO_WINDOWS("link_to_windows", TileCategory.XIAOMI_SMART),
    CAST_WIRELESS("cast_wireless", TileCategory.XIAOMI_SMART),
    SMART_DEVICES("smart_devices", TileCategory.XIAOMI_SMART),
    HOME_CONTROL("home_control", TileCategory.XIAOMI_SMART),
    CONNECTED_DEVICES("connected_devices", TileCategory.XIAOMI_SMART),
    BT_AUDIO("bt_audio", TileCategory.XIAOMI_SMART),
    DEVICE_MANAGEMENT("device_management", TileCategory.XIAOMI_SMART),
    STORAGE_CLEANER("storage_cleaner", TileCategory.XIAOMI_SMART),

    // Additional & Sound
    NEARBY_SHARE("nearby_share", TileCategory.SOUND_UTILITIES),
    SOUND_MODE("sound_mode", TileCategory.SOUND_UTILITIES),
    SILENT_MODE("silent_mode", TileCategory.SOUND_UTILITIES),
    VIBRATE_MODE("vibrate_mode", TileCategory.SOUND_UTILITIES),
    APP_SETTINGS("app_settings", TileCategory.SOUND_UTILITIES);

    companion object {
        fun fromId(id: String): QuickTileId? = entries.firstOrNull { it.id == id }

        val defaultOrder: List<QuickTileId> = listOf(
            AUTO_ROTATE,
            MOBILE_DATA,
            FLASHLIGHT,
            AIRPLANE_MODE,
            DND,
            BATTERY_SAVER,
            SYNC_SETTINGS,
            LOCATION,
            MI_SHARE,
            LINK_TO_WINDOWS,
            SCREEN_RECORDING,
            SCREENSHOT,
            DARK_MODE,
            READING_MODE,
            CAST_WIRELESS,
            HOTSPOT,
            STORAGE_CLEANER,
            SMART_DEVICES,
            HOME_CONTROL,
            PERFORMANCE_MODE,
            SOUND_MODE,
            VIBRATE_MODE,
            SILENT_MODE,
            NFC,
            VPN,
            EYE_COMFORT,
            SCREEN_TIMEOUT,
            NEARBY_SHARE,
            BT_AUDIO,
            APP_SETTINGS
        )
    }
}

data class ControlCenterConfig(
    // Layout
    val initialVisibleTiles: Int = 6,
    val gridColumns: Int = 4,
    val activeTileIds: List<QuickTileId> = QuickTileId.defaultOrder,
    val favoriteTileIds: Set<QuickTileId> = setOf(
        QuickTileId.FLASHLIGHT,
        QuickTileId.AUTO_ROTATE,
        QuickTileId.DND,
        QuickTileId.SOUND_MODE
    ),
    val hideUnavailableTiles: Boolean = false,
    val rememberLastExpandedState: Boolean = false,
    val lastWasExpanded: Boolean = false,

    // Brightness & Volume
    val showBrightnessSlider: Boolean = true,
    val showVolumeSlider: Boolean = true,
    val sliderThicknessDp: Int = 44,
    val showSliderIcons: Boolean = true,
    val sliderPosition: SliderPosition = SliderPosition.BOTTOM,
    val sliderStyle: SliderStyle = SliderStyle.ONE_UI_GLOW,

    // Media & Device Cards
    val showMediaPlayer: Boolean = true,
    val mediaPlayerPosition: SliderPosition = SliderPosition.TOP,
    val showConnectedDevicesRow: Boolean = true,

    // Icons & Tile Appearance
    val iconSize: IconSizeOption = IconSizeOption.MEDIUM,
    val tileSizeDp: Int = 54,
    val tileCornerRadiusDp: Int = 27,
    val tileSpacingDp: Int = 10,
    val showTileLabels: Boolean = true,
    val iconAlignment: TileAlignment = TileAlignment.CENTER,
    val customActiveColorArgb: Long = 0xFF2563EB,
    val customInactiveColorArgb: Long = 0xFF25324E,
    val customIconColorArgb: Long = 0xFFF8FAFC,
    val labelScale: Float = 1.0f,

    // Background & Theme
    val themePreset: ThemePreset = ThemePreset.MIDNIGHT_BLUE,
    val backgroundAlpha: Float = 0.88f,
    val blurIntensity: Float = 0.65f,
    val glassIntensity: Float = 0.72f,
    val panelCornerRadiusDp: Int = 30,
    val shadowIntensity: Float = 0.65f,
    val customAccentColorArgb: Long = 0xFF2B7FFF,

    // Behavior & Animations
    val hapticsEnabled: Boolean = true,
    val openSpeedMs: Int = 320,
    val closeSpeedMs: Int = 240,
    val autoSaveEnabled: Boolean = true,
    val language: AppLanguage = AppLanguage.SYSTEM,

    // Floating Button
    val floatingButtonEnabled: Boolean = false,
    val floatingButtonVisible: Boolean = true,
    val floatingButtonTemporarilyHidden: Boolean = false,
    val floatingButtonSizeDp: Int = 54,
    val floatingButtonAlpha: Float = 0.90f,
    val floatingButtonIconIndex: Int = 0,
    val floatingButtonEdgeRight: Boolean = true,
    val floatingButtonYFraction: Float = 0.34f,
    val floatingShowOnSelectedScreensOnly: Boolean = false
)

data class ResolvedVisualPalette(
    val bgTop: Color,
    val bgBottom: Color,
    val panelSurface: Color,
    val cardSurface: Color,
    val accent: Color,
    val accentSecondary: Color,
    val activeTile: Color,
    val inactiveTile: Color,
    val iconColor: Color,
    val textPrimary: Color,
    val textSecondary: Color
) {
    companion object {
        fun fromConfig(config: ControlCenterConfig): ResolvedVisualPalette {
            val preset = config.themePreset
            val isCustom = preset == ThemePreset.CUSTOM
            return ResolvedVisualPalette(
                bgTop = Color(preset.bgTop),
                bgBottom = Color(preset.bgBottom),
                panelSurface = Color(preset.panelSurface),
                cardSurface = Color(preset.cardSurface),
                accent = if (isCustom) Color(config.customAccentColorArgb) else Color(preset.accent),
                accentSecondary = Color(preset.accentSecondary),
                activeTile = if (isCustom) Color(config.customActiveColorArgb) else Color(preset.activeTile),
                inactiveTile = if (isCustom) Color(config.customInactiveColorArgb) else Color(preset.inactiveTile),
                iconColor = Color(config.customIconColorArgb),
                textPrimary = Color(preset.textPrimary),
                textSecondary = Color(preset.textSecondary)
            )
        }
    }
}
