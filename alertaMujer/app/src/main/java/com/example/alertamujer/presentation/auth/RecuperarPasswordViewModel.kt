package com.example.alertamujer.presentation.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.alertamujer.data.dto.ForgotPasswordRequest
import com.example.alertamujer.data.network.repository.AuthRepository
import kotlinx.coroutines.launch

class RecuperarPasswordViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    private val _navegarAVerificar = MutableLiveData<String>()
    val navegarAVerificar: LiveData<String> get() = _navegarAVerificar

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError: LiveData<String> get() = _mensajeError

    fun solicitarCodigo(email: String) {
        if (email.isEmpty()) {
            _mensajeError.value = "Por favor, ingresa tu correo electrónico"
            return
        }

        viewModelScope.launch {
            try {
                val request = ForgotPasswordRequest(email)
                val response = repository.solicitarRecuperacion(request)
                val body = response.body()
                val code = response.code()

                Log.d("RecuperarPassVM", "HTTP Code: $code")
                Log.d("RecuperarPassVM", "Response Body: $body")

                if (response.isSuccessful && body != null && body.success == true) {
                    _navegarAVerificar.value = email
                } else {
                    val errorMsg = body?.mensaje ?: "El correo no está registrado en el sistema"
                    _mensajeError.value = errorMsg
                }
            } catch (e: Exception) {
                Log.e("RecuperarPassVM", "Excepción de red: ${e.message}", e)
                _mensajeError.value = "Error de conexión. Verifica tu internet."
            }
        }
    }
}