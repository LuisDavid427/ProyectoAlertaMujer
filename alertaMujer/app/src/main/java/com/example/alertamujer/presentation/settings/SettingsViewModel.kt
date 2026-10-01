package com.example.alertamujer.presentation.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.alertamujer.util.SessionManager


class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)
    private val sharedPreferences = application.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    private val _isDarkMode = MutableLiveData<Boolean>()
    val isDarkMode: LiveData<Boolean> get() = _isDarkMode

    private val _nombreUsuario = MutableLiveData<String>()
    val nombreUsuario: LiveData<String> get() = _nombreUsuario

    private val _emailUsuario = MutableLiveData<String>()
    val emailUsuario: LiveData<String> get() = _emailUsuario

    private val _cerrarSesionEvento = MutableLiveData<Boolean>()
    val cerrarSesionEvento: LiveData<Boolean> get() = _cerrarSesionEvento

    init {
        _isDarkMode.value = sharedPreferences.getBoolean("dark_mode", false)
        cargarDatosUsuario()
    }

    private fun cargarDatosUsuario() {
        _nombreUsuario.value = sessionManager.obtenerNombreUsuario() ?: "Usuario"
        _emailUsuario.value = sessionManager.obtenerEmail() ?: "Correo no registrado"
    }

    fun updateTheme(isDark: Boolean) {
        sharedPreferences.edit().putBoolean("dark_mode", isDark).apply()
        _isDarkMode.value = isDark
    }

    fun cerrarSesion() {
        sessionManager.limpiarSesion()
        _cerrarSesionEvento.value = true
    }
}