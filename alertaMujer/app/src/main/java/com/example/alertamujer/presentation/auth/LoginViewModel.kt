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
import com.example.alertamujer.util.SessionManager
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()

    // AHORA USAMOS LA BÓVEDA SEGURA PARA GUARDAR LA SESIÓN
    private val sessionManager = SessionManager(application)

    private val _navegarAMain = MutableLiveData<Boolean>()
    val navegarAMain: LiveData<Boolean> get() = _navegarAMain

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError: LiveData<String> get() = _mensajeError

    init {
        // Lee el estado desde la bóveda encriptada
        if (sessionManager.estaLogueado()) {
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
                    val data = response.body()

                    if (data?.success == true) {
                        val id = data.id_usuario ?: -1
                        val tokenJwt = data.token ?: ""

                        // GUARDADO SEGURO
                        guardarSesion(id, tokenJwt)
                        vincularDispositivoConFCM(id)

                        _navegarAMain.value = true
                    } else {
                        _mensajeError.value = data?.mensaje ?: "Error en las credenciales"
                    }
                } else {
                    _mensajeError.value = "Error en el servidor: ${response.code()}"
                }
            } catch (e: Exception) {
                _mensajeError.value = "Error de conexión: Verifique su internet"
            }
        }
    }

    private fun vincularDispositivoConFCM(idUsuario: Int) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Error al obtener token", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result

            viewModelScope.launch {
                try {
                    repository.actualizarTokenFCM(idUsuario, token)
                    Log.d("FCM", "Token registrado con éxito en el servidor")
                } catch (e: Exception) {
                    Log.e("FCM", "Error al registrar token en el backend", e)
                }
            }
        }
    }

    private fun guardarSesion(idUsuario: Int, token: String) {
        // Todo se guarda a través del SessionManager
        sessionManager.guardarEstadoLogin(true)
        sessionManager.guardarIdUsuario(idUsuario)
        sessionManager.guardarToken(token)
    }
}