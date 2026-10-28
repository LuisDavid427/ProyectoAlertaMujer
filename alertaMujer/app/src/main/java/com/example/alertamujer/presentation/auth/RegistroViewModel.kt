package com.example.alertamujer.presentation.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.alertamujer.data.dto.RegistroRequest
import com.example.alertamujer.data.dto.AuthResponse
import com.example.alertamujer.data.network.repository.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    // Capa de Datos: Instanciamos el repositorio
    private val repository = AuthRepository(application)

    private val _registroExitoso = MutableLiveData<Boolean>()
    val registroExitoso: LiveData<Boolean> get() = _registroExitoso

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError: LiveData<String> get() = _mensajeError

    fun intentarRegistro(nombre: String, email: String, pass: String, fechaNacimiento: String) { // <-- 1. Añade el parámetro
        // 2. Validación de campos
        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty() || fechaNacimiento.isEmpty()) {
            _mensajeError.value = "Completa todos los campos"
            return
        }

        viewModelScope.launch {
            try {
                val request = RegistroRequest(
                    nombre = nombre,
                    email = email,
                    contrasena = pass,
                    fechaNacimiento = fechaNacimiento // <-- 3. Añádelo al objeto
                )
                // ... resto del código igual

                // 2. Llamada al Repositorio (Capa de Datos)
                val response: Response<AuthResponse> = repository.registrar(request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()
                    if (body?.success == true) {
                        _registroExitoso.value = true
                    } else {
                        // El servidor respondió con éxito pero el negocio rechazó el registro
                        _mensajeError.value = body?.mensaje ?: "Error en el registro"
                    }
                } else {
                    // ⚠️ AQUÍ CAEN LOS ERRORES HTTP REALES (400, 401, 500, etc.)
                    val errorRaw = response.errorBody()?.string() ?: "Error desconocido"

                    // Esto te mostrará el código y el JSON exacto que manda Render en la pantalla del celular
                    _mensajeError.value = "HTTP ${response.code()}: $errorRaw"
                }
            } catch (e: Exception) {
                // Error de infraestructura (Sin internet, servidor caído)
                _mensajeError.value = "Fallo de conexión: Verifique su internet"
            }
        }
    }
}