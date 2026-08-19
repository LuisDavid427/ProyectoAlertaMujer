package com.example.alertamujer.presentation.alerta

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.alertamujer.data.manager.SosManager

class SosViewModel(application: Application) : AndroidViewModel(application) {

    private val sosManager = SosManager.getInstance(application)

    // Escucha el flujo del Singleton y lo convierte a LiveData para la Activity
    val estadoAlerta: LiveData<SosManager.EstadoAlerta> = sosManager.estadoAlerta.asLiveData()
    val idAlertaActual: LiveData<Int?> = sosManager.idAlertaActual.asLiveData()

    fun procesarAlertaInicial() {
        sosManager.procesarAlertaInicial()
    }

    fun desactivarAlertaEnServidor() {
        sosManager.desactivarAlertaEnServidor()
    }
}