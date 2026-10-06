package com.example.alertamujer.presentation.alerta

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel


class AutoridadesViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("PrefsAutoridades", Context.MODE_PRIVATE)

    fun obtenerNumeroPolicia(): String = prefs.getString("num_policia", "123") ?: "123"
    fun obtenerNumeroBomberos(): String = prefs.getString("num_bomberos", "119") ?: "119"
    fun obtenerNumeroAmbulancia(): String = prefs.getString("num_ambulancia", "125") ?: "125"

    fun guardarNumeros(policia: String, bomberos: String, ambulancia: String) {
        prefs.edit().apply {
            putString("num_policia", policia.trim())
            putString("num_bomberos", bomberos.trim())
            putString("num_ambulancia", ambulancia.trim())
            apply()
        }
    }
}