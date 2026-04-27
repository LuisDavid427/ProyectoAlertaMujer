package com.example.alertamujer.presentation.alerta

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alertamujer.data.repository.AlertaRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AdjuntarViewModel : ViewModel() {

    private val repository = AlertaRepository()

    enum class TipoCaptura { FOTO, VIDEO, AUDIO, MOSTRAR_OPCIONES }

    private val _accionCaptura = MutableLiveData<TipoCaptura>()
    val accionCaptura: LiveData<TipoCaptura> get() = _accionCaptura

    private val _estadoSubida = MutableLiveData<String>()
    val estadoSubida: LiveData<String> get() = _estadoSubida

    fun alHacerClicEnCamara() { _accionCaptura.value = TipoCaptura.MOSTRAR_OPCIONES }
    fun alHacerClicEnAudio() { _accionCaptura.value = TipoCaptura.AUDIO }

    fun seleccionarOpcionCamara(opcion: Int) {
        when (opcion) {
            0 -> _accionCaptura.value = TipoCaptura.FOTO
            1 -> _accionCaptura.value = TipoCaptura.VIDEO
        }
    }

    fun enviarArchivoAlServidor(idAlerta: Int, file: File, esVideo: Boolean) {
        _estadoSubida.value = "Subiendo archivo..."

        viewModelScope.launch {
            try {
                val mimeType = if (esVideo) "video/mp4" else "image/jpeg"
                val requestFile: RequestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
                val body: MultipartBody.Part = MultipartBody.Part.createFormData("archivo", file.name, requestFile)

                val tipoStr = if (esVideo) "VIDEO" else "FOTO"
                val tipoBody: RequestBody = tipoStr.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = repository.subirEvidencia(idAlerta, body, tipoBody)

                if (response.isSuccessful) {
                    _estadoSubida.value = "Evidencia enviada con éxito"
                } else {
                    _estadoSubida.value = "Error al subir: ${response.code()}"
                }
            } catch (e: Exception) {
                _estadoSubida.value = "Fallo de red: ${e.message}"
            }
        }
    }
}