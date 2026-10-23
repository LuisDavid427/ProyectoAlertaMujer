package com.example.alertamujer.data.network.repository

import android.content.Context
import com.example.alertamujer.data.dto.AuthResponse
import com.example.alertamujer.data.dto.FcmTokenRequest
import com.example.alertamujer.data.dto.ForgotPasswordRequest
import com.example.alertamujer.data.dto.LoginRequest
import com.example.alertamujer.data.dto.RegistroRequest
import com.example.alertamujer.data.dto.ResetPasswordRequest
import com.example.alertamujer.data.dto.VerifyCodeRequest
import com.example.alertamujer.data.network.RetrofitClient
import com.example.alertamujer.data.network.services.AuthService

import retrofit2.Response

class AuthRepository(private val context: Context) {

    private val authService: AuthService = RetrofitClient.getAuthService(context)

    suspend fun login(request: LoginRequest): Response<AuthResponse> {
        return authService.login(request)
    }

    suspend fun registrar(request: RegistroRequest): Response<AuthResponse> {
        return authService.registrar(request)
    }

    suspend fun actualizarTokenFCM(idUsuario: Int, token: String): Response<Void> {
        val request = FcmTokenRequest(idUsuario = idUsuario, token = token)
        return RetrofitClient.getUsuarioService(context).actualizarToken(request)
    }

    suspend fun solicitarRecuperacion(request: ForgotPasswordRequest): Response<AuthResponse> {
        return authService.solicitarRecuperacion(request)
    }

    suspend fun cambiarPassword(request: ResetPasswordRequest): Response<AuthResponse> {
        return authService.cambiarPassword(request)
    }

    suspend fun verificarCodigo(request: VerifyCodeRequest): Response<AuthResponse> {
        return authService.verificarCodigo(request)
    }
}
