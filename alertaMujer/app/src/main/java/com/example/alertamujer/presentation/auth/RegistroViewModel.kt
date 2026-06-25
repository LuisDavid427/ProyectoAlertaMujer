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
    private val repository = AuthRepository()

    private val _registroExitoso = MutableLiveData<Boolean>()
    val registroExitoso: LiveData<Boolean> get() = _registroExitoso

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError: LiveData<String> get() = _mensajeError

    fun intentarRegistro(nombre: String, email: String, pass: String) {
        // 1. Validación de campos (Lógica de presentación)
        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            _mensajeError.value = "Completa todos los campos"
            return
        }

        viewModelScope.launch {
            try {
                val request = RegistroRequest(
                    nombre = nombre,
                    email = email,
                    contrasena = pass
                )

                // 2. Llamada al Repositorio (Capa de Datos)
                val response: Response<AuthResponse> = repository.registrar(request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()
                    if (body?.success == true) {
                        _registroExitoso.value = true
                    } else {
                        // El servidor respondió pero con un error de negocio (ej: correo ya registrado)
                        _mensajeError.value = body?.mensaje ?: "Error en el registro"
                    }
                } else {
                    // Error de código HTTP (400, 500, etc.)
                    val errorRaw = response.errorBody()?.string() ?: "Error desconocido"
                    _mensajeError.value = "Error del Servidor: $errorRaw"
                }
            } catch (e: Exception) {
                // Error de infraestructura (Sin internet, servidor caído)
                _mensajeError.value = "Fallo de conexión: Verifique su internet"
            }
        }
    }
}