package com.example.alertamujer.util

import android.content.Context

class AppSettingsPreferences(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    fun isDarkModeEnabled(): Boolean = prefs.getBoolean("dark_mode", false)

    fun setDarkModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("dark_mode", enabled).apply()
    }
}
