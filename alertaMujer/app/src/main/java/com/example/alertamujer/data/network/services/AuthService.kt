package com.example.alertamujer.data.network.services

import com.example.alertamujer.data.dto.AuthResponse
import com.example.alertamujer.data.dto.ForgotPasswordRequest
import com.example.alertamujer.data.dto.LoginRequest
import com.example.alertamujer.data.dto.RegisterRequest
import com.example.alertamujer.data.dto.ResetPasswordRequest
import com.example.alertamujer.data.dto.VerifyCodeRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {
    @POST("api/auth/login-movil")
    suspend fun login(@Body datos: LoginRequest): Response<AuthResponse>

    @POST("api/usuarios/guardar")
    suspend fun registrar(@Body datos: RegisterRequest): Response<AuthResponse>

    @POST("api/usuarios/actualizar-token")
    suspend fun actualizarToken(
        @Query("idUsuario") idUsuario: Int,
        @Query("token") token: String
    ): Response<Void>

    @POST("api/auth/forgot-password")
    suspend fun solicitarRecuperacion(@Body request: ForgotPasswordRequest): Response<AuthResponse>

    @POST("api/auth/reset-password")
    suspend fun cambiarPassword(@Body request: ResetPasswordRequest): Response<AuthResponse>

    @POST("api/auth/verificar-codigo")
    suspend fun verificarCodigo(@Body request: VerifyCodeRequest): Response<AuthResponse>
}
