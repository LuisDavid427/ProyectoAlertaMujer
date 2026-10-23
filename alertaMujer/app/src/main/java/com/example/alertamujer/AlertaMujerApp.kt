package com.example.alertamujer

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.messaging.FirebaseMessaging

class AlertaMujerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        applyTheme()
        registerFcmToken()
    }

    private fun applyTheme() {
        val sharedPreferences = getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun registerFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fallo al obtener el token FCM", task.exception)
                return@addOnCompleteListener
            }

            val fcmToken = task.result
            val sessionManager = com.example.alertamujer.util.SessionManager(this)
            sessionManager.guardarFcmToken(fcmToken)
        }
    }
}
