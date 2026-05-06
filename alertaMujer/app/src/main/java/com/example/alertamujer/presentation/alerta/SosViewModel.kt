package com.example.alertamujer.presentation.alerta

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.alertamujer.data.dto.AlertaRequest
import com.example.alertamujer.data.dto.AlertaResponse
import com.example.alertamujer.data.repository.AlertaRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import retrofit2.Response

class SosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AlertaRepository()

    private val _estadoAlerta = MutableLiveData<EstadoAlerta>(EstadoAlerta.Inactiva)
    val estadoAlerta: LiveData<EstadoAlerta> get() = _estadoAlerta

    // Exponemos el ID para que la Activity pueda pasarlo a AdjuntarActivity
    private val _idAlertaActual = MutableLiveData<Int?>()
    val idAlertaActual: LiveData<Int?> get() = _idAlertaActual

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    @SuppressLint("MissingPermission")
    fun obtenerUbicacionYEnviar() {
        _estadoAlerta.value = EstadoAlerta.Enviando

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                enviarAlertaAlServidor(location.latitude, location.longitude)
            } else {
                _estadoAlerta.value = EstadoAlerta.Error("Enciende el GPS para enviar la alerta")
            }
        }.addOnFailureListener {
            _estadoAlerta.value = EstadoAlerta.Error("Error al leer el sensor GPS")
        }
    }

    private fun enviarAlertaAlServidor(latitud: Double, longitud: Double) {
        val prefs: SharedPreferences = getApplication<Application>()
            .getSharedPreferences("AlertaMujerPrefs", Context.MODE_PRIVATE)
        val userId = prefs.getInt("id_usuario", -1)

        // NUEVO: Leemos el mensaje personalizado. Si no hay nada, usa el de por defecto.
        val mensajePersonalizado = prefs.getString("mensaje_sos", "¡Auxilio! Alerta SOS iniciada") ?: "¡Auxilio! Alerta SOS iniciada"

        viewModelScope.launch {
            try {
                val request = AlertaRequest(
                    id_usuario = userId,
                    mensaje = mensajePersonalizado,
                    latitud = latitud,
                    longitud = longitud
                )

                val response: Response<AlertaResponse> = repository.enviarAlertaSOS(request)

                if (response.isSuccessful && response.body() != null) {
                    val id = response.body()?.id_alerta
                    _idAlertaActual.value = id
                    _estadoAlerta.value = EstadoAlerta.Activa
                } else {
                    _estadoAlerta.value = EstadoAlerta.Error("Error del servidor: ${response.code()}")
                }
            } catch (e: Exception) {
                _estadoAlerta.value = EstadoAlerta.Error("Sin conexión: ${e.message}")
            }
        }
    }

    fun desactivarAlerta() {
        _idAlertaActual.value = null
        _estadoAlerta.value = EstadoAlerta.Inactiva
    }

    sealed class EstadoAlerta {
        object Inactiva : EstadoAlerta()
        object Enviando : EstadoAlerta()
        object Activa : EstadoAlerta()
        data class Error(val mensaje: String) : EstadoAlerta()
    }
}