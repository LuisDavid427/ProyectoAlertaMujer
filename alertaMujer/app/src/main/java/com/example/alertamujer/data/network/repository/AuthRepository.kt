package com.example.alertamujer.data.network.repository

import android.content.Context
import com.example.alertamujer.data.dto.LoginRequest
import com.example.alertamujer.data.dto.RegistroRequest
import com.example.alertamujer.data.dto.AuthResponse
import com.example.alertamujer.data.dto.FcmTokenRequest
import com.example.alertamujer.data.network.RetrofitClient
import retrofit2.Response

class AuthRepository(private val context: Context) {

    // Login utiliza authService que no requiere token previo
    suspend fun login(request: LoginRequest): Response<AuthResponse> {
        return RetrofitClient.authService.login(request)
    }

    // Registro también usa authService público
    suspend fun registrar(request: RegistroRequest): Response<AuthResponse> {
        return RetrofitClient.authService.registrar(request)
    }

    // Actualizar Token FCM ahora pasa el contexto para inyectar el token autenticado de forma transparente
    suspend fun actualizarTokenFCM(idUsuario: Int, token: String): Response<Void> {
        val request = FcmTokenRequest(idUsuario = idUsuario, token = token)
        return RetrofitClient.getUsuarioService(context).actualizarToken(request)
    }

    suspend fun solicitarRecuperacion(email: String): Response<AuthResponse> {
        val requestMap = mapOf("email" to email)
        return RetrofitClient.authService.solicitarRecuperacion(requestMap)
    }

    suspend fun cambiarPassword(email: String, codigo: String, nuevaPassword: String): Response<AuthResponse> {
        val requestMap = mapOf(
            "email" to email,
            "codigo" to codigo,
            "nuevaPassword" to nuevaPassword
        )
        return RetrofitClient.authService.cambiarPassword(requestMap)
    }
    suspend fun verificarCodigo(email: String, codigo: String): Response<AuthResponse> {
        val requestMap = mapOf("email" to email, "codigo" to codigo)
        return RetrofitClient.authService.verificarCodigo(requestMap)
    }
}