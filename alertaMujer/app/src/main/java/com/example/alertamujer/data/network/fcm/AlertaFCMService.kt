package com.example.alertamujer.network.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.alertamujer.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AlertaFCMService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Extraer datos enviados desde Spring Boot
        val nombreVictima = remoteMessage.data["nombre_victima"] ?: "Alguien"
        val mensaje = remoteMessage.data["mensaje"] ?: "Necesita ayuda inmediata"

        // 1. Mostrar la notificación en la barra de estado
        mostrarNotificacionEmergencia(nombreVictima, mensaje)

        // 2. Notificar a la App (Broadcast) para que el Chat se refresque solo
        val intent = Intent("NUEVA_ALERTA_RECIBIDA")
        sendBroadcast(intent)
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
}