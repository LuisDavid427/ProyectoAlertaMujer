package com.example.alertamujer.data.manager

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import com.example.alertamujer.data.dto.AlertaRequest
import com.example.alertamujer.data.dto.UbicacionRequest
import com.example.alertamujer.data.local.AppDatabase
import com.example.alertamujer.data.network.RetrofitClient
import com.example.alertamujer.util.SessionManager
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import org.json.JSONArray

class SosManager private constructor(private val context: Context) {

    sealed class EstadoAlerta {
        object Inactiva : EstadoAlerta()
        object Procesando : EstadoAlerta()
        object Activa : EstadoAlerta()
        data class Error(val mensaje: String) : EstadoAlerta()
    }

    private val sessionManager = SessionManager(context)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var trackingJob: Job? = null

    // ESTADOS REACTIVOS COMPARTIDOS EN TIEMPO REAL
    private val _estadoAlerta = MutableStateFlow<EstadoAlerta>(EstadoAlerta.Inactiva)
    val estadoAlerta: StateFlow<EstadoAlerta> = _estadoAlerta

    private val _idAlertaActual = MutableStateFlow<Int?>(null)
    val idAlertaActual: StateFlow<Int?> = _idAlertaActual

    companion object {
        @Volatile
        private var INSTANCE: SosManager? = null

        fun getInstance(context: Context): SosManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SosManager(context.applicationContext).also { INSTANCE = it }
            }
    }

    fun isGpsActivado(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun obtenerAuthHeader(): String? {
        val rawToken = sessionManager.obtenerToken() ?: return null
        if (rawToken.isBlank()) return null
        val tokenLimpio = rawToken.replace("Bearer", "", ignoreCase = true).trim()
        return "Bearer $tokenLimpio"
    }

    @SuppressLint("MissingPermission")
    fun procesarAlertaInicial() {
        if (!isGpsActivado()) {
            _estadoAlerta.value = EstadoAlerta.Error("Por favor, activa el GPS de tu dispositivo.")
            return
        }

        val authHeader = obtenerAuthHeader()
        val userId = sessionManager.obtenerIdUsuario()

        if (authHeader == null || userId == null || userId == -1) {
            _estadoAlerta.value = EstadoAlerta.Error("Sesión expirada o invalida. Vuelve a iniciar sesión.")
            return
        }

        _estadoAlerta.value = EstadoAlerta.Procesando

        scope.launch {
            val loc = obtenerUbicacionActual()
            if (loc == null) {
                _estadoAlerta.value = EstadoAlerta.Error("No se pudo obtener tu ubicación GPS.")
                return@launch
            }

            val mensaje = sessionManager.obtenerMensajeSOS()
            val db = AppDatabase.getDatabase(context)
            val listaContactos = db.contactoDao().obtenerTodosLosContactosSincrono()
            val listaEmails = listaContactos.map { it.email }

            enviarSmsOculto(mensaje, loc.latitude, loc.longitude)

            val request = AlertaRequest(
                idUsuario = userId,
                mensaje = mensaje,
                latitud = loc.latitude,
                longitud = loc.longitude,
                contactosNotificar = listaEmails
            )

            try {
                val response = RetrofitClient.alertaService.enviarAlertaSOS(authHeader, request)
                if (response.isSuccessful) {
                    val idAlerta = response.body()?.id_alerta ?: -1
                    _idAlertaActual.value = idAlerta
                    _estadoAlerta.value = EstadoAlerta.Activa
                    iniciarRastreoContinuo(idAlerta)
                } else {
                    _estadoAlerta.value = EstadoAlerta.Error("Error servidor: ${response.code()}")
                }
            } catch (e: Exception) {
                _estadoAlerta.value = EstadoAlerta.Error("Error de conexión: ${e.message}")
            }
        }
    }

    private fun iniciarRastreoContinuo(idAlerta: Int) {
        trackingJob?.cancel()
        trackingJob = scope.launch {
            val authHeader = obtenerAuthHeader() ?: return@launch
            while (isActive) {
                val loc = obtenerUbicacionActual()
                if (loc != null) {
                    val req = UbicacionRequest(loc.latitude, loc.longitude)
                    RetrofitClient.alertaService.enviarUbicacionContinua(authHeader, idAlerta, req)
                }
                delay(10000)
            }
        }
    }

    fun desactivarAlertaEnServidor() {
        trackingJob?.cancel()
        val authHeader = obtenerAuthHeader() ?: ""
        val idAlerta = _idAlertaActual.value ?: -1

        scope.launch {
            try {
                if (idAlerta != -1) {
                    RetrofitClient.alertaService.desactivarAlerta(authHeader, idAlerta)
                }
            } catch (e: Exception) {
                Log.e("DEBUG_SOS", "Error al desactivar: ${e.message}")
            } finally {
                _idAlertaActual.value = null
                _estadoAlerta.value = EstadoAlerta.Inactiva
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun obtenerUbicacionActual(): android.location.Location? {
        return try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
                ?: fusedLocationClient.lastLocation.await()
        } catch (e: Exception) { null }
    }

    private fun enviarSmsOculto(msg: String, lat: Double, lng: Double) {
        try {
            val prefs = context.getSharedPreferences("contactos_prefs", Context.MODE_PRIVATE)
            val json = prefs.getString("contactos", "[]") ?: "[]"
            val contactosArray = JSONArray(json)
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java)
            } else SmsManager.getDefault()

            for (i in 0 until contactosArray.length()) {
                val numero = contactosArray.getJSONObject(i).getString("numero")
                smsManager.sendTextMessage(numero, null, "$msg\nLat: $lat, Lng: $lng", null, null)
            }
        } catch (e: Exception) { Log.e("DEBUG_SOS", "Error SMS: ${e.message}") }
    }
}