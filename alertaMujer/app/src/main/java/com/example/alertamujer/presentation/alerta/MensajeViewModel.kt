package com.example.alertamujer.presentation.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.alertamujer.util.SessionManager

class MensajeViewModel(application: Application) : AndroidViewModel(application) {

    private val _estadoMensaje = MutableLiveData<String>()
    val estadoMensaje: LiveData<String> get() = _estadoMensaje

    // Instanciamos el SessionManager
    private val sessionManager = SessionManager(application)

    fun guardarMensajeLocal(nuevoMensaje: String) {
        if (nuevoMensaje.isBlank()) {
            _estadoMensaje.value = "El mensaje no puede estar vacío"
            return
        }

        try {
            // Guardamos usando SessionManager en el almacenamiento cifrado
            sessionManager.guardarMensajeSOS(nuevoMensaje)
            _estadoMensaje.value = "Mensaje guardado en tu celular"
        } catch (e: Exception) {
            _estadoMensaje.value = "Error al guardar el mensaje"
        }
    }

    fun obtenerMensajeActual(): String {
        return sessionManager.obtenerMensajeSOS()
    }
}