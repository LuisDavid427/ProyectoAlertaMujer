package com.example.alertamujer.data.network.fcm

import android.content.Context
import android.util.Log
import com.example.alertamujer.data.local.AppDatabase
import com.example.alertamujer.data.local.entity.AlertaEntity
import com.example.alertamujer.util.AesUtil
import com.example.alertamujer.util.NotificationUtils
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class FcmMessageProcessor(private val context: Context) {

    private val LLAVE_SECRETA = "AlertaMujerSuperSecretKey2026!!!"
    private val notificationUtils = NotificationUtils(context)
    private val db = AppDatabase.getDatabase(context)

    suspend fun procesarMensaje(remoteMessage: RemoteMessage) {
        val datosSeguros = remoteMessage.data["datos_seguros"] ?: return

        try {
            val jsonDesencriptado = AesUtil.desencriptar(datosSeguros, LLAVE_SECRETA)
            val jsonObject = JSONObject(jsonDesencriptado)
            val accion = jsonObject.optString("accion")

            when (accion) {
                "nueva_evidencia" -> manejarEvidencia(jsonObject)
                "alerta_desactivada" -> manejarDesactivacion(jsonObject)
                "alerta_activada" -> manejarActivacion(jsonObject)
                else -> manejarEmergenciaSos(jsonObject)
            }
        } catch (e: Exception) {
            Log.e("FCM_PROCESSOR", "Fallo procesando mensaje seguro: ${e.message}")
        }
    }

    private suspend fun manejarEvidencia(json: JSONObject) {
        val idAlerta = json.optInt("id_alerta")
        val nombreVictima = json.optString("nombre_victima", "Alguien")
        val tipoEvidencia = json.optString("tipo_evidencia", "archivo")

        withContext(Dispatchers.Main) {
            notificationUtils.showNotification(
                "canal_evidencia",
                "Nueva evidencia de $nombreVictima",
                "Se ha adjuntado un nuevo $tipoEvidencia a la alerta.",
                idAlerta
            )
        }
    }

    private suspend fun manejarDesactivacion(json: JSONObject) {
        val idAlerta = json.optInt("id_alerta")
        val nombreUsuario = json.optString("nombre_victima", "Alguien")

        // Actualizamos la base de datos local para que el chat refleje que está a salvo
        withContext(Dispatchers.IO) {
            val alertaExistente = db.alertaDao().obtenerAlertaPorIdAlerta(idAlerta)
            if (alertaExistente != null) {
                val alertaActualizada = alertaExistente.copy(
                    mensaje = "🟢 Alerta finalizada: $nombreUsuario se encuentra a salvo."
                )
                db.alertaDao().insertarAlerta(alertaActualizada)
            }
        }

        withContext(Dispatchers.Main) {
            notificationUtils.showNotification(
                "canal_estado_alerta",
                "Alerta de $nombreUsuario: A salvo",
                "El estado de la alerta ha cambiado a: A salvo",
                idAlerta,
                "#00FF00"
            )
        }
    }

    private suspend fun manejarActivacion(json: JSONObject) {
        val idAlerta = json.optInt("id_alerta")
        val nombreUsuario = json.optString("nombre_victima", "Alguien")

        withContext(Dispatchers.Main) {
            notificationUtils.showNotification(
                "canal_estado_alerta",
                "Alerta de $nombreUsuario: En peligro",
                "El estado de la alerta ha cambiado a: En peligro",
                idAlerta,
                "#FF0000"
            )
        }
    }

    private suspend fun manejarEmergenciaSos(json: JSONObject) {
        val idAlerta = json.optInt("id_alerta")
        val nombreUsuario = json.optString("nombre_victima", "Alguien")
        val idUsuario = json.optInt("id_usuario")
        val mensaje = json.optString("mensaje", "¡Auxilio!")
        val latitud = json.optDouble("latitud")
        val longitud = json.optDouble("longitud")

        val alertaEntity = AlertaEntity(
            id_local = 0,
            id_alerta = idAlerta,
            id_usuario = idUsuario,
            nombre_usuario = nombreUsuario,
            mensaje = mensaje,
            latitud = latitud,
            longitud = longitud,
            timestamp = System.currentTimeMillis()
        )

        withContext(Dispatchers.IO) {
            db.alertaDao().insertarAlerta(alertaEntity)
        }

        withContext(Dispatchers.Main) {
            notificationUtils.showNotification(
                "canal_emergencia_sos",
                "EMERGENCIA: $nombreUsuario",
                mensaje,
                System.currentTimeMillis().toInt()
            )
        }
    }
}