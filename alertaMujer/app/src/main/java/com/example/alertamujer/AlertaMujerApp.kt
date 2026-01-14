package com.example.alertamujer

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class AlertaMujerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
