package com.example.alertamujer.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SessionManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "AlertaMujerPrefs_Seguro",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun guardarToken(token: String) { sharedPreferences.edit().putString("token_jwt", token).apply() }
    fun obtenerToken(): String? { return sharedPreferences.getString("token_jwt", null) }

    fun guardarIdUsuario(id: Int) { sharedPreferences.edit().putInt("id_usuario", id).apply() }
    fun obtenerIdUsuario(): Int { return sharedPreferences.getInt("id_usuario", -1) }

    // Funciones nuevas para el Login
    fun guardarEstadoLogin(estado: Boolean) { sharedPreferences.edit().putBoolean("isLoggedIn", estado).apply() }
    fun estaLogueado(): Boolean { return sharedPreferences.getBoolean("isLoggedIn", false) }

    fun limpiarSesion() { sharedPreferences.edit().clear().apply() }
}