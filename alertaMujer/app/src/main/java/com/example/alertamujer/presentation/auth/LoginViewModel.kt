package com.example.alertamujer.presentation.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.alertamujer.data.dto.LoginRequest
import com.example.alertamujer.data.dto.AuthResponse
import com.example.alertamujer.data.network.repository.AuthRepository
import com.example.alertamujer.util.FcmUtil
import com.example.alertamujer.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)
    private val sessionManager = SessionManager(application)

    private val _navegarAMain = MutableLiveData<Boolean>()
    val navegarAMain: LiveData<Boolean> get() = _navegarAMain

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError: LiveData<String> get() = _mensajeError

    init {
        // Sincroniza FCM si la sesión ya estaba activa previamente al abrir la app
        if (sessionManager.estaLogueado()) {
            FcmUtil.sincronizarTokenSesion(getApplication())
            _navegarAMain.value = true
        }
    }

    fun intentarLogin(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) {
            _mensajeError.value = "Por favor, completa todos los campos"
            return
        }

        viewModelScope.launch {
            try {
                val request = LoginRequest(email = email, password = pass)
                val response: Response<AuthResponse> = repository.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!

                    if (data.success) {
                        val id = data.id_usuario ?: -1
                        val tokenJwt = data.token ?: ""
                        val nombreApi = data.nombre ?: "Usuario"

                        Log.e("DEBUG_NOMBRE", "Nombre mapeado desde la API: '$nombreApi'")

                        // 1. Guardar la sesión de forma sincrónica en memoria/disco
                        guardarSesion(id, tokenJwt, email, pass, nombreApi)

                        // 2. Ejecutar la sincronización del token FCM garantizando que el JWT ya existe
                        withContext(Dispatchers.Main) {
                            FcmUtil.sincronizarTokenSesion(getApplication())
                        }

                        // 3. Redirigir a MainActivity
                        _navegarAMain.value = true
                    } else {
                        _mensajeError.value = data.mensaje ?: "Error en las credenciales"
                    }
                } else {
                    _mensajeError.value = "Error en el servidor: ${response.code()}"
                }
            } catch (e: Exception) {
                _mensajeError.value = "Error de conexión: Verifique su internet"
            }
        }
    }

    private fun guardarSesion(idUsuario: Int, token: String, email: String, pass: String, nombreUsuario: String) {
        sessionManager.guardarEstadoLogin(true)
        sessionManager.guardarIdUsuario(idUsuario)
        sessionManager.guardarToken(token)
        sessionManager.guardarNombreUsuario(nombreUsuario)
        sessionManager.guardarCredenciales(email, pass)
    }
}