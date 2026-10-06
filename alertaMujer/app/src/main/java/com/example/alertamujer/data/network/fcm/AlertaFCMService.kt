package com.example.alertamujer.data.network.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.alertamujer.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.alertamujer.data.network.RetrofitClient
import com.example.alertamujer.util.AesUtil
import com.example.alertamujer.util.SessionManager
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

                val alertaEntity = AlertaEntity(
                    id_alerta = jsonObject.optInt("id_alerta"),
                    nombre_usuario = jsonObject.optString("nombre_victima", "Alguien"),
                    id_usuario = jsonObject.optInt("id_usuario"),
                    mensaje = jsonObject.optString("mensaje", "Auxilio!"),
                    latitud = jsonObject.optDouble("latitud"),
                    longitud = jsonObject.optDouble("longitud")
                )

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
                vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent para abrir la pantalla de Chats al hacer clic en la notificación
        val intent = Intent(this, com.example.alertamujer.ui.contactos.ChatsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            this, 0, intent,
            android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_alert)
            .setContentTitle("¡EMERGENCIA: $titulo!")
            .setContentText(contenido)
            .setPriority(NotificationCompat.PRIORITY_MAX) // Prioridad máxima para Android 8+
            .setCategory(NotificationCompat.CATEGORY_ALARM) // Categoría de alarma para que salte sobre otras apps
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        guardarTokenEnServidor(token)
    }

    private fun guardarTokenEnServidor(token: String) {
        // Leemos id_usuario de forma segura mediante SessionManager
        val sessionManager = SessionManager(applicationContext)
        val idUsuario = sessionManager.obtenerIdUsuario()

        // Guardamos el nuevo token localmente
        sessionManager.guardarFcmToken(token)

        if (idUsuario != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = FcmTokenRequest(idUsuario = idUsuario, token = token)
                    val respuesta = RetrofitClient.getUsuarioService(applicationContext).actualizarToken(request)

                    if (respuesta.isSuccessful) {
                        Log.d("FCM_TOKEN", "🟢 Token sincronizado con éxito en MySQL para ID: $idUsuario")
                    } else {
                        Log.e("FCM_TOKEN", "🔴 Error del servidor (${respuesta.code()}) al guardar el token")
                    }
                } catch (e: Exception) {
                    Log.e("FCM_TOKEN", "🔴 Fallo de red al enviar token: ${e.message}")
                }
            }
        } else {
            Log.w("FCM_TOKEN", "⚠️ Token rotado por FCM, pero no hay usuario autenticado en SessionManager.")
        }
    }
}