package com.example.alertamujer.data.network.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.alertamujer.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.alertamujer.data.network.RetrofitClient
import com.example.alertamujer.util.AesUtil // <-- Importa la clase utilitaria que creaste en Kotlin
import com.example.alertamujer.data.dto.FcmTokenRequest
import com.example.alertamujer.data.local.entity.AlertaEntity
import com.example.alertamujer.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class AlertaFCMService : FirebaseMessagingService() {

    private val LLAVE_SECRETA = "AlertaMujerSuperSecretKey2026!!!"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val datosSeguros = remoteMessage.data["datos_seguros"]

        if (datosSeguros != null) {
            try {
                val jsonDesencriptado = AesUtil.desencriptar(datosSeguros, LLAVE_SECRETA)
                val jsonObject = JSONObject(jsonDesencriptado)

                // Convertimos a Entidad de Room
                val alertaEntity = AlertaEntity(
                    id_alerta = jsonObject.optInt("id_alerta"),
                    nombre_usuario = jsonObject.optString("nombre_victima", "Alguien"),
                    mensaje = jsonObject.optString("mensaje", "Auxilio!"),
                    latitud = jsonObject.optDouble("latitud"),
                    longitud = jsonObject.optDouble("longitud")
                )

                // Guardamos en Room inmediatamente
                val db = AppDatabase.getDatabase(applicationContext)
                CoroutineScope(Dispatchers.IO).launch {
                    db.alertaDao().insertarAlerta(alertaEntity)
                }

                mostrarNotificacionEmergencia(alertaEntity.nombre_usuario, alertaEntity.mensaje)

            } catch (e: Exception) {
                Log.e("ALERTA_SEGURA", "Fallo: ${e.message}")
            }
        }
    }
    private fun mostrarNotificacionEmergencia(titulo: String, contenido: String) {
        val channelId = "canal_emergencia_sos"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Alertas de Auxilio",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para alertas de emergencia de contactos de confianza"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_alert) // Asegúrate de tener este icono
            .setContentTitle("¡EMERGENCIA: $titulo!")
            .setContentText(contenido)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        guardarTokenEnServidor(token)
    }



    private fun guardarTokenEnServidor(token: String) {
        val sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)
        val idUsuario = sharedPreferences.getInt("id_usuario", -1)

        if (idUsuario != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // 1. Armamos el DTO con la estructura exacta que espera Spring Boot
                    val request = FcmTokenRequest(idUsuario = idUsuario, token = token)

                    // 2. Usamos usuarioService (el nuevo que creamos) en lugar de authService
                    val respuesta = RetrofitClient.usuarioService.actualizarToken(request)

                    if (respuesta.isSuccessful) {
                        Log.d("FCM_TOKEN", "Token sincronizado con éxito en MySQL")
                    } else {
                        Log.e("FCM_TOKEN", "Error en el servidor al guardar el token")
                    }
                } catch (e: Exception) {
                    Log.e("FCM_TOKEN", "Fallo de red al enviar token: ${e.message}")
                }
            }
        }
    }
}