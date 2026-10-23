package com.example.alertamujer.util

import android.content.Context

class SessionManager(context: Context) {

    private val authSessionManager = AuthSessionManager(context)
    private val userPreferences = UserPreferences(context)
    private val emergencyPreferences = EmergencyPreferences(context)
    private val notificationPreferences = NotificationPreferences(context)

    fun guardarToken(token: String) = authSessionManager.guardarToken(token)
    fun obtenerToken(): String? = authSessionManager.obtenerToken()

    fun guardarIdUsuario(id: Int) = authSessionManager.guardarIdUsuario(id)
    fun obtenerIdUsuario(): Int = authSessionManager.obtenerIdUsuario()

    fun guardarNombreUsuario(nombre: String) = userPreferences.guardarNombreUsuario(nombre)
    fun obtenerNombreUsuario(): String? = userPreferences.obtenerNombreUsuario()

    fun guardarEstadoLogin(estado: Boolean) = authSessionManager.guardarEstadoLogin(estado)
    fun estaLogueado(): Boolean = authSessionManager.estaLogueado()

    fun guardarMensajeSOS(mensaje: String) = emergencyPreferences.guardarMensajeSOS(mensaje)
    fun obtenerMensajeSOS(): String = emergencyPreferences.obtenerMensajeSOS()

    fun guardarEstadoBurbuja(estado: Boolean) = emergencyPreferences.guardarEstadoBurbuja(estado)
    fun isBurbujaActivada(): Boolean = emergencyPreferences.isBurbujaActivada()

    fun limpiarSesion() = authSessionManager.limpiarSesion()

    fun guardarCredenciales(correo: String, pass: String) = authSessionManager.guardarCredenciales(correo, pass)
    fun obtenerEmail(): String? = authSessionManager.obtenerEmail()
    fun obtenerPassword(): String? = authSessionManager.obtenerPassword()

    fun guardarFcmToken(fcmToken: String) = notificationPreferences.guardarFcmToken(fcmToken)
    fun obtenerFcmToken(): String? = notificationPreferences.obtenerFcmToken()
}
