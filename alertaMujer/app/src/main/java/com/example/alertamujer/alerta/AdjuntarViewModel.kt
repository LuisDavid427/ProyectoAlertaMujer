package com.example.alertamujer.alerta

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdjuntarViewModel : ViewModel() {

    enum class TipoCaptura { FOTO, VIDEO, AUDIO, MOSTRAR_OPCIONES }

    private val _accionCaptura = MutableLiveData<TipoCaptura>()
    val accionCaptura: LiveData<TipoCaptura> get() = _accionCaptura

    fun alHacerClicEnCamara() {
        _accionCaptura.value = TipoCaptura.MOSTRAR_OPCIONES
    }

    fun alHacerClicEnAudio() {
        _accionCaptura.value = TipoCaptura.AUDIO
    }

    fun seleccionarOpcionCamara(opcion: Int) {
        when (opcion) {
            0 -> _accionCaptura.value = TipoCaptura.FOTO
            1 -> _accionCaptura.value = TipoCaptura.VIDEO
        }
    }
}