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
import android.app.Application
import android.content.Context

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

    // En tu ViewModel, cambia la firma de la función:
    fun enviarArchivoAlServidor(context: Context, idAlerta: Int, file: File, esVideo: Boolean) {
        _estadoSubida.value = "Subiendo archivo..."

        // Ahora usas el context que te pasaron como parámetro
        val prefs = context.getSharedPreferences("AlertaMujerPrefs", Context.MODE_PRIVATE)
        val token = prefs.getString("token_jwt", "") ?: ""
        val tokenFormateado = "Bearer $token"

        viewModelScope.launch {
            try {
                val mimeType = if (esVideo) "video/mp4" else "image/jpeg"
                val requestFile: RequestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
                val body: MultipartBody.Part = MultipartBody.Part.createFormData("archivo", file.name, requestFile)

                val tipoStr = if (esVideo) "VIDEO" else "FOTO"
                val tipoBody: RequestBody = tipoStr.toRequestBody("text/plain".toMediaTypeOrNull())

                // 2. Pasamos el token como primer argumento, tal como lo exige el nuevo repositorio
                val response = repository.subirEvidencia(tokenFormateado, idAlerta, body, tipoBody)

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