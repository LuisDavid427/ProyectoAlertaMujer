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

    fun guardarToken(token: String) {
        sharedPreferences.edit().putString("token_jwt", token).apply()
    }

    fun obtenerToken(): String? {
        return sharedPreferences.getString("token_jwt", null)
    }

    fun guardarIdUsuario(id: Int) {
        sharedPreferences.edit().putInt("id_usuario", id).apply()
    }

    fun obtenerIdUsuario(): Int {
        return sharedPreferences.getInt("id_usuario", -1)
    }

    // 🟢 NOMBRE DE USUARIO (Persistencia segura)
    fun guardarNombreUsuario(nombre: String) {
        sharedPreferences.edit().putString("nombre_usuario", nombre).apply()
    }

    fun obtenerNombreUsuario(): String? {
        return sharedPreferences.getString("nombre_usuario", null)
    }

    // Funciones de Login
    fun guardarEstadoLogin(estado: Boolean) {
        sharedPreferences.edit().putBoolean("isLoggedIn", estado).apply()
    }

    fun estaLogueado(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    // Mensaje SOS
    fun guardarMensajeSOS(mensaje: String) {
        sharedPreferences.edit().putString("mensaje_sos", mensaje).apply()
    }

    fun obtenerMensajeSOS(): String {
        return sharedPreferences.getString("mensaje_sos", "¡Auxilio! Necesito ayuda inmediata.")
            ?: "¡Auxilio! Necesito ayuda inmediata."
    }

    // 🟢 ESTADO DE LA BURBUJA FLOTANTE
    fun guardarEstadoBurbuja(estado: Boolean) {
        sharedPreferences.edit().putBoolean("burbuja_activada", estado).apply()
    }

    fun isBurbujaActivada(): Boolean {
        return sharedPreferences.getBoolean("burbuja_activada", false)
    }

    fun limpiarSesion() {
        sharedPreferences.edit().clear().apply()
    }

    fun guardarCredenciales(correo: String, pass: String) {
        sharedPreferences.edit()
            .putString("user_correo", correo)
            .putString("user_password", pass)
            .apply()
    }

    fun obtenerEmail(): String? = sharedPreferences.getString("user_correo", null)
    fun obtenerPassword(): String? = sharedPreferences.getString("user_password", null)

    // 🟢 TOKEN FCM (Firebase Cloud Messaging)
    fun guardarFcmToken(fcmToken: String) {
        sharedPreferences.edit().putString("fcm_token", fcmToken).apply()
    }

    fun obtenerFcmToken(): String? {
        return sharedPreferences.getString("fcm_token", null)
    }
}