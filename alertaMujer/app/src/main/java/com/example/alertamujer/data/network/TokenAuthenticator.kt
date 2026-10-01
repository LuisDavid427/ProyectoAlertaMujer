package com.example.alertamujer.data.network

import android.content.Context
import com.example.alertamujer.util.SessionManager
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class TokenAuthenticator(
    private val context: Context,
    private val sessionManager: SessionManager = SessionManager(context)
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            sessionManager.limpiarSesion()
            return null
        }

        synchronized(this) {
            val tokenActual = sessionManager.obtenerToken()
            val tokenEnHeader = response.request.header("Authorization")?.replace("Bearer ", "")?.trim()

            if (!tokenActual.isNullOrBlank() && tokenActual != tokenEnHeader) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $tokenActual")
                    .build()
            }

            val nuevoToken = solicitarNuevoToken() ?: run {
                sessionManager.limpiarSesion()
                return null
            }

            sessionManager.guardarToken(nuevoToken)

            return response.request.newBuilder()
                .header("Authorization", "Bearer $nuevoToken")
                .build()
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    private fun solicitarNuevoToken(): String? {
        val tokenViejo = sessionManager.obtenerToken()
        if (tokenViejo.isNullOrBlank()) return null

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("http://192.168.1.22:8080/api/auth/refresh")
            .post("".toRequestBody(null))
            .addHeader("Authorization", "Bearer $tokenViejo")
            .build()

        return try {
            val res = client.newCall(request).execute()
            if (res.isSuccessful) {
                val resJson = JSONObject(res.body?.string() ?: "")
                resJson.optString("token", null)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}