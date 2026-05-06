package com.example.alertamujer.presentation.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MensajeViewModel(application: Application) : AndroidViewModel(application) {

    private val _estadoMensaje = MutableLiveData<String>()
    val estadoMensaje: LiveData<String> get() = _estadoMensaje

    fun guardarMensajeLocal(nuevoMensaje: String) {
        if (nuevoMensaje.isBlank()) {
            _estadoMensaje.value = "El mensaje no puede estar vacío"
            return
        }

        try {
            // Guardamos el mensaje en las mismas preferencias que ya usas para el usuario
            val prefs = getApplication<Application>().getSharedPreferences("AlertaMujerPrefs", Context.MODE_PRIVATE)
            prefs.edit().putString("mensaje_sos", nuevoMensaje).apply()

            _estadoMensaje.value = "Mensaje guardado en tu celular"
        } catch (e: Exception) {
            _estadoMensaje.value = "Error al guardar el mensaje"
        }
    }
}