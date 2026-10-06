package com.example.alertamujer.presentation.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.alertamujer.data.network.repository.AuthRepository
import kotlinx.coroutines.launch

class VerificarCodigoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    private val _navegarACambiarPass = MutableLiveData<Pair<String, String>>()
    val navegarACambiarPass: LiveData<Pair<String, String>> get() = _navegarACambiarPass

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError: LiveData<String> get() = _mensajeError

    fun verificarCodigo(email: String, codigo: String) {
        if (codigo.length != 6) {
            _mensajeError.value = "Ingresa un código válido de 6 dígitos"
            return
        }

        viewModelScope.launch {
            try {
                // Consulta real al backend para validar el código de 6 dígitos
                val response = repository.verificarCodigo(email, codigo)
                val body = response.body()

                if (response.isSuccessful && body != null && body.success) {
                    // Si el backend lo aprueba, avanzamos a la pantalla de cambiar contraseña
                    _navegarACambiarPass.value = Pair(email, codigo)
                } else {
                    // Si el backend lo rechaza (código falso o expirado), frena el flujo aquí mismo
                    _mensajeError.value = "Código inválido o expirado"
                }
            } catch (e: Exception) {
                _mensajeError.value = "Error de conexión al verificar el código"
            }
        }
    }
}