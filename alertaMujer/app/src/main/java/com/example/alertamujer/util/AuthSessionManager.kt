package com.example.alertamujer.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class AuthSessionManager(context: Context) {

    private val appContext = context.applicationContext

    private val masterKey = MasterKey.Builder(appContext)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        appContext,
        "auth_session_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val TOKEN_JWT = "token_jwt"
        private const val ID_USUARIO = "id_usuario"
        private const val IS_LOGGED_IN = "isLoggedIn"
        private const val USER_EMAIL = "user_correo"
        private const val USER_PASSWORD = "user_password"
    }

    fun guardarToken(token: String) {
        sharedPreferences.edit().putString(TOKEN_JWT, token).apply()
    }

    fun obtenerToken(): String? = sharedPreferences.getString(TOKEN_JWT, null)

    fun guardarIdUsuario(id: Int) {
        sharedPreferences.edit().putInt(ID_USUARIO, id).apply()
    }

    fun obtenerIdUsuario(): Int = sharedPreferences.getInt(ID_USUARIO, -1)

    fun guardarEstadoLogin(estado: Boolean) {
        sharedPreferences.edit().putBoolean(IS_LOGGED_IN, estado).apply()
    }

    fun estaLogueado(): Boolean = sharedPreferences.getBoolean(IS_LOGGED_IN, false)

    fun limpiarSesion() {
        sharedPreferences.edit().clear().apply()
    }

    fun guardarCredenciales(correo: String, pass: String) {
        sharedPreferences.edit()
            .putString(USER_EMAIL, correo)
            .putString(USER_PASSWORD, pass)
            .apply()
    }

    fun obtenerEmail(): String? = sharedPreferences.getString(USER_EMAIL, null)
    fun obtenerPassword(): String? = sharedPreferences.getString(USER_PASSWORD, null)
}
