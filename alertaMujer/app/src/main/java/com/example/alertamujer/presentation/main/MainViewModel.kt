package com.example.alertamujer.presentation.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.alertamujer.data.local.AppDatabase

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val prefsSettings = application.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    // Reactivo: Si la BD cambia, el número de contactos se recalcula solo
    val numeroContactos: LiveData<Int> = db.contactoDao().obtenerTodosLosContactos().map { it.size }

    private val _isDarkModeOn = MutableLiveData<Boolean>()
    val isDarkModeOn: LiveData<Boolean> get() = _isDarkModeOn

    fun cargarDatosGenerales() {
        // Carga el tema una sola vez o cuando sea necesario
        _isDarkModeOn.value = prefsSettings.getBoolean("dark_mode", false)
    }
}