package com.example.alertamujer.data.network.repository

import com.example.alertamujer.data.dto.LoginRequest
import com.example.alertamujer.data.dto.RegistroRequest
import com.example.alertamujer.data.dto.AuthResponse
import com.example.alertamujer.data.dto.FcmTokenRequest
import com.example.alertamujer.data.network.RetrofitClient
import retrofit2.Response

class AuthRepository {

    // Función para el Login
    suspend fun login(request: LoginRequest): Response<AuthResponse> {
        return RetrofitClient.authService.login(request)
    }

    // Función para el Registro
    suspend fun registrar(request: RegistroRequest): Response<AuthResponse> {
        return RetrofitClient.authService.registrar(request)
    }

    // Función para actualizar el Token FCM
    suspend fun actualizarTokenFCM(idUsuario: Int, token: String): Response<Void> {
        // 1. Empacamos los datos en el objeto que espera Spring Boot
        val request = FcmTokenRequest(idUsuario = idUsuario, token = token)

        // 2. Consumimos el endpoint usando el nuevo usuarioService
        return RetrofitClient.usuarioService.actualizarToken(request)
    }
}