package com.example.data

import java.util.Locale

data class UiStrings(
    val isArabic: Boolean,
    val appTitle: String,
    val controlCenterHeader: String,
    val subtitleTagline: String,

    // Navigation & Dashboard
    val navHome: String,
    val navStudio: String,
    val navSettings: String,
    val pullDownControlCenter: String,
    val liveControlCenterReady: String,
    val floatingOverlayTitle: String,
    val floatingOverlayRunning: String,
    val floatingOverlayStopped: String,
    val floatingOverlayNeedsPerm: String,
    val enableOverlayBtn: String,
    val disableOverlayBtn: String,
    val grantPermissionBtn: String,
    val quickLaunchSection: String,
    val quickCardStudio: String,
    val quickCardStudioSub: String,
    val quickCardTiles: String,
    val quickCardTilesSub: String,
    val quickCardThemes: String,
    val quickCardThemesSub: String,
    val quickCardPermissions: String,
    val quickCardPermissionsSub: String,
    val deviceStatusTitle: String,
    val dragTipBanner: String,
    val nonRootNotice: String,

    // Control Center Panel
    val wifiLabel: String,
    val bluetoothLabel: String,
    val connected: String,
    val disconnected: String,
    val turnedOn: String,
    val turnedOff: String,
    val unavailable: String,
    val mediaPlaying: String,
    val mediaIdle: String,
    val mediaOpenPlayer: String,
    val audioOutput: String,
    val connectedDevicesCard: String,
    val smartDevicesCard: String,
    val noConnectedDevice: String,
    val manageSmartDevices: String,
    val brightnessLabel: String,
    val volumeLabel: String,
    val dragToExpand: String,
    val dragToCollapse: String,
    val searchTilesPlaceholder: String,
    val powerMenuTitle: String,
    val powerMenuDesc: String,
    val openDeviceSettings: String,
    val openBatterySettings: String,
    val openDisplaySettings: String,

    // Settings Hub (11 sections)
    val settingsHubTitle: String,
    val secLayout: String,
    val secLayoutSub: String,
    val secTiles: String,
    val secTilesSub: String,
    val secSliders: String,
    val secSlidersSub: String,
    val secMedia: String,
    val secMediaSub: String,
    val secIcons: String,
    val secIconsSub: String,
    val secBackground: String,
    val secBackgroundSub: String,
    val secAnimations: String,
    val secAnimationsSub: String,
    val secFloatingBtn: String,
    val secFloatingBtnSub: String,
    val secPermissions: String,
    val secPermissionsSub: String,
    val secBackup: String,
    val secBackupSub: String,
    val secAbout: String,
    val secAboutSub: String,

    // Studio & Customization labels
    val livePreview: String,
    val compactMode: String,
    val mediumMode: String,
    val expandedMode: String,
    val initialTileCount: String,
    val gridColumnsLabel: String,
    val showBrightnessSlider: String,
    val showVolumeSlider: String,
    val showMediaPlayer: String,
    val showConnectedDevices: String,
    val sliderThickness: String,
    val showSliderIcons: String,
    val sliderPosition: String,
    val sliderStyleLabel: String,
    val mediaPosition: String,
    val iconSizeLabel: String,
    val tileSizeLabel: String,
    val tileCornerRadius: String,
    val tileSpacing: String,
    val showTileLabels: String,
    val showTileLabelsSub: String,
    val labelScale: String,
    val iconAlignmentLabel: String,
    val activeTileColor: String,
    val inactiveTileColor: String,
    val accentColor: String,
    val themePresets: String,
    val bgTransparency: String,
    val bgBlurIntensity: String,
    val glassEffectIntensity: String,
    val panelCornerRadius: String,
    val shadowIntensity: String,
    val hapticsFeedback: String,
    val openAnimSpeed: String,
    val closeAnimSpeed: String,
    val autoSaveChanges: String,
    val rememberLastState: String,
    val restoreDefaults: String,
    val floatingBtnEnable: String,
    val floatingBtnVisibility: String,
    val floatingBtnTempHide: String,
    val floatingBtnSelectedOnly: String,
    val floatingBtnSize: String,
    val floatingBtnTransparency: String,
    val floatingBtnIcon: String,
    val floatingBtnResetPos: String,
    val hideUnavailableTiles: String,
    val activeTilesSection: String,
    val availableToAddSection: String,
    val dragOrUseArrowsHint: String,
    val favoritesOnlyHint: String,
    val exportBackup: String,
    val importBackup: String,
    val copyBackupJson: String,
    val backupSuccess: String,
    val restoreSuccess: String,
    val restoreFailed: String,
    val languageLabel: String,
    val langSystem: String,
    val langArabic: String,
    val langEnglish: String,
    val granted: String,
    val notGranted: String,
    val optional: String,
    val requiredForFloating: String,
    val permOverlayTitle: String,
    val permOverlayDesc: String,
    val permWriteSettingsTitle: String,
    val permWriteSettingsDesc: String,
    val permDndTitle: String,
    val permDndDesc: String,
    val permNotifTitle: String,
    val permNotifDesc: String,
    val permBluetoothTitle: String,
    val permBluetoothDesc: String,
    val hardwareCompatTitle: String,
    val available: String,
    val notInstalled: String,
    val aboutDisclaimerTitle: String,
    val aboutDisclaimerBody: String,
    val backLabel: String,
    val closeLabel: String,
    val posTop: String,
    val posMiddle: String,
    val posBottom: String,
    val alignCenter: String,
    val alignStart: String
) {
    fun tileTitle(tile: QuickTileId): String = if (isArabic) {
        when (tile) {
            QuickTileId.WIFI -> "Wi-Fi"
            QuickTileId.BLUETOOTH -> "Bluetooth"
            QuickTileId.MOBILE_DATA -> "بيانات الهاتف"
            QuickTileId.AIRPLANE_MODE -> "وضع الطيران"
            QuickTileId.HOTSPOT -> "نقطة الاتصال"
            QuickTileId.LOCATION -> "الموقع"
            QuickTileId.NFC -> "NFC"
            QuickTileId.VPN -> "VPN"
            QuickTileId.AUTO_ROTATE -> "الدوران التلقائي"
            QuickTileId.FLASHLIGHT -> "المصباح"
            QuickTileId.DND -> "عدم الإزعاج"
            QuickTileId.BATTERY_SAVER -> "توفير الطاقة"
            QuickTileId.DARK_MODE -> "الوضع الداكن"
            QuickTileId.READING_MODE -> "وضع القراءة"
            QuickTileId.EYE_COMFORT -> "راحة العين"
            QuickTileId.SCREEN_TIMEOUT -> "مهلة الشاشة"
            QuickTileId.SCREENSHOT -> "لقطة شاشة"
            QuickTileId.SCREEN_RECORDING -> "تسجيل الشاشة"
            QuickTileId.PERFORMANCE_MODE -> "وضع الأداء"
            QuickTileId.SYNC_SETTINGS -> "المزامنة"
            QuickTileId.MI_SHARE -> "Mi Share"
            QuickTileId.LINK_TO_WINDOWS -> "ربط Windows"
            QuickTileId.CAST_WIRELESS -> "البث"
            QuickTileId.SMART_DEVICES -> "الأجهزة الذكية"
            QuickTileId.HOME_CONTROL -> "مركز المنزل"
            QuickTileId.CONNECTED_DEVICES -> "الأجهزة المتصلة"
            QuickTileId.BT_AUDIO -> "صوت بلوتوث"
            QuickTileId.DEVICE_MANAGEMENT -> "إدارة الأجهزة"
            QuickTileId.STORAGE_CLEANER -> "التنظيف"
            QuickTileId.NEARBY_SHARE -> "مشاركة سريعة"
            QuickTileId.SOUND_MODE -> "الصوت"
            QuickTileId.SILENT_MODE -> "صامت"
            QuickTileId.VIBRATE_MODE -> "اهتزاز"
            QuickTileId.APP_SETTINGS -> "إعدادات الجهاز"
        }
    } else {
        when (tile) {
            QuickTileId.WIFI -> "Wi-Fi"
            QuickTileId.BLUETOOTH -> "Bluetooth"
            QuickTileId.MOBILE_DATA -> "Mobile Data"
            QuickTileId.AIRPLANE_MODE -> "Airplane"
            QuickTileId.HOTSPOT -> "Hotspot"
            QuickTileId.LOCATION -> "Location"
            QuickTileId.NFC -> "NFC"
            QuickTileId.VPN -> "VPN"
            QuickTileId.AUTO_ROTATE -> "Auto-rotate"
            QuickTileId.FLASHLIGHT -> "Flashlight"
            QuickTileId.DND -> "Do Not Disturb"
            QuickTileId.BATTERY_SAVER -> "Battery Saver"
            QuickTileId.DARK_MODE -> "Dark Mode"
            QuickTileId.READING_MODE -> "Reading"
            QuickTileId.EYE_COMFORT -> "Eye Comfort"
            QuickTileId.SCREEN_TIMEOUT -> "Timeout"
            QuickTileId.SCREENSHOT -> "Screenshot"
            QuickTileId.SCREEN_RECORDING -> "Screen Rec"
            QuickTileId.PERFORMANCE_MODE -> "Performance"
            QuickTileId.SYNC_SETTINGS -> "Sync"
            QuickTileId.MI_SHARE -> "Mi Share"
            QuickTileId.LINK_TO_WINDOWS -> "Link Windows"
            QuickTileId.CAST_WIRELESS -> "Cast"
            QuickTileId.SMART_DEVICES -> "Smart Devices"
            QuickTileId.HOME_CONTROL -> "Home Control"
            QuickTileId.CONNECTED_DEVICES -> "Devices"
            QuickTileId.BT_AUDIO -> "BT Audio"
            QuickTileId.DEVICE_MANAGEMENT -> "Device Mgr"
            QuickTileId.STORAGE_CLEANER -> "Cleaner"
            QuickTileId.NEARBY_SHARE -> "Quick Share"
            QuickTileId.SOUND_MODE -> "Sound"
            QuickTileId.SILENT_MODE -> "Silent"
            QuickTileId.VIBRATE_MODE -> "Vibrate"
            QuickTileId.APP_SETTINGS -> "Settings"
        }
    }

    fun presetName(preset: ThemePreset): String = if (isArabic) {
        when (preset) {
            ThemePreset.MIDNIGHT_BLUE -> "أزرق منتصف الليل"
            ThemePreset.PURPLE_GALAXY -> "مجرة بنفسجية"
            ThemePreset.ELECTRIC_CYAN -> "سماوي كهربائي"
            ThemePreset.EMERALD -> "زمردي"
            ThemePreset.ROSE_GOLD -> "ذهبي وردي"
            ThemePreset.MINIMAL_BLACK -> "أسود نقي"
            ThemePreset.ARCTIC_SILVER -> "فضي قطبي"
            ThemePreset.CUSTOM -> "ألوان مخصصة"
        }
    } else {
        when (preset) {
            ThemePreset.MIDNIGHT_BLUE -> "Midnight Blue"
            ThemePreset.PURPLE_GALAXY -> "Purple Galaxy"
            ThemePreset.ELECTRIC_CYAN -> "Electric Cyan"
            ThemePreset.EMERALD -> "Emerald"
            ThemePreset.ROSE_GOLD -> "Rose Gold"
            ThemePreset.MINIMAL_BLACK -> "Minimal Black"
            ThemePreset.ARCTIC_SILVER -> "Arctic Silver"
            ThemePreset.CUSTOM -> "Custom Colors"
        }
    }

    fun iconSizeName(size: IconSizeOption): String = if (isArabic) {
        when (size) {
            IconSizeOption.EXTRA_SMALL -> "صغير جداً"
            IconSizeOption.SMALL -> "صغير"
            IconSizeOption.MEDIUM -> "متوسط"
            IconSizeOption.LARGE -> "كبير"
            IconSizeOption.EXTRA_LARGE -> "كبير جداً"
        }
    } else {
        when (size) {
            IconSizeOption.EXTRA_SMALL -> "XS"
            IconSizeOption.SMALL -> "Small"
            IconSizeOption.MEDIUM -> "Medium"
            IconSizeOption.LARGE -> "Large"
            IconSizeOption.EXTRA_LARGE -> "XL"
        }
    }
}

