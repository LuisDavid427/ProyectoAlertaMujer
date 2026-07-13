package com.example.alertamujer.presentation.alerta

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.alertamujer.data.dto.*
import com.example.alertamujer.data.local.AppDatabase
import com.example.alertamujer.data.model.Contacto
import com.example.alertamujer.data.network.RetrofitClient
import com.example.alertamujer.util.SessionManager // Importamos tu bóveda segura
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.json.JSONArray

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
        // 1. Obtenemos datos desde nuestra bóveda segura
        val token = sessionManager.obtenerToken() ?: return
        val userId = sessionManager.obtenerIdUsuario()

        // Obtenemos el mensaje personalizado del usuario (o el predeterminado)
        val prefs = getApplication<Application>().getSharedPreferences("AlertaMujerPrefs", android.content.Context.MODE_PRIVATE)
        val mensaje = prefs.getString("mensaje_sos", "¡Auxilio! Necesito ayuda inmediata.") ?: "¡Auxilio!"

        viewModelScope.launch {
            // 2. Obtenemos ubicación actual
            val loc = obtenerUbicacionActual() ?: return@launch

            // 3. Obtenemos la lista de contactos desde Room para extraer los emails
            val db = AppDatabase.getDatabase(getApplication())
            val listaContactos = db.contactoDao().obtenerTodosLosContactosSincrono() // Debes crear este método en tu DAO
            val listaEmails = listaContactos.map { it.email }

            // 4. Enviamos SMS de respaldo a los números (lo que ya tenías)
            enviarSmsOculto(mensaje, loc.latitude, loc.longitude)

            // 5. Construimos el Request incluyendo la lista de emails para el servidor
            val request = AlertaRequest(
                idUsuario = userId,
                mensaje = mensaje,
                latitud = loc.latitude,
                longitud = loc.longitude,
                contactosNotificar = listaEmails
            )

            try {
                // 6. Enviamos al servidor
                val response = RetrofitClient.alertaService.enviarAlertaSOS("Bearer $token", request)

                if (response.isSuccessful) {
                    val idAlerta = response.body()?.id_alerta ?: -1

                    _idAlertaActual.postValue(idAlerta)
                    _estadoAlerta.value = EstadoAlerta.Activa

                    // Iniciamos el rastreo GPS constante
                    iniciarRastreoContinuo(idAlerta)
                } else {
                    _estadoAlerta.value = EstadoAlerta.Error("Servidor no pudo procesar la alerta")
                }
            } catch (e: Exception) {
                _estadoAlerta.value = EstadoAlerta.Error("Fallo de red: ${e.message}")
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
                delay(10000)
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
        val prefs = getApplication<Application>().getSharedPreferences("contactos_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("contactos", "[]") ?: "[]"

        val contactosArray = JSONArray(json)
        val emails = mutableListOf<String>()

        val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getApplication<Application>().getSystemService(SmsManager::class.java)
        } else SmsManager.getDefault()

        for (i in 0 until contactosArray.length()) {
            val obj = contactosArray.getJSONObject(i)
            val numero = obj.getString("numero")
            val email = obj.optString("email", "") // Asegúrate de guardar email en AddContacto
            emails.add(email)
            smsManager.sendTextMessage(numero, null, "$msg\nLat: $lat, Lng: $lng", null, null)
        }

        // Aquí es donde deberías pasar 'emails' a tu AlertaRequest si quieres enviar la lista al servidor
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