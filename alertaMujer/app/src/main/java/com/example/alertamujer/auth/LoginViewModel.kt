package com.example.alertamujer.auth

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("AlertaMujerPrefs", Context.MODE_PRIVATE)

    // Señal para navegar al menú principal
    private val _navegarAMain = MutableLiveData<Boolean>()
    val navegarAMain: LiveData<Boolean> get() = _navegarAMain

    // Señal para mostrar errores en pantalla
    private val _mensajeError = MutableLiveData<String>()
    val mensajeError: LiveData<String> get() = _mensajeError

    init {
        // EL GUARDIÁN DE SESIÓN: Apenas nace el ViewModel, revisa si ya hay sesión
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            _navegarAMain.value = true
        }
    }

    fun intentarLogin(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) {
            // Si hay error, disparamos la señal con el mensaje
            _mensajeError.value = "Por favor, ingresa tus credenciales"
            return
        }

        // --- ESTRUCTURA PARA FUTURA VALIDACIÓN CON BD ---
        // Aquí conectarás con tu Spring Boot o MySQL. Por ahora simulamos el éxito.

        // GUARDAR LA SESIÓN
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.apply()

        // Disparamos la señal para ir al MainActivity
        _navegarAMain.value = true
    }
}