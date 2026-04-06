package com.example.alertamujer.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    // El ViewModel se encarga de la lógica de datos (SharedPreferences)
    private val sharedPreferences = application.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    // Usamos LiveData para avisarle a la vista cómo debe estar el Switch
    private val _isDarkMode = MutableLiveData<Boolean>()
    val isDarkMode: LiveData<Boolean> get() = _isDarkMode

    init {
        // Apenas se crea el ViewModel, lee la memoria y actualiza el LiveData
        _isDarkMode.value = sharedPreferences.getBoolean("dark_mode", false)
    }

    // Función que llamará la Actividad cuando el usuario toque el Switch
    fun updateTheme(isDark: Boolean) {
        // 1. Guarda en memoria
        sharedPreferences.edit().putBoolean("dark_mode", isDark).apply()
        // 2. Actualiza el estado
        _isDarkMode.value = isDark
    }

    // Dejamos lista la función para cuando implementes el botón de guardar cuenta
    fun saveCredentials(username: String, pass: String) {
        // TODO: Lógica para guardar usuario y contraseña
        // Aquí puedes hacer validaciones (ej. si están vacíos) sin ensuciar la Actividad
    }
}