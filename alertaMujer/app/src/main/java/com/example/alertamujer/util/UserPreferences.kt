package com.example.alertamujer.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class UserPreferences(context: Context) {

    private val appContext = context.applicationContext

    private val masterKey = MasterKey.Builder(appContext)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        appContext,
        "user_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val NOMBRE_USUARIO = "nombre_usuario"
    }

    fun guardarNombreUsuario(nombre: String) {
        sharedPreferences.edit().putString(NOMBRE_USUARIO, nombre).apply()
    }

    fun obtenerNombreUsuario(): String? = sharedPreferences.getString(NOMBRE_USUARIO, null)
}
