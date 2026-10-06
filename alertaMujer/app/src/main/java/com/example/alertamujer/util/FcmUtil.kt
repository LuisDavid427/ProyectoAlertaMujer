package com.example.alertamujer.util

import android.content.Context
import android.util.Log
import com.example.alertamujer.data.dto.FcmTokenRequest
import com.example.alertamujer.data.network.RetrofitClient
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object FcmUtil {

    fun sincronizarTokenSesion(context: Context) {
        val sessionManager = SessionManager(context)
        val idUsuario = sessionManager.obtenerIdUsuario()

        if (idUsuario == -1) {
            Log.w("FCM_TOKEN", "⚠️ No se sincronizó FCM: idUsuario no válido (-1).")
            return
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                val fcmToken = task.result

                // 1. Guardar localmente en SessionManager
                sessionManager.guardarFcmToken(fcmToken)

                // 2. Enviar a Spring Boot
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val request = FcmTokenRequest(idUsuario = idUsuario, token = fcmToken)
                        val respuesta = RetrofitClient.getUsuarioService(context).actualizarToken(request)

                        if (respuesta.isSuccessful) {
                            Log.d("FCM_TOKEN", "🟢 Token FCM registrado en MySQL para ID usuario: $idUsuario")
                        } else {
                            Log.e("FCM_TOKEN", "🔴 Fallo del servidor al actualizar FCM: ${respuesta.code()}")
                        }
                    } catch (e: Exception) {
                        Log.e("FCM_TOKEN", "🔴 Error de red al sincronizar FCM token: ${e.message}")
                    }
                }
            } else {
                Log.e("FCM_TOKEN", "🔴 Error al obtener token desde Firebase Messaging", task.exception)
            }
        }
    }
}