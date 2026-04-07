package com.example.alertamujer.alerta

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.example.alertamujer.api.RetrofitClient
import kotlinx.coroutines.launch
import com.example.alertamujer.api.AlertaRequest

class SosViewModel(application: Application) : AndroidViewModel(application) {

    private val _estadoAlerta = MutableLiveData<EstadoAlerta>(EstadoAlerta.Inactiva)
    val estadoAlerta: LiveData<EstadoAlerta> get() = _estadoAlerta

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    @SuppressLint("MissingPermission")
    fun obtenerUbicacionYEnviar() {
        _estadoAlerta.value = EstadoAlerta.Enviando

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                // Tenemos ubicación, ahora la mandamos al servidor
                enviarAlertaAlServidor(location.latitude, location.longitude)
            } else {
                _estadoAlerta.value = EstadoAlerta.Error("Enciende el GPS del teléfono para enviar la alerta")
            }
        }.addOnFailureListener {
            _estadoAlerta.value = EstadoAlerta.Error("Fallo al leer el sensor GPS.")
        }
    }

    private fun enviarAlertaAlServidor(latitud: Double, longitud: Double) {
        val prefs = getApplication<Application>().getSharedPreferences("AlertaMujerPrefs", Context.MODE_PRIVATE)
        val userId = prefs.getInt("id_usuario", -1)

        viewModelScope.launch {
            try {
                val request = AlertaRequest(
                    latitud = latitud,
                    longitud = longitud,
                    id_usuario = userId,
                    mensaje = "¡Auxilio! Alerta SOS iniciada"
                )

                val response = RetrofitClient.instance.enviarAlertaSOS(request)

                if (response.isSuccessful) {
                    _estadoAlerta.value = EstadoAlerta.Activa
                } else {
                    _estadoAlerta.value = EstadoAlerta.Error("Error del servidor: ${response.code()}")
                }
            } catch (e: Exception) {
                _estadoAlerta.value = EstadoAlerta.Error("Fallo de conexión: ${e.message}")
            }
        }
    }

    fun desactivarAlerta() {
        _estadoAlerta.value = EstadoAlerta.Inactiva
    }

    sealed class EstadoAlerta {
        object Inactiva : EstadoAlerta()
        object Enviando : EstadoAlerta()
        object Activa : EstadoAlerta()
        data class Error(val mensaje: String) : EstadoAlerta()
    }
}