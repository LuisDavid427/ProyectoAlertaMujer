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
import com.example.alertamujer.data.local.entity.AlertaEntity
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

    @SuppressLint("MissingPermission")
    fun procesarAlertaInicial() {
        if (!isGpsActivado()) {
            _estadoAlerta.value = EstadoAlerta.Error("Por favor, activa el GPS de tu dispositivo.")
            return
        }

        val userId = sessionManager.obtenerIdUsuario()
        val token = sessionManager.obtenerToken()

        if (token.isNullOrBlank() || userId == null || userId == -1) {
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

            // 🟢 1. Cargar contactos desde Room (Fuente única)
            val listaContactos = db.contactoDao().obtenerTodosLosContactosSincrono()

            // Filtrar correos no vacíos/nulos para el Backend
            val listaEmails = listaContactos.map { it.email }.filter { it.isNotBlank() }

            Log.d("DEBUG_SOS", "🟢 Contactos encontrados en Room: ${listaContactos.size}. Emails a enviar: $listaEmails")

            // 🟢 2. Enviar SMS usando directamente los números de Room
            enviarSmsOcultoDesdeRoom(listaContactos, mensaje, loc.latitude, loc.longitude)

            // 🟢 3. Enviar petición al Backend con la lista de correos
            val request = AlertaRequest(
                idUsuario = userId,
                mensaje = mensaje,
                latitud = loc.latitude,
                longitud = loc.longitude,
                contactosNotificar = listaEmails
            )

            try {
                val response = RetrofitClient.getAlertaService(context).enviarAlertaSOS(request)
                if (response.isSuccessful) {
                    val idAlerta = response.body()?.id_alerta ?: -1
                    _idAlertaActual.value = idAlerta
                    _estadoAlerta.value = EstadoAlerta.Activa

                    try {
                        val nombreUsuario = sessionManager.obtenerNombreUsuario() ?: "Usuario"
                        val idUsuario = sessionManager.obtenerIdUsuario()
                        val nuevaAlertaLocal = AlertaEntity(
                            id_local = 0,
                            id_alerta = idAlerta,
                            id_usuario = idUsuario,
                            nombre_usuario = nombreUsuario,
                            mensaje = mensaje,
                            latitud = loc.latitude,
                            longitud = loc.longitude,
                            timestamp = System.currentTimeMillis()
                        )
                        db.alertaDao().insertarAlerta(nuevaAlertaLocal)
                        Log.d("ROOM_SOS", "✅ Alerta guardada en Room con id_alerta: $idAlerta")
                    } catch (e: Exception) {
                        Log.e("ROOM_SOS", "❌ Error guardando en Room: ${e.message}", e)
                    }

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
            while (isActive) {
                val loc = obtenerUbicacionActual()
                if (loc != null) {
                    val req = UbicacionRequest(loc.latitude, loc.longitude)
                    try {
                        RetrofitClient.getAlertaService(context).enviarUbicacionContinua(idAlerta, req)
                    } catch (e: Exception) {
                        Log.e("DEBUG_SOS", "Error en rastreo continuo: ${e.message}")
                    }
                }
                delay(10000)
            }
        }
    }

    fun desactivarAlertaEnServidor() {
        trackingJob?.cancel()
        val idAlerta = _idAlertaActual.value ?: -1

        scope.launch {
            try {
                if (idAlerta != -1) {
                    RetrofitClient.getAlertaService(context).desactivarAlerta(idAlerta)
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

    private fun enviarSmsOcultoDesdeRoom(contactos: List<com.example.alertamujer.data.local.entity.ContactoEntity>, msg: String, lat: Double, lng: Double) {
        try {
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }

            val linkUbicacion = "https://maps.google.com/?q=$lat,$lng"
            val textoFinal = "$msg\nMi ubicación actual: $linkUbicacion"

            for (contacto in contactos) {
                if (contacto.numero.isNotBlank()) {
                    smsManager.sendTextMessage(contacto.numero, null, textoFinal, null, null)
                    Log.d("DEBUG_SOS", "📱 SMS enviado a ${contacto.nombre} (${contacto.numero})")
                }
            }
        } catch (e: Exception) {
            Log.e("DEBUG_SOS", "🔴 Error enviando SMS: ${e.message}")
        }
    }
}