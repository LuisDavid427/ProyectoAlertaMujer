package com.example.alertamujer.auth

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegistroViewModel : ViewModel() {

    // Señal para avisar que el registro fue exitoso
    private val _registroExitoso = MutableLiveData<Boolean>()
    val registroExitoso: LiveData<Boolean> get() = _registroExitoso

    // Señal para mostrar errores de validación
    private val _mensajeError = MutableLiveData<String>()
    val mensajeError: LiveData<String> get() = _mensajeError

    fun intentarRegistro(nombre: String, email: String, pass: String) {
        // 1. Validaciones lógicas
        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            _mensajeError.value = "Por favor, completa todos los campos"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _mensajeError.value = "El correo no es válido"
            return
        }

        // 2. Simulación de guardado (Aquí irá tu petición POST a Spring Boot/MySQL)
        // Por ahora simulamos éxito inmediato
        _registroExitoso.value = true
    }
}