object LocalizationProvider {
    fun resolve(language: AppLanguage): UiStrings {
        val useArabic = when (language) {
            AppLanguage.ARABIC -> true
            AppLanguage.ENGLISH -> false
            AppLanguage.SYSTEM -> Locale.getDefault().language.equals("ar", ignoreCase = true)
        }
        return if (useArabic) arabicStrings else englishStrings
    }

    private val arabicStrings = UiStrings(
        isArabic = true,
        appTitle = "One X Control",
        controlCenterHeader = "ONE X CONTROL",
        subtitleTagline = "مركز تحكم علوي بتصميم One UI ومزايا HyperOS",
        navHome = "الرئيسية",
        navStudio = "استوديو التخصيص",
        navSettings = "الإعدادات",
        pullDownControlCenter = "سحب مركز التحكم من الأعلى",
        liveControlCenterReady = "اضغط على الزر العائم أو هنا لفتح اللوحة العلوية",
        floatingOverlayTitle = "الزر العائم فوق التطبيقات",
        floatingOverlayRunning = "نشط على حافة الشاشة — اسحبه لتغيير موقعه",
        floatingOverlayStopped = "متوقف حالياً — فعّله للوصول السريع من أي تطبيق",
        floatingOverlayNeedsPerm = "يتطلب صلاحية الظهور فوق التطبيقات الأخرى",
        enableOverlayBtn = "تفعيل الزر العائم",
        disableOverlayBtn = "إيقاف الزر العائم",
        grantPermissionBtn = "منح الصلاحية",
        quickLaunchSection = "التخصيص والتحكم السريع",
        quickCardStudio = "استوديو التخصيص المباشر",
        quickCardStudioSub = "معاينة حية فورية لكل التغييرات",
        quickCardTiles = "إدارة وترتيب الأزرار",
        quickCardTilesSub = "سحب وإفلات وإضافة وإخفاء الاختصارات",
        quickCardThemes = "السمات والزجاج الضبابي",
        quickCardThemesSub = "8 قوالب فاخرة وألوان مخصصة",
        quickCardPermissions = "الصلاحيات والتوافق",
        quickCardPermissionsSub = "فحص مستشعرات HyperOS و Android 14",
        deviceStatusTitle = "حالة الجهاز الحقيقية",
        dragTipBanner = "اسحب اللوحة للأسفل للتوسيع، وللأعلى للإغلاق السلس",
        nonRootNotice = "يعمل بالكامل بدون روت ولا يستبدل SystemUI الأصلي للنظام.",
        wifiLabel = "Wi-Fi",
        bluetoothLabel = "Bluetooth",
        connected = "متصل",
        disconnected = "غير متصل",
        turnedOn = "مُفعّل",
        turnedOff = "مُطفأ",
        unavailable = "غير متوفر",
        mediaPlaying = "الوسائط قيد التشغيل",
        mediaIdle = "لا توجد وسائط نشطة حالياً",
        mediaOpenPlayer = "فتح مشغل الموسيقى",
        audioOutput = "مخرج الصوت",
        connectedDevicesCard = "الأجهزة المتصلة",
        smartDevicesCard = "إدارة الأجهزة الذكية",
        noConnectedDevice = "سماعة الهاتف",
        manageSmartDevices = "البث والربط الذكي",
        brightnessLabel = "السطوع",
        volumeLabel = "الصوت",
        dragToExpand = "اسحب للأسفل للتوسيع",
        dragToCollapse = "اسحب للأعلى للطي",
        searchTilesPlaceholder = "بحث في أزرار التحكم...",
        powerMenuTitle = "اختصارات الطاقة والنظام",
        powerMenuDesc = "تقيّد أندرويد 14 قائمة إيقاف التشغيل للتطبيقات العادية دون صلاحية النظام. يمكنك الوصول السريع لإعدادات البطارية والعرض والنظام:",
        openDeviceSettings = "إعدادات النظام",
        openBatterySettings = "إعدادات البطارية",
        openDisplaySettings = "إعدادات الشاشة",
        settingsHubTitle = "إعدادات ONE X CONTROL",
        secLayout = "تخصيص مركز التحكم",
        secLayoutSub = "تخطيط اللوحة وعدد الأزرار والأعمدة",
        secTiles = "إعدادات الأزرار والاختصارات",
        secTilesSub = "ترتيب وإضافة وإزالة الأزرار والمفضلة",
        secSliders = "السطوع والصوت",
        secSlidersSub = "إظهار الأشرطة والسُمك والموقع والمظهر",
        secMedia = "مشغل الوسائط والأجهزة",
        secMediaSub = "تحكم الوسائط وبطاقات الأجهزة المتصلة",
        secIcons = "الأيقونات وأحجام الأزرار",
        secIconsSub = "حجم الأيقونات والتباعد والزوايا والألوان",
        secBackground = "مظهر اللوحة والشفافية",
        secBackgroundSub = "القوالب اللونية والضبابية وتأثير الزجاج",
        secAnimations = "الحركات والاهتزاز",
        secAnimationsSub = "سرعة الفتح والإغلاق والاستجابة اللمسية",
        secFloatingBtn = "إجراءات الزر العائم",
        secFloatingBtnSub = "الحجم والشفافية والأيقونة والموقع",
        secPermissions = "الخصوصية والصلاحيات",
        secPermissionsSub = "حالة الصلاحيات وتوافق عتاد الجهاز",
        secBackup = "النسخ الاحتياطي والاستعادة",
        secBackupSub = "حفظ واسترجاع إعداداتك كملف JSON",
        secAbout = "حول التطبيق واللغة",
        secAboutSub = "اللغة العربية/الإنجليزية ومعلومات التوافق",
        livePreview = "معاينة حية تفاعلية",
        compactMode = "مضغوط",
        mediumMode = "متوسط",
        expandedMode = "موسع",
        initialTileCount = "عدد الأزرار عند الفتح الأول",
        gridColumnsLabel = "تخطيط الشبكة (عدد الأعمدة)",
        showBrightnessSlider = "إظهار شريط السطوع",
        showVolumeSlider = "إظهار شريط الصوت",
        showMediaPlayer = "إظهار مشغل الوسائط",
        showConnectedDevices = "إظهار الأجهزة المتصلة",
        sliderThickness = "سماكة الأشرطة",
        showSliderIcons = "إظهار أيقونات الأشرطة",
        sliderPosition = "موضع أشرطة التحكم",
        sliderStyleLabel = "نمط أشرطة التحكم",
        mediaPosition = "موضع مشغل الوسائط",
        iconSizeLabel = "حجم الأيقونات",
        tileSizeLabel = "حجم الأزرار",
        tileCornerRadius = "استدارة حواف الأزرار",
        tileSpacing = "المسافة بين الأزرار",
        showTileLabels = "إظهار أسماء الأزرار",
        showTileLabelsSub = "عرض اسم كل زر أسفل الأيقونة",
        labelScale = "حجم خط التسميات",
        iconAlignmentLabel = "محاذاة الأيقونات",
        activeTileColor = "لون الزر النشط",
        inactiveTileColor = "لون الزر غير النشط",
        accentColor = "اللون الرئيسي للتوهج",
        themePresets = "قوالب الألوان الفاخرة",
        bgTransparency = "شفافية الخلفية",
        bgBlurIntensity = "تعتيم الخلفية (الضبابية)",
        glassEffectIntensity = "شدة تأثير الزجاج",
        panelCornerRadius = "استدارة حواف اللوحة",
        shadowIntensity = "شدة الظلال والتوهج",
        hapticsFeedback = "اهتزاز عند اللمس",
        openAnimSpeed = "سرعة حركة الفتح",
        closeAnimSpeed = "سرعة حركة الإغلاق",
        autoSaveChanges = "حفظ التعديلات تلقائياً",
        rememberLastState = "تذكر آخر حالة (مضغوط / موسع)",
        restoreDefaults = "استعادة الإعدادات الافتراضية",
        floatingBtnEnable = "تشغيل الزر العائم على النظام",
        floatingBtnVisibility = "إظهار الزر العائم",
        floatingBtnTempHide = "إخفاء مؤقت للزر العائم",
        floatingBtnSelectedOnly = "إظهار داخل الشاشة الرئيسية فقط",
        floatingBtnSize = "حجم الزر العائم",
        floatingBtnTransparency = "شفافية الزر العائم",
        floatingBtnIcon = "أيقونة الزر العائم",
        floatingBtnResetPos = "إعادة تعيين موقع الزر العائم",
        hideUnavailableTiles = "إخفاء الميزات غير المتوفرة على الجهاز",
        activeTilesSection = "الأزرار النشطة (اضغط مطولاً أو استخدم الأسهم للترتيب)",
        availableToAddSection = "أزرار إضافية متاحة",
        dragOrUseArrowsHint = "رتّب أزرارك المفضلة وسيتم حفظها فوراً في DataStore",
        favoritesOnlyHint = "النجمة تحدد الأزرار المفضلة في اللوحة المضغوطة",
        exportBackup = "تصدير ملف النسخ الاحتياطي",
        importBackup = "استيراد ملف إعدادات",
        copyBackupJson = "نسخ الإعدادات كـ JSON",
        backupSuccess = "تم حفظ النسخة الاحتياطية بنجاح",
        restoreSuccess = "تمت استعادة الإعدادات بنجاح",
        restoreFailed = "تعذر قراءة ملف النسخة الاحتياطية",
        languageLabel = "لغة التطبيق",
        langSystem = "لغة النظام",
        langArabic = "العربية",
        langEnglish = "English",
        granted = "ممنوحة",
        notGranted = "غير ممنوحة",
        optional = "اختيارية",
        requiredForFloating = "مطلوبة للزر العائم",
        permOverlayTitle = "الظهور فوق التطبيقات الأخرى",
        permOverlayDesc = "تسمح بعرض الزر العائم وفتح لوحة ONE X CONTROL من الحافة العلوية فوق أي تطبيق.",
        permWriteSettingsTitle = "تعديل إعدادات النظام",
        permWriteSettingsDesc = "تسمح بضبط سطوع الشاشة الحقيقي وتبديل الدوران التلقائي مباشرة من اللوحة.",
        permDndTitle = "الوصول لعدم الإزعاج",
        permDndDesc = "تسمح بتشغيل وإيقاف وضع عدم الإزعاج ووضع الصامت مباشرة بضغطة واحدة.",
        permNotifTitle = "الإشعارات (Android 13+)",
        permNotifDesc = "لعرض إشعار خفيف يحافظ على استقرار الزر العائم عند تفعيله دون استهلاك بطارية.",
        permBluetoothTitle = "الأجهزة القريبة (Bluetooth)",
        permBluetoothDesc = "لعرض اسم السماعة أو الجهاز المتصل فعلياً في بطاقة البلوتوث والأجهزة المتصلة.",
        hardwareCompatTitle = "فحص توافق العتاد و HyperOS",
        available = "متوفر",
        notInstalled = "غير مثبت على الجهاز",
        aboutDisclaimerTitle = "التوافق مع Android 14 و Xiaomi HyperOS",
        aboutDisclaimerBody = "يعمل ONE X CONTROL كتطبيق أصلي آمن بدون روت وبدون فتح البوتلودر. لا يقوم التطبيق بتعديل أو تعطيل SystemUI الأصلي، بل يوفر مركز تحكم علوي إضافي عبر الزر العائم ويستخدم واجهات أندرويد الرسمية فقط.",
        backLabel = "رجوع",
        closeLabel = "إغلاق",
        posTop = "أعلى",
        posMiddle = "وسط",
        posBottom = "أسفل",
        alignCenter = "توسيط",
        alignStart = "بداية"
    )

