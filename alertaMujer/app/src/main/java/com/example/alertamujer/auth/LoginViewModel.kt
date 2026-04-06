package com.example.alertamujer.auth

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.alertamujer.api.RetrofitClient
import kotlinx.coroutines.launch
import com.example.alertamujer.api.LoginRequest



class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("AlertaMujerPrefs", Context.MODE_PRIVATE)

    // Señal para navegar al menú principal
    private val _navegarAMain = MutableLiveData<Boolean>()
    val navegarAMain: LiveData<Boolean> get() = _navegarAMain

    // Señal para mostrar errores en pantalla
    private val _mensajeError = MutableLiveData<String>()
    val mensajeError: LiveData<String> get() = _mensajeError

    init {
        // EL GUARDIÁN DE SESIÓN: Apenas nace el ViewModel, revisa si ya hay sesión
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            _navegarAMain.value = true
        }
    }

    fun intentarLogin(email: String, pass: String) {
        viewModelScope.launch {
            try {
                val request = LoginRequest(email, pass)
                val response = RetrofitClient.instance.login(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()
                    sharedPreferences.edit().apply {
                        putBoolean("isLoggedIn", true)
                        putInt("id_usuario", data?.id_usuario ?: -1)
                        apply()
                    }
                    _navegarAMain.value = true
                } else {
                    _mensajeError.value = "Error: Credenciales inválidas"
                }
            } catch (e: Exception) {
                _mensajeError.value = "Fallo: ${e.message}"
            }
        }
    }
}