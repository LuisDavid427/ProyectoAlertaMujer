package com.example.alertamujer.presentation.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.alertamujer.data.dto.ResetPasswordRequest
import com.example.alertamujer.data.network.repository.AuthRepository
import kotlinx.coroutines.launch

class CambiarPasswordViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    private val _cambioExitoso = MutableLiveData<Boolean>()
    val cambioExitoso: LiveData<Boolean> get() = _cambioExitoso

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError: LiveData<String> get() = _mensajeError

    fun actualizarPassword(email: String, codigo: String, pass: String, confirmarPass: String) {
        if (pass.isEmpty() || confirmarPass.isEmpty()) {
            _mensajeError.value = "Completa todos los campos"
            return
        }

        if (pass != confirmarPass) {
            _mensajeError.value = "Las contraseñas no coinciden"
            return
        }

        if (pass.length < 6) {
            _mensajeError.value = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        viewModelScope.launch {
            try {
                val request = ResetPasswordRequest(
                    email = email,
                    codigo = codigo,
                    nuevaPassword = pass
                )

                val response = repository.cambiarPassword(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    _cambioExitoso.value = true
                } else {
                    _mensajeError.value = "Código inválido o expirado"
                }
            } catch (e: Exception) {
                _mensajeError.value = "Error de conexión al actualizar"
            }
        }
    }
}