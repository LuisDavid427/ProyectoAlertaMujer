package com.example.alertamujer.data.network.repository

import com.example.alertamujer.data.dto.LoginRequest
import com.example.alertamujer.data.dto.RegistroRequest
import com.example.alertamujer.data.dto.AuthResponse
import com.example.alertamujer.data.network.RetrofitClient
import com.example.alertamujer.data.network.fcm.AlertaFCMService
import retrofit2.Response

class AuthRepository {

    // Función para el Login
    suspend fun login(request: LoginRequest): Response<AuthResponse> {
        return RetrofitClient.authService.login(request)
    }

    // NUEVO: Función para el Registro
    suspend fun registrar(request: RegistroRequest): Response<AuthResponse> {
        return RetrofitClient.authService.registrar(request)
    }
    // En AuthRepository.kt
    suspend fun actualizarTokenFCM(idUsuario: Int, token: String): Response<Void> {
        // Este endpoint lo crearemos en Spring Boot (ej: /api/usuarios/actualizar-token)
        return RetrofitClient.actualizarToken(idUsuario, token)
    }
}