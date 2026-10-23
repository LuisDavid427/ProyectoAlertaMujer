package com.example.alertamujer.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EmergencyPreferences(context: Context) {

    private val appContext = context.applicationContext

    private val masterKey = MasterKey.Builder(appContext)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        appContext,
        "emergency_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val MENSAJE_SOS = "mensaje_sos"
        private const val BURBUJA_ACTIVADA = "burbuja_activada"
    }

    fun guardarMensajeSOS(mensaje: String) {
        sharedPreferences.edit().putString(MENSAJE_SOS, mensaje).apply()
    }

    fun obtenerMensajeSOS(): String =
        sharedPreferences.getString(MENSAJE_SOS, "¡Auxilio! Necesito ayuda inmediata.")
            ?: "¡Auxilio! Necesito ayuda inmediata."

    fun guardarEstadoBurbuja(estado: Boolean) {
        sharedPreferences.edit().putBoolean(BURBUJA_ACTIVADA, estado).apply()
    }

    fun isBurbujaActivada(): Boolean = sharedPreferences.getBoolean(BURBUJA_ACTIVADA, false)
}