    private val englishStrings = UiStrings(
        isArabic = false,
        appTitle = "One X Control",
        controlCenterHeader = "ONE X CONTROL",
        subtitleTagline = "Top-edge One UI Glass Control Center with HyperOS shortcuts",
        navHome = "Home",
        navStudio = "Studio",
        navSettings = "Settings",
        pullDownControlCenter = "Open Top Control Center",
        liveControlCenterReady = "Tap the floating button or here to slide down the panel",
        floatingOverlayTitle = "System Floating Control Button",
        floatingOverlayRunning = "Active on screen edge — drag anywhere to reposition",
        floatingOverlayStopped = "Paused — enable for instant access over any app",
        floatingOverlayNeedsPerm = "Requires Display Over Other Apps permission",
        enableOverlayBtn = "Enable Floating Button",
        disableOverlayBtn = "Stop Floating Button",
        grantPermissionBtn = "Grant Permission",
        quickLaunchSection = "Quick Customization",
        quickCardStudio = "Live Customization Studio",
        quickCardStudioSub = "Interactive preview updated in real time",
        quickCardTiles = "Quick Tile Manager",
        quickCardTilesSub = "Reorder, add, remove & favorite shortcuts",
        quickCardThemes = "Themes & Glassmorphism",
        quickCardThemesSub = "8 luxury presets + custom colors",
        quickCardPermissions = "Permissions & Compatibility",
        quickCardPermissionsSub = "Android 14 & HyperOS feature audit",
        deviceStatusTitle = "Live Device Telemetry",
        dragTipBanner = "Drag downward to expand tiles, upward to collapse or dismiss",
        nonRootNotice = "100% non-root APK. Preserves original Xiaomi SystemUI completely.",
        wifiLabel = "Wi-Fi",
        bluetoothLabel = "Bluetooth",
        connected = "Connected",
        disconnected = "Disconnected",
        turnedOn = "On",
        turnedOff = "Off",
        unavailable = "Unavailable",
        mediaPlaying = "Audio Playing",
        mediaIdle = "No Active Audio",
        mediaOpenPlayer = "Open Music App",
        audioOutput = "Audio Output",
        connectedDevicesCard = "Connected Devices",
        smartDevicesCard = "Smart Devices",
        noConnectedDevice = "Phone Speaker",
        manageSmartDevices = "Cast & Smart Link",
        brightnessLabel = "Brightness",
        volumeLabel = "Volume",
        dragToExpand = "Drag down to expand",
        dragToCollapse = "Drag up to collapse",
        searchTilesPlaceholder = "Filter quick controls...",
        powerMenuTitle = "Power & System Shortcuts",
        powerMenuDesc = "Android 14 restricts direct system power dialogs for standard non-root apps. Jump directly to device power, battery, or system controls:",
        openDeviceSettings = "System Settings",
        openBatterySettings = "Battery & Power",
        openDisplaySettings = "Display Settings",
        settingsHubTitle = "ONE X CONTROL Settings",
        secLayout = "Control Center Layout",
        secLayoutSub = "Panel presets, initial tiles & grid columns",
        secTiles = "Quick Button Management",
        secTilesSub = "Add, remove, favorite & reorder tiles",
        secSliders = "Brightness & Volume",
        secSlidersSub = "Visibility, thickness, position & style",
        secMedia = "Media Player & Devices",
        secMediaSub = "Playback card, position & connected devices",
        secIcons = "Icons & Tile Sizes",
        secIconsSub = "Icon scale, corner radius, spacing & colors",
        secBackground = "Background & Transparency",
        secBackgroundSub = "8 presets, glass blur, shadows & accent",
        secAnimations = "Animations & Haptics",
        secAnimationsSub = "Open/close speed, vibration & auto-save",
        secFloatingBtn = "Floating Button",
        secFloatingBtnSub = "Visibility, size, opacity, icon & edge snap",
        secPermissions = "Permissions & Compatibility",
        secPermissionsSub = "Transparent status & hardware checks",
        secBackup = "Backup & Restore",
        secBackupSub = "Export or import your full configuration",
        secAbout = "About ONE X CONTROL",
        secAboutSub = "Language (Arabic/English) & Android 14 info",
        livePreview = "Live Interactive Preview",
        compactMode = "Compact",
        mediumMode = "Balanced",
        expandedMode = "Expanded",
        initialTileCount = "Initial Visible Tiles",
        gridColumnsLabel = "Grid Layout (Columns)",
        showBrightnessSlider = "Show Brightness Slider",
        showVolumeSlider = "Show Volume Slider",
        showMediaPlayer = "Show Media Player",
        showConnectedDevices = "Show Connected Devices",
        sliderThickness = "Slider Thickness",
        showSliderIcons = "Show Slider Icons",
        sliderPosition = "Slider Position",
        sliderStyleLabel = "Slider Appearance",
        mediaPosition = "Media Player Position",
        iconSizeLabel = "Icon Size",
        tileSizeLabel = "Tile Size",
        tileCornerRadius = "Tile Corner Radius",
        tileSpacing = "Tile Spacing",
        showTileLabels = "Show Tile Labels",
        showTileLabelsSub = "Display action name beneath each icon",
        labelScale = "Label Font Scale",
        iconAlignmentLabel = "Icon Alignment",
        activeTileColor = "Active Tile Color",
        inactiveTileColor = "Inactive Tile Color",
        accentColor = "Glow Accent Color",
        themePresets = "Premium Theme Presets",
        bgTransparency = "Background Transparency",
        bgBlurIntensity = "Blur / Frost Intensity",
        glassEffectIntensity = "Glass Border Intensity",
        panelCornerRadius = "Panel Corner Radius",
        shadowIntensity = "Shadow & Glow Intensity",
        hapticsFeedback = "Haptic Feedback",
        openAnimSpeed = "Opening Animation Speed",
        closeAnimSpeed = "Closing Animation Speed",
        autoSaveChanges = "Auto-Save Changes",
        rememberLastState = "Remember Compact/Expanded State",
        restoreDefaults = "Restore Default Configuration",
        floatingBtnEnable = "Enable System Floating Button",
        floatingBtnVisibility = "Show Floating Button",
        floatingBtnTempHide = "Temporarily Hide Button",
        floatingBtnSelectedOnly = "Show on Home Screen Only",
        floatingBtnSize = "Floating Button Size",
        floatingBtnTransparency = "Floating Button Opacity",
        floatingBtnIcon = "Floating Button Icon",
        floatingBtnResetPos = "Reset Edge Position",
        hideUnavailableTiles = "Hide Unavailable Device Features",
        activeTilesSection = "Active Tiles (Reorder or Star Favorites)",
        availableToAddSection = "Available Tiles to Add",
        dragOrUseArrowsHint = "Customize your grid order — persisted automatically in DataStore",
        favoritesOnlyHint = "Starred tiles appear first in Compact mode",
        exportBackup = "Export Configuration File",
        importBackup = "Import Configuration File",
        copyBackupJson = "Copy Config JSON",
        backupSuccess = "Configuration exported successfully",
        restoreSuccess = "Configuration restored successfully",
        restoreFailed = "Invalid configuration file",
        languageLabel = "Interface Language",
        langSystem = "System Default",
        langArabic = "العربية",
        langEnglish = "English",
        granted = "Granted",
        notGranted = "Not Granted",
        optional = "Optional",
        requiredForFloating = "Needed for Floating Button",
        permOverlayTitle = "Display Over Other Apps",
        permOverlayDesc = "Displays the circular floating shortcut button and top-anchored control center panel above other apps.",
        permWriteSettingsTitle = "Modify System Settings",
        permWriteSettingsDesc = "Lets the brightness slider adjust actual system screen brightness and toggle Auto-Rotate directly.",
        permDndTitle = "Do Not Disturb Access",
        permDndDesc = "Enables one-tap toggling of Do Not Disturb and Silent ringer modes.",
        permNotifTitle = "Notifications (Android 13+)",
        permNotifDesc = "Shows a lightweight status notification to keep the floating button reliable without background polling.",
        permBluetoothTitle = "Nearby Devices (Bluetooth)",
        permBluetoothDesc = "Displays the real connected Bluetooth audio device name inside the Bluetooth and Connected Devices cards.",
        hardwareCompatTitle = "Hardware & HyperOS Feature Audit",
        available = "Available",
        notInstalled = "Not installed on device",
        aboutDisclaimerTitle = "Android 14 & Xiaomi HyperOS Compatibility",
        aboutDisclaimerBody = "ONE X CONTROL is a standard non-root APK designed for locked bootloaders. It never modifies or disables Xiaomi SystemUI; instead, it launches an optional top-anchored glassmorphic Control Center via the floating edge button using official Android APIs.",
        backLabel = "Back",
        closeLabel = "Close",
        posTop = "Top",
        posMiddle = "Middle",
        posBottom = "Bottom",
        alignCenter = "Center",
        alignStart = "Start"
    )
}
