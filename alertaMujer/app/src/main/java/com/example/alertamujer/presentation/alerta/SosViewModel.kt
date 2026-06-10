package com.example.alertamujer.presentation.alerta

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.telephony.SmsManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.alertamujer.data.dto.AlertaRequest
import com.example.alertamujer.data.dto.AlertaResponse
import com.example.alertamujer.data.model.Contacto
import com.example.alertamujer.data.repository.AlertaRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import retrofit2.Response

class SosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AlertaRepository()

    private val _estadoAlerta = MutableLiveData<EstadoAlerta>(EstadoAlerta.Inactiva)
    val estadoAlerta: LiveData<EstadoAlerta> get() = _estadoAlerta

    private val _idAlertaActual = MutableLiveData<Int?>()
    val idAlertaActual: LiveData<Int?> get() = _idAlertaActual

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    @SuppressLint("MissingPermission")
    fun obtenerUbicacionYEnviar() {
        _estadoAlerta.value = EstadoAlerta.Enviando

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                enviarAlertaDobleVia(location.latitude, location.longitude)
            } else {
                _estadoAlerta.value = EstadoAlerta.Error("No se pudo obtener la ubicación GPS")
            }
        }.addOnFailureListener {
            _estadoAlerta.value = EstadoAlerta.Error("Error al acceder al GPS")
        }
    }

    private fun enviarAlertaDobleVia(latitud: Double, longitud: Double) {
        val prefs: SharedPreferences = getApplication<Application>()
            .getSharedPreferences("AlertaMujerPrefs", Context.MODE_PRIVATE)

        val userId = prefs.getInt("id_usuario", -1)
        val mensajePersonalizado = prefs.getString("mensaje_sos", "¡Auxilio! Alerta SOS iniciada") ?: "¡Auxilio! Alerta SOS iniciada"

        viewModelScope.launch {
            try {
                // =================================================================
                // VÍA 1: POR LA PROPIA APLICACIÓN (Hacia Spring Boot)
                // =================================================================
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

                    // =================================================================
                    // VÍA 2: POR SMS EN SEGUNDO PLANO
                    // Se ejecuta justo después de que Spring Boot confirma la alerta
                    // =================================================================
                    enviarSmsOculto(mensajePersonalizado, latitud, longitud)

                } else {
                    _estadoAlerta.value = EstadoAlerta.Error("Error del servidor: ${response.code()}")
                }
            } catch (e: Exception) {
                _estadoAlerta.value = EstadoAlerta.Error("Sin conexión: ${e.message}")
            }
        }
    }

    private fun enviarSmsOculto(mensajeBase: String, lat: Double, lng: Double) {
        val context = getApplication<Application>()
        val prefs = context.getSharedPreferences("AlertaMujerPrefs", Context.MODE_PRIVATE)
        val json = prefs.getString("lista_contactos", "[]")

        if (json == "[]") return // Si no hay números guardados, cancela el SMS silenciosamente

        val gson = Gson()
        val type = object : TypeToken<List<Contacto>>() {}.type
        val contactos: List<Contacto> = gson.fromJson(json, type)

        // Link dinámico estándar de Google Maps
        val urlMaps = "https://maps.google.com/?q=$lat,$lng"
        val mensajeCompleto = "$mensajeBase\nUbicación: $urlMaps"

        try {
            // Lógica compatible con tu requerimiento mínimo de Android 8+
            val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }

            // Divide el mensaje si supera los 160 caracteres nativos del SMS
            val partesMensaje = smsManager.divideMessage(mensajeCompleto)

            for (contacto in contactos) {
                smsManager.sendMultipartTextMessage(contacto.numero, null, partesMensaje, null, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // No cambiamos el estado a Error aquí, porque la alerta a la aplicación (Spring Boot) ya fue un éxito.
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