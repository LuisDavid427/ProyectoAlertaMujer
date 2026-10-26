package com.example.alertamujer.data.manager

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import com.example.alertamujer.data.dto.AlertaRequest
import com.example.alertamujer.data.dto.UbicacionRequest
import com.example.alertamujer.data.local.entity.AlertaEntity
import com.example.alertamujer.data.network.repository.AlertaRepository // 👈 Usamos el AlertaRepository unificado
import com.example.alertamujer.util.SessionManager
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class SosManager private constructor(private val context: Context) {

    sealed class EstadoAlerta {
        object Inactiva : EstadoAlerta()
        object Procesando : EstadoAlerta()
        object Activa : EstadoAlerta()
        data class Error(val mensaje: String) : EstadoAlerta()
    }

    private val sessionManager = SessionManager(context)
    private val alertaRepository = AlertaRepository(context) // 👈 Instanciamos el repositorio único
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
            _estadoAlerta.value = EstadoAlerta.Error("Sesión expirada o inválida. Vuelve a iniciar sesión.")
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

            // 1. Cargamos contactos locales a través del repositorio[cite: 5]
            val listaContactos = alertaRepository.obtenerContactosLocales()
            val listaEmails = listaContactos.map { it.email }.filter { it.isNotBlank() }

            Log.d("DEBUG_SOS", "🟢 Contactos encontrados: ${listaContactos.size}. Emails: $listaEmails")

            // 2. Enviamos SMS locales
            enviarSmsOcultoDesdeRoom(listaContactos, mensaje, loc.latitude, loc.longitude)

            // 3. Petición al backend mediante el repositorio
            val request = AlertaRequest(
                idUsuario = userId,
                mensaje = mensaje,
                latitud = loc.latitude,
                longitud = loc.longitude,
                contactosNotificar = listaEmails
            )

            try {
                val response = alertaRepository.enviarAlertaSOS(request)
                if (response.isSuccessful) {
                    val idAlerta = response.body()?.idAlerta ?: -1
                    _idAlertaActual.value = idAlerta
                    _estadoAlerta.value = EstadoAlerta.Activa

                    try {
                        val nombreUsuario = sessionManager.obtenerNombreUsuario() ?: "Usuario"
                        val nuevaAlertaLocal = AlertaEntity(
                            id_local = 0,
                            id_alerta = idAlerta,
                            id_usuario = userId,
                            nombre_usuario = nombreUsuario,
                            mensaje = mensaje,
                            latitud = loc.latitude,
                            longitud = loc.longitude,
                            timestamp = System.currentTimeMillis()
                        )
                        // 4. Guardado local en Room a través del repositorio[cite: 4]
                        alertaRepository.guardarAlertaLocal(nuevaAlertaLocal)
                        Log.d("ROOM_SOS", "✅ Alerta guardada localmente con ID: $idAlerta")
                    } catch (e: Exception) {
                        Log.e("ROOM_SOS", "❌ Error guardando local: ${e.message}", e)
                    }

                    iniciarRastreoContinuo(idAlerta)
                } else {
                    val codigoError = response.code()
                    if (codigoError == 400 || codigoError == 403) {
                        _estadoAlerta.value = EstadoAlerta.Error("Tu cuenta se encuentra bloqueada por la administración.")
                    } else {
                        _estadoAlerta.value = EstadoAlerta.Error("Error servidor: $codigoError")
                    }
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
                        alertaRepository.enviarUbicacionContinua(idAlerta, req)
                        Log.d("DEBUG_SOS", "📍 Ubicación continua enviada: ${loc.latitude}, ${loc.longitude}")
                    } catch (e: Exception) {
                        Log.e("DEBUG_SOS", "Error en rastreo continuo: ${e.message}")
                    }
                }
                delay(10000) // Cada 10 segundos
            }
        }
    }

    fun desactivarAlertaEnServidor() {
        trackingJob?.cancel()
        val idAlerta = _idAlertaActual.value ?: -1

        scope.launch {
            try {
                if (idAlerta != -1) {
                    val listaContactos = alertaRepository.obtenerContactosLocales()
                    val listaEmails = listaContactos.map { it.email }.filter { it.isNotBlank() }
                    val body = mapOf("contactosNotificar" to listaEmails)

                    alertaRepository.desactivarAlerta(idAlerta, body)
                    Log.d("DEBUG_SOS", "🟢 Alerta desactivada correctamente.")
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
            withTimeoutOrNull(7000L) {
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
            } ?: fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            Log.e("DEBUG_SOS", "🔴 Error obteniendo ubicación: ${e.message}")
            null
        }
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