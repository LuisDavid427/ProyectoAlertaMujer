package com.example.alertamujer.presentation.alerta

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
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
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class SosViewModel(application: Application) : AndroidViewModel(application) {

    private val _estadoAlerta = MutableLiveData<EstadoAlerta>(EstadoAlerta.Inactiva)
    val estadoAlerta: LiveData<EstadoAlerta> get() = _estadoAlerta

    private val _idAlertaActual = MutableLiveData<Int?>()
    val idAlertaActual: LiveData<Int?> get() = _idAlertaActual

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("AlertaMujerPrefs", Context.MODE_PRIVATE)

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    private var isTracking = false

    // 1. INICIO: Crea la alerta, guarda ID, envía SMS y arranca el ciclo
    @SuppressLint("MissingPermission")
    fun procesarAlertaInicial() {
        val token = sharedPreferences.getString("token_jwt", null) ?: return
        val userId = sharedPreferences.getInt("id_usuario", -1)
        val mensaje = sharedPreferences.getString("mensaje_sos", "¡Auxilio!") ?: "¡Auxilio!"

        viewModelScope.launch {
            val loc = obtenerUbicacionActual() ?: return@launch

            // Envío SMS de respaldo
            enviarSmsOculto(mensaje, loc.latitude, loc.longitude)

            // Envío al Servidor
            val request = AlertaRequest(userId, mensaje, loc.latitude, loc.longitude)
            try {
                val response = RetrofitClient.alertaService.enviarAlertaSOS("Bearer $token", request)
                if (response.isSuccessful) {
                    val idAlerta = response.body()?.id_alerta ?: -1
                    sharedPreferences.edit().putInt("id_alerta_activa", idAlerta).apply()
                    _idAlertaActual.postValue(idAlerta)
                    _estadoAlerta.value = EstadoAlerta.Activa
                    iniciarRastreoContinuo(idAlerta)
                }
            } catch (e: Exception) {
                _estadoAlerta.value = EstadoAlerta.Error("Error al conectar: ${e.message}")
            }
        }
    }

    // 2. RASTREO: Envío automático cada 5 segundos
    private fun iniciarRastreoContinuo(idAlerta: Int) {
        isTracking = true
        val token = sharedPreferences.getString("token_jwt", "") ?: ""

        viewModelScope.launch {
            while (isTracking) {
                val loc = obtenerUbicacionActual()
                if (loc != null) {
                    val req = UbicacionRequest(loc.latitude, loc.longitude)
                    RetrofitClient.alertaService.enviarUbicacionContinua("Bearer $token", idAlerta, req)
                }
                delay(5000)
            }
        }
    }

    // 3. DESACTIVAR: Rompe el bucle y notifica al servidor
    fun desactivarAlertaEnServidor() {
        val token = sharedPreferences.getString("token_jwt", "") ?: ""
        val idAlerta = sharedPreferences.getInt("id_alerta_activa", -1)

        isTracking = false

        viewModelScope.launch {
            try {
                val response = RetrofitClient.alertaService.desactivarAlerta("Bearer $token", idAlerta)
                if (response.isSuccessful) {
                    sharedPreferences.edit().remove("id_alerta_activa").apply()
                    _idAlertaActual.postValue(null)
                    _estadoAlerta.value = EstadoAlerta.Inactiva
                }
            } catch (e: Exception) {
                Log.e("DEBUG_SOS", "Error desactivando: ${e.message}")
            }
        }
    }

    private fun enviarSmsOculto(msg: String, lat: Double, lng: Double) {
        val json = sharedPreferences.getString("lista_contactos", "[]") ?: "[]"
        if (json == "[]") return

        val contactos: List<Contacto> = Gson().fromJson(json, object : TypeToken<List<Contacto>>() {}.type)
        val urlMaps = "https://www.google.com/maps/search/?api=1&query=$lat,$lng"

        val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getApplication<Application>().getSystemService(SmsManager::class.java)
        } else SmsManager.getDefault()

        contactos.forEach {
            smsManager.sendTextMessage(it.numero, null, "$msg\n$urlMaps", null, null)
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