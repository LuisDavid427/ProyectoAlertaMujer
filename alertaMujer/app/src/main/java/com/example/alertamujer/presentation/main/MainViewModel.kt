package com.example.alertamujer.presentation.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.json.JSONArray

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsContactos = application.getSharedPreferences("contactos_prefs", Context.MODE_PRIVATE)
    private val prefsSettings = application.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
    private val _numeroContactos = MutableLiveData<Int>()
    val numeroContactos: LiveData<Int> get() = _numeroContactos
    private val _isDarkModeOn = MutableLiveData<Boolean>()
    val isDarkModeOn: LiveData<Boolean> get() = _isDarkModeOn

    fun cargarDatosGenerales() {
        // 1. Lógica para contar contactos (JSON)
        val contactosJson = prefsContactos.getString("contactos", "[]")
        val contactosArray = JSONArray(contactosJson)
        _numeroContactos.value = contactosArray.length()

        // 2. Lógica para saber el tema preferido
        _isDarkModeOn.value = prefsSettings.getBoolean("dark_mode", false)
    }
}