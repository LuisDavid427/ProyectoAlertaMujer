package com.example.alertamujer.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class NotificationPreferences(context: Context) {

    private val appContext = context.applicationContext

    private val masterKey = MasterKey.Builder(appContext)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        appContext,
        "notification_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val FCM_TOKEN = "fcm_token"
    }

    fun guardarFcmToken(fcmToken: String) {
        sharedPreferences.edit().putString(FCM_TOKEN, fcmToken).apply()
    }

    fun obtenerFcmToken(): String? = sharedPreferences.getString(FCM_TOKEN, null)
}
