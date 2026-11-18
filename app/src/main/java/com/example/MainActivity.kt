package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.system.ActionFeedback
import com.example.ui.components.FloatingControlButton
import com.example.ui.components.OneXAmbientBackground
import com.example.ui.components.TopAnchoredControlCenterOverlay
import com.example.ui.screens.AboutAndLanguageScreen
import com.example.ui.screens.BackupRestoreScreen
import com.example.ui.screens.CustomizationStudioScreen
import com.example.ui.screens.FloatingButtonSettingsScreen
import com.example.ui.screens.HomeDashboardScreen
import com.example.ui.screens.PermissionsCompatibilityScreen
import com.example.ui.screens.SettingsHubScreen
import com.example.ui.screens.SettingsSection
import com.example.ui.screens.StudioTab
import com.example.ui.theme.LocalOneXPalette
import com.example.ui.theme.LocalOneXStrings
import com.example.ui.theme.OneXControlTheme
import com.example.viewmodel.ControlCenterViewModel
import kotlin.math.roundToInt

private const val ROUTE_HOME = "home"
private const val ROUTE_STUDIO = "studio"
private const val ROUTE_SETTINGS = "settings"

class MainActivity : ComponentActivity() {

    private val viewModel: ControlCenterViewModel by viewModels()
    private val pendingRoute = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        pendingRoute.value = intent?.getStringExtra("start_route")

        setContent {
            val config by viewModel.config.collectAsStateWithLifecycle()
            OneXControlTheme(config = config) {
                OneXControlAppRoot(
                    viewModel = viewModel,
                    pendingRoute = pendingRoute.value,
                    onConsumePendingRoute = { pendingRoute.value = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        pendingRoute.value = intent.getStringExtra("start_route")
    }

    override fun onResume() {
        super.onResume()
        viewModel.onAppResumed()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onAppPaused()
    }
}

@Composable
private fun OneXControlAppRoot(
    viewModel: ControlCenterViewModel,
    pendingRoute: String?,
    onConsumePendingRoute: () -> Unit
) {
    val config by viewModel.config.collectAsStateWithLifecycle()
    val systemState by viewModel.systemState.collectAsStateWithLifecycle()
    val permissions by viewModel.permissions.collectAsStateWithLifecycle()
    val feedback by viewModel.feedback.collectAsStateWithLifecycle()
    val isPanelOpen by viewModel.isInAppPanelOpen.collectAsStateWithLifecycle()
    val isPanelExpanded by viewModel.isInAppPanelExpanded.collectAsStateWithLifecycle()
    val isOverlayRunning by viewModel.isOverlayServiceRunning.collectAsStateWithLifecycle()

    val palette = LocalOneXPalette.current
    val strings = LocalOneXStrings.current
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(pendingRoute) {
        if (!pendingRoute.isNullOrBlank()) {
            navController.navigate(pendingRoute) {
                launchSingleTop = true
            }
            onConsumePendingRoute()
        }
    }

    LaunchedEffect(feedback) {
        if (feedback is ActionFeedback.Message) {
            val msg = feedback as ActionFeedback.Message
            snackbarHostState.showSnackbar(if (strings.isArabic) msg.arabic else msg.english)
            viewModel.deviceManager.clearFeedback()
        }
    }

    // Intercept system back button to smoothly close the in-app Control Center shade
    BackHandler(enabled = isPanelOpen) {
        viewModel.closeInAppControlCenter()
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isTopLevel = currentRoute == ROUTE_HOME || currentRoute == ROUTE_STUDIO || currentRoute == ROUTE_SETTINGS

    val navItems = listOf(
        Triple(ROUTE_HOME, strings.navHome, Icons.Rounded.Home),
        Triple(ROUTE_STUDIO, strings.navStudio, Icons.Rounded.Tune),
        Triple(ROUTE_SETTINGS, strings.navSettings, Icons.Rounded.Settings)
    )

    OneXAmbientBackground(palette = palette) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useNavigationRail = maxWidth >= 600.dp
            val density = LocalDensity.current
            val containerWidthPx = with(density) { maxWidth.toPx() }
            val containerHeightPx = with(density) { maxHeight.toPx() }
            val btnSizePx = with(density) { config.floatingButtonSizeDp.coerceIn(48, 72).dp.toPx() }
            val edgeMarginPx = with(density) { 10.dp.toPx() }

            Row(modifier = Modifier.fillMaxSize()) {
                if (useNavigationRail && isTopLevel) {
                    NavigationRail(
                        containerColor = palette.panelSurface.copy(alpha = 0.90f),
                        modifier = Modifier
                            .fillMaxHeight()
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .windowInsetsPadding(WindowInsets.navigationBars)
                    ) {
                        navItems.forEach { (route, label, icon) ->
                            val selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == route } == true
                            NavigationRailItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(icon, contentDescription = label) },
                                label = { Text(label) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = palette.textPrimary,
                                    indicatorColor = palette.accent,
                                    unselectedIconColor = palette.textSecondary,
                                    unselectedTextColor = palette.textSecondary
                                ),
                                modifier = Modifier.testTag("nav_rail_$route")
                            )
                        }
                    }
                }

                Scaffold(
                    containerColor = Color.Transparent,
                    contentWindowInsets = WindowInsets.statusBars,
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                        )
                    },
                    bottomBar = {
                        if (!useNavigationRail && isTopLevel) {
                            NavigationBar(
                                containerColor = palette.panelSurface.copy(alpha = 0.92f),
                                windowInsets = WindowInsets.navigationBars
                            ) {
                                navItems.forEach { (route, label, icon) ->
                                    val selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == route } == true
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(route) {
                                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = { Icon(icon, contentDescription = label) },
                                        label = { Text(label) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color.White,
                                            selectedTextColor = palette.textPrimary,
                                            indicatorColor = palette.accent,
                                            unselectedIconColor = palette.textSecondary,
                                            unselectedTextColor = palette.textSecondary
                                        ),
                                        modifier = Modifier.testTag("nav_bar_$route")
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = ROUTE_HOME,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable(ROUTE_HOME) {
                            HomeDashboardScreen(
                                config = config,
                                palette = palette,
                                strings = strings,
                                systemState = systemState,
                                permissions = permissions,
                                isOverlayRunning = isOverlayRunning,
                                onOpenControlCenter = { viewModel.openInAppControlCenter() },
                                onToggleOverlay = { viewModel.toggleSystemFloatingOverlay(it) },
                                onOpenStudio = { navController.navigate(ROUTE_STUDIO) { launchSingleTop = true } },
                                onOpenTiles = { navController.navigate(SettingsSection.TILES.route) },
                                onOpenThemes = { navController.navigate(SettingsSection.BACKGROUND.route) },
                                onOpenPermissions = { navController.navigate(SettingsSection.PERMISSIONS.route) },
                                contentPadding = innerPadding
                            )
                        }

                        composable(ROUTE_STUDIO) {
                            CustomizationStudioScreen(
                                initialTab = StudioTab.LAYOUT,
                                showBackButton = false,
                                config = config,
                                palette = palette,
                                strings = strings,
                                systemState = systemState,
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() },
                                contentPadding = innerPadding
                            )
                        }

                        composable(ROUTE_SETTINGS) {
                            SettingsHubScreen(
                                palette = palette,
                                strings = strings,
                                onSelectSection = { section -> navController.navigate(section.route) },
                                contentPadding = innerPadding
                            )
                        }

                        // Sections 1-7 detail routes inside CustomizationStudioScreen
                        composable(SettingsSection.LAYOUT.route) {
                            StudioDetailRoute(StudioTab.LAYOUT, config, palette, strings, systemState, viewModel, innerPadding) {
                                navController.popBackStack()
                            }
                        }
                        composable(SettingsSection.TILES.route) {
                            StudioDetailRoute(StudioTab.TILES, config, palette, strings, systemState, viewModel, innerPadding) {
                                navController.popBackStack()
                            }
                        }
                        composable(SettingsSection.SLIDERS.route) {
                            StudioDetailRoute(StudioTab.SLIDERS, config, palette, strings, systemState, viewModel, innerPadding) {
                                navController.popBackStack()
                            }
                        }
                        composable(SettingsSection.MEDIA.route) {
                            StudioDetailRoute(StudioTab.MEDIA, config, palette, strings, systemState, viewModel, innerPadding) {
                                navController.popBackStack()
                            }
                        }
                        composable(SettingsSection.ICONS.route) {
                            StudioDetailRoute(StudioTab.ICONS, config, palette, strings, systemState, viewModel, innerPadding) {
                                navController.popBackStack()
                            }
                        }
                        composable(SettingsSection.BACKGROUND.route) {
                            StudioDetailRoute(StudioTab.BACKGROUND, config, palette, strings, systemState, viewModel, innerPadding) {
                                navController.popBackStack()
                            }
                        }
                        composable(SettingsSection.ANIMATIONS.route) {
                            StudioDetailRoute(StudioTab.ANIMATIONS, config, palette, strings, systemState, viewModel, innerPadding) {
                                navController.popBackStack()
                            }
                        }

                        // Sections 8-11 dedicated screens
                        composable(SettingsSection.FLOATING_BUTTON.route) {
                            FloatingButtonSettingsScreen(
                                config = config,
                                palette = palette,
                                strings = strings,
                                isOverlayRunning = isOverlayRunning,
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() },
                                contentPadding = innerPadding
                            )
                        }
                        composable(SettingsSection.PERMISSIONS.route) {
                            PermissionsCompatibilityScreen(
                                palette = palette,
                                strings = strings,
                                systemState = systemState,
                                permissions = permissions,
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() },
                                contentPadding = innerPadding
                            )
                        }
                        composable(SettingsSection.BACKUP.route) {
                            BackupRestoreScreen(
                                palette = palette,
                                strings = strings,
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() },
                                contentPadding = innerPadding
                            )
                        }
                        composable(SettingsSection.ABOUT.route) {
                            AboutAndLanguageScreen(
                                config = config,
                                palette = palette,
                                strings = strings,
                                systemState = systemState,
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() },
                                contentPadding = innerPadding
                            )
                        }
                    }
                }
            }

            // In-App Draggable Edge Floating Control Button (when system overlay isn't already showing)
            val showInAppFloatingBtn = !isPanelOpen &&
                !isOverlayRunning &&
                config.floatingButtonVisible &&
                !config.floatingButtonTemporarilyHidden &&
                (!config.floatingShowOnSelectedScreensOnly || currentRoute == ROUTE_HOME)

            if (showInAppFloatingBtn && containerWidthPx > 0f && containerHeightPx > 0f) {
                var dragOffsetX by remember(config.floatingButtonEdgeRight, containerWidthPx) {
                    mutableFloatStateOf(
                        if (config.floatingButtonEdgeRight) containerWidthPx - btnSizePx - edgeMarginPx
                        else edgeMarginPx
                    )
                }
                var dragOffsetY by remember(config.floatingButtonYFraction, containerHeightPx) {
                    mutableFloatStateOf(
                        (containerHeightPx * config.floatingButtonYFraction)
                            .coerceIn(edgeMarginPx * 5f, containerHeightPx - btnSizePx - edgeMarginPx * 10f)
                    )
                }

                Box(
                    modifier = Modifier
                        .offset { IntOffset(dragOffsetX.roundToInt(), dragOffsetY.roundToInt()) }
                        .pointerInput(containerWidthPx, containerHeightPx) {
                            detectDragGestures(
                                onDragEnd = {
                                    val snapRight = (dragOffsetX + btnSizePx / 2f) >= containerWidthPx / 2f
                                    dragOffsetX = if (snapRight) {
                                        containerWidthPx - btnSizePx - edgeMarginPx
                                    } else {
                                        edgeMarginPx
                                    }
                                    val yFrac = (dragOffsetY / containerHeightPx).coerceIn(0.08f, 0.85f)
                                    viewModel.setFloatingPosition(snapRight, yFrac)
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    dragOffsetX = (dragOffsetX + dragAmount.x)
                                        .coerceIn(edgeMarginPx, containerWidthPx - btnSizePx - edgeMarginPx)
                                    dragOffsetY = (dragOffsetY + dragAmount.y)
                                        .coerceIn(edgeMarginPx * 4f, containerHeightPx - btnSizePx - edgeMarginPx * 8f)
                                }
                            )
                        }
                ) {
                    FloatingControlButton(
                        config = config,
                        palette = palette,
                        contentDescription = strings.pullDownControlCenter,
                        onClick = { viewModel.openInAppControlCenter() }
                    )
                }
            }

            // In-App Top-Anchored Control Center Shade Overlay
            TopAnchoredControlCenterOverlay(
                visible = isPanelOpen,
                expanded = isPanelExpanded,
                config = config,
                palette = palette,
                strings = strings,
                systemState = systemState,
                onExpandedChange = { viewModel.setInAppPanelExpanded(it) },
                onDismiss = { viewModel.closeInAppControlCenter() },
                onTileClick = { tile -> viewModel.onTileTapped(tile) },
                onBrightnessChange = { viewModel.onBrightnessChanged(it) },
                onBrightnessChangeFinished = { viewModel.onBrightnessChangeFinished() },
                onVolumeChange = { viewModel.onVolumeChanged(it) },
                onMediaKey = { viewModel.deviceManager.dispatchMediaKey(it) },
                onOpenMusicApp = {
                    viewModel.deviceManager.openMusicPlayer()
                    viewModel.closeInAppControlCenter()
                },
                onOpenVolumePanel = { viewModel.deviceManager.openVolumePanel() },
                onOpenDisplaySettings = { viewModel.deviceManager.openDisplaySettings() },
                onOpenBatterySettings = { viewModel.deviceManager.openBatterySettings() },
                onOpenDeviceSettings = { viewModel.deviceManager.openSystemSettings() },
                onOpenAppSettings = {
                    viewModel.closeInAppControlCenter()
                    navController.navigate(ROUTE_SETTINGS) { launchSingleTop = true }
                },
                onOpenCustomize = {
                    viewModel.closeInAppControlCenter()
                    navController.navigate(ROUTE_STUDIO) { launchSingleTop = true }
                }
            )
        }
    }
}

@Composable
private fun StudioDetailRoute(
    tab: StudioTab,
    config: com.example.data.ControlCenterConfig,
    palette: com.example.data.ResolvedVisualPalette,
    strings: com.example.data.UiStrings,
    systemState: com.example.system.DeviceSystemState,
    viewModel: ControlCenterViewModel,
    contentPadding: PaddingValues,
    onBack: () -> Unit
) {
    CustomizationStudioScreen(
        initialTab = tab,
        showBackButton = true,
        config = config,
        palette = palette,
        strings = strings,
        systemState = systemState,
        viewModel = viewModel,
        onBack = onBack,
        contentPadding = contentPadding
    )
}
