package com.example.alertamujer.presentation.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.alertamujer.data.local.AppDatabase
import com.example.alertamujer.util.AppSettingsPreferences

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val appSettingsPreferences = AppSettingsPreferences(application)

    // Reactivo: Si la BD cambia, el número de contactos se recalcula solo
    val numeroContactos: LiveData<Int> = db.contactoDao().obtenerTodosLosContactos().map { it.size }

    private val _isDarkModeOn = MutableLiveData<Boolean>()
    val isDarkModeOn: LiveData<Boolean> get() = _isDarkModeOn

    fun cargarDatosGenerales() {
        _isDarkModeOn.value = appSettingsPreferences.isDarkModeEnabled()
    }
}
