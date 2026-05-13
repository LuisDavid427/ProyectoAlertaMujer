package com.example.alertamujer.presentation.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alertamujer.data.dto.AlertaRecibidaDTO
import com.example.alertamujer.data.repository.AlertaRepository
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val repository = AlertaRepository()

    private val _alertas = MutableLiveData<List<AlertaRecibidaDTO>>()
    val alertas: LiveData<List<AlertaRecibidaDTO>> get() = _alertas

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun cargarChat() {
        viewModelScope.launch {
            try {
                // Llamada GET a tu backend para traer los mensajes de alerta
                val response = repository.obtenerAlertasActivas()

                if (response.isSuccessful && response.body() != null) {
                    _alertas.value = response.body()
                } else {
                    _error.value = "Error al cargar el chat: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Sin conexión con el servidor"
            }
        }
    }
}