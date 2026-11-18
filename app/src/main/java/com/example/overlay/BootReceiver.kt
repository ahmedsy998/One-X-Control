package com.example.overlay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.example.data.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action != Intent.ACTION_BOOT_COMPLETED && action != Intent.ACTION_LOCKED_BOOT_COMPLETED) {
            return
        }
        if (!Settings.canDrawOverlays(context)) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repo = PreferencesRepository(context.applicationContext)
                val config = repo.configFlow.first()
                if (config.floatingButtonEnabled && config.floatingButtonVisible) {
                    val serviceIntent = Intent(context.applicationContext, FloatingControlService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.applicationContext.startForegroundService(serviceIntent)
                    } else {
                        context.applicationContext.startService(serviceIntent)
                    }
                }
            } catch (_: Exception) {
                // Respect Android 14 background start restrictions gracefully without crashing
            } finally {
                pendingResult.finish()
            }
        }
    }
}
