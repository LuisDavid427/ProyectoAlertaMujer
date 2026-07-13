package com.example.alertamujer.presentation.alerta

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.alertamujer.data.dto.*
import com.example.alertamujer.data.model.Contacto
import com.example.alertamujer.data.network.RetrofitClient
import com.example.alertamujer.util.SessionManager // Importamos tu bóveda segura
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class SosViewModel(application: Application) : AndroidViewModel(application) {

    private val _estadoAlerta = MutableLiveData<EstadoAlerta>(EstadoAlerta.Inactiva)
    val estadoAlerta: LiveData<EstadoAlerta> get() = _estadoAlerta

    private val _idAlertaActual = MutableLiveData<Int?>()
    val idAlertaActual: LiveData<Int?> get() = _idAlertaActual

    // Usamos el SessionManager en lugar de SharedPreferences directo
    private val sessionManager = SessionManager(application)

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    private var isTracking = false

    @SuppressLint("MissingPermission")
    fun procesarAlertaInicial() {
        // Obtenemos el token de forma segura
        val token = sessionManager.obtenerToken() ?: return
        val userId = sessionManager.obtenerIdUsuario()
        // Aquí podrías crear un método en SessionManager para el mensaje
        val mensaje = "¡Auxilio! Necesito ayuda."

        viewModelScope.launch {
            val loc = obtenerUbicacionActual() ?: return@launch

            enviarSmsOculto(mensaje, loc.latitude, loc.longitude)

            val request = AlertaRequest(userId, mensaje, loc.latitude, loc.longitude)
            try {
                val response = RetrofitClient.alertaService.enviarAlertaSOS("Bearer $token", request)
                if (response.isSuccessful) {
                    val idAlerta = response.body()?.id_alerta ?: -1
                    // Guardamos el ID de forma segura
                    _idAlertaActual.postValue(idAlerta)
                    _estadoAlerta.value = EstadoAlerta.Activa
                    iniciarRastreoContinuo(idAlerta)
                }
            } catch (e: Exception) {
                _estadoAlerta.value = EstadoAlerta.Error("Error al conectar: ${e.message}")
            }
        }
    }

    private fun iniciarRastreoContinuo(idAlerta: Int) {
        isTracking = true
        val token = sessionManager.obtenerToken() ?: ""

        viewModelScope.launch {
            while (isTracking) {
                val loc = obtenerUbicacionActual()
                if (loc != null) {
                    val req = UbicacionRequest(loc.latitude, loc.longitude)
                    RetrofitClient.alertaService.enviarUbicacionContinua("Bearer $token", idAlerta, req)
                }
                delay(5000) // Cambié 50000 por 5000 para que sea real
            }
        }
    }

    fun desactivarAlertaEnServidor() {
        val token = sessionManager.obtenerToken() ?: ""
        val idAlerta = _idAlertaActual.value ?: -1

        isTracking = false

        viewModelScope.launch {
            try {
                val response = RetrofitClient.alertaService.desactivarAlerta("Bearer $token", idAlerta)
                if (response.isSuccessful) {
                    _idAlertaActual.postValue(null)
                    _estadoAlerta.value = EstadoAlerta.Inactiva
                }
            } catch (e: Exception) {
                Log.e("DEBUG_SOS", "Error desactivando: ${e.message}")
            }
        }
    }

    private fun enviarSmsOculto(msg: String, lat: Double, lng: Double) {
        // NOTA: Como la lista de contactos es texto plano en la BD, sigue usando SharedPreferences viejo.
        // Pero lo ideal es que a futuro la pases a SessionManager también.
        val prefs = getApplication<Application>().getSharedPreferences("AlertaMujerPrefs", android.content.Context.MODE_PRIVATE)
        val json = prefs.getString("lista_contactos", "[]") ?: "[]"
        if (json == "[]") return

        val contactos: List<Contacto> = Gson().fromJson(json, object : TypeToken<List<Contacto>>() {}.type)

        val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getApplication<Application>().getSystemService(SmsManager::class.java)
        } else SmsManager.getDefault()

        contactos.forEach {
            smsManager.sendTextMessage(it.numero, null, "$msg\nLat: $lat, Lng: $lng", null, null)
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun obtenerUbicacionActual() =
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()

    sealed class EstadoAlerta {
        object Inactiva : EstadoAlerta()
        object Activa : EstadoAlerta()
        data class Error(val mensaje: String) : EstadoAlerta()
    }
}