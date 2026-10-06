package com.example.alertamujer.presentation.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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
                val response = repository.solicitarRecuperacion(email)
                val body = response.body()

                if (response.isSuccessful && body?.success == true) {
                    _navegarAVerificar.value = email
                } else {
                    // Intentamos extraer el mensaje exacto que mandó el backend (404)
                    // O usamos uno por defecto si el cuerpo viene vacío
                    _mensajeError.value = "El correo no está registrado en el sistema"
                }
            } catch (e: Exception) {
                _mensajeError.value = "Error de conexión. Verifica tu internet."
            }
        }
    }
}