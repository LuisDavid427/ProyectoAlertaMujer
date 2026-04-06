package com.example.alertamujer.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.json.JSONArray

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Referencias a las memorias
    private val prefsContactos = application.getSharedPreferences("contactos_prefs", Context.MODE_PRIVATE)
    private val prefsSettings = application.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    // LiveData para el número de contactos
    private val _numeroContactos = MutableLiveData<Int>()
    val numeroContactos: LiveData<Int> get() = _numeroContactos

    // LiveData para saber si el modo oscuro debe estar activado
    private val _isDarkModeOn = MutableLiveData<Boolean>()
    val isDarkModeOn: LiveData<Boolean> get() = _isDarkModeOn

    // Esta función se llamará cada vez que la pantalla vuelva a estar visible (onResume)
    fun cargarDatosGenerales() {
        // 1. Lógica para contar contactos (JSON)
        val contactosJson = prefsContactos.getString("contactos", "[]")
        val contactosArray = JSONArray(contactosJson)
        _numeroContactos.value = contactosArray.length()

        // 2. Lógica para saber el tema preferido
        _isDarkModeOn.value = prefsSettings.getBoolean("dark_mode", false)
    }
}