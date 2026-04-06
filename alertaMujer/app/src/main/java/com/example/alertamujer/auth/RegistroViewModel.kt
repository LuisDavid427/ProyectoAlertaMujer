package com.example.alertamujer.auth

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alertamujer.api.RetrofitClient
import kotlinx.coroutines.launch
import com.example.alertamujer.api.RegistroRequest



class RegistroViewModel : ViewModel() {

    // Señal para avisar que el registro fue exitoso
    private val _registroExitoso = MutableLiveData<Boolean>()
    val registroExitoso: LiveData<Boolean> get() = _registroExitoso

    // Señal para mostrar errores de validación
    private val _mensajeError = MutableLiveData<String>()
    val mensajeError: LiveData<String> get() = _mensajeError

    fun intentarRegistro(nombre: String, email: String, pass: String) {
        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            _mensajeError.value = "Completa todos los campos"
            return
        }

        viewModelScope.launch {
            try {
                val request = RegistroRequest(nombre, email, pass)
                val response = RetrofitClient.instance.registrar(request)

                if (response.isSuccessful) {
                    _registroExitoso.value = true
                } else {
                    // --- ESTE ES EL CAMBIO CLAVE ---
                    // Leemos el cuerpo del error crudo que envía Spring Boot
                    val errorRaw = response.errorBody()?.string() ?: "Error desconocido"
                    _mensajeError.value = "Error del Servidor: $errorRaw"
                }
            } catch (e: Exception) {
                _mensajeError.value = "Fallo de conexión: ${e.message}"
            }
        }
    }
}