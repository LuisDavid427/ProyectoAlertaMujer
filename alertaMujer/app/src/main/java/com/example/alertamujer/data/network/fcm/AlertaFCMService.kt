package com.example.alertamujer.data.network.fcm

import android.app.NotificationManager
import com.example.alertamujer.util.NotificationUtils
import com.example.alertamujer.util.FcmUtil
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlertaFCMService : FirebaseMessagingService() {

    private lateinit var messageProcessor: FcmMessageProcessor

    override fun onCreate() {
        super.onCreate()
        messageProcessor = FcmMessageProcessor(applicationContext)
        crearCanalesNotificacion()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Delegamos todo el procesamiento pesado al procesador limpio
        CoroutineScope(Dispatchers.IO).launch {
            messageProcessor.procesarMensaje(remoteMessage)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Reutilizamos tu utilitario global de sincronización de tokens
        FcmUtil.sincronizarTokenSesion(applicationContext)
    }

    private fun crearCanalesNotificacion() {
        val notificationUtils = NotificationUtils(this)
        notificationUtils.createNotificationChannel(
            "canal_emergencia_sos",
            "Alertas de Auxilio",
            "Canal para alertas de emergencia de contactos de confianza",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationUtils.createNotificationChannel(
            "canal_evidencia",
            "Nueva Evidencia",
            "Canal para notificar nueva evidencia adjuntada",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationUtils.createNotificationChannel(
            "canal_estado_alerta",
            "Estado de la Alerta",
            "Canal para notificar cambios en el estado de la alerta",
            NotificationManager.IMPORTANCE_HIGH
        )
    }
}