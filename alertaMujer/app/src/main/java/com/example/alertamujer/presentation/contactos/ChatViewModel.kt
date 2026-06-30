package com.example.alertamujer.presentation.contactos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alertamujer.data.model.MensajeAlerta

/**
 * Gestiona el estado de las alertas.
 * Utiliza LiveData para asegurar que la UI reaccione a los cambios de datos.
 */
class ChatsViewModel : ViewModel() {

    private val _mensajes = MutableLiveData<MutableList<MensajeAlerta>>(mutableListOf())
    val mensajes: LiveData<MutableList<MensajeAlerta>> get() = _mensajes

    /**
     * Inserta una nueva alerta recibida y notifica a los observadores.
     */
    fun recibirNuevaAlerta(nuevaAlerta: MensajeAlerta) {
        val listaActual = _mensajes.value ?: mutableListOf()
        listaActual.add(nuevaAlerta)
        _mensajes.value = listaActual
    }
}