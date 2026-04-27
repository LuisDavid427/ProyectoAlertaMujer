package com.example.alertamujer.presentation.auth

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.alertamujer.data.dto.LoginRequest
import com.example.alertamujer.data.dto.AuthResponse
import com.example.alertamujer.data.repository.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    // Capa de Datos: Inyectamos el repositorio
    private val repository = AuthRepository()

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("AlertaMujerPrefs", Context.MODE_PRIVATE)

    // LiveData para manejar la navegación hacia el Main
    private val _navegarAMain = MutableLiveData<Boolean>()
    val navegarAMain: LiveData<Boolean> get() = _navegarAMain

    // LiveData para manejar los mensajes de error en la UI
    private val _mensajeError = MutableLiveData<String>()
    val mensajeError: LiveData<String> get() = _mensajeError

    init {
        // Verificación automática de sesión al arrancar
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            _navegarAMain.value = true
        }
    }

    fun intentarLogin(email: String, pass: String) {
        // Validaciones básicas de entrada antes de disparar la red
        if (email.isEmpty() || pass.isEmpty()) {
            _mensajeError.value = "Por favor, completa todos los campos"
            return
        }

        viewModelScope.launch {
            try {
                val request = LoginRequest(email = email, password = pass)

                // Usamos el Repositorio, respetando la arquitectura de capas
                val response: Response<AuthResponse> = repository.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()

                    if (data?.success == true) {
                        guardarSesion(data.id_usuario ?: -1)
                        _navegarAMain.value = true
                    } else {
                        _mensajeError.value = data?.mensaje ?: "Error en las credenciales"
                    }
                } else {
                    _mensajeError.value = "Error en el servidor: ${response.code()}"
                }
            } catch (e: Exception) {
                // Manejo de errores de red (Sin internet, timeout, etc.)
                _mensajeError.value = "Error de conexión: Verifique su internet"
            }
        }
    }

    private fun guardarSesion(idUsuario: Int) {
        sharedPreferences.edit().apply {
            putBoolean("isLoggedIn", true)
            putInt("id_usuario", idUsuario)
            apply()
        }
    }
}