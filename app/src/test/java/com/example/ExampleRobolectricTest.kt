package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppLanguage
import com.example.data.ControlCenterConfig
import com.example.data.LocalizationProvider
import com.example.data.PreferencesRepository
import com.example.data.ThemePreset
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `app name matches One X Control`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("One X Control", appName)
    }

    @Test
    fun `arabic and english localization resolve correctly`() {
        val ar = LocalizationProvider.resolve(AppLanguage.ARABIC)
        val en = LocalizationProvider.resolve(AppLanguage.ENGLISH)
        assertTrue(ar.isArabic)
        assertEquals("ONE X CONTROL", ar.controlCenterHeader)
        assertEquals("ONE X CONTROL", en.controlCenterHeader)
        assertTrue(en.pullDownControlCenter.isNotBlank())
    }

    @Test
    fun `datastore persists config and json backup round-trips`() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = PreferencesRepository(context)
        val custom = ControlCenterConfig(
            initialVisibleTiles = 8,
            gridColumns = 5,
            themePreset = ThemePreset.PURPLE_GALAXY,
            backgroundAlpha = 0.75f,
            language = AppLanguage.ARABIC
        )
        repo.saveConfig(custom)
        val loaded = repo.configFlow.first()
        assertEquals(8, loaded.initialVisibleTiles)
        assertEquals(5, loaded.gridColumns)
        assertEquals(ThemePreset.PURPLE_GALAXY, loaded.themePreset)

        val json = repo.exportConfigJson()
        repo.resetToDefaults()
        val importResult = repo.importConfigJson(json)
        assertTrue(importResult.isSuccess)
        val restored = repo.configFlow.first()
        assertEquals(8, restored.initialVisibleTiles)
        assertEquals(5, restored.gridColumns)
        assertEquals(ThemePreset.PURPLE_GALAXY, restored.themePreset)
    }
}
