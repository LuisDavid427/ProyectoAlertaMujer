package com.example.alertamujer.data.network.services

import com.example.alertamujer.data.dto.LoginRequest
import com.example.alertamujer.data.dto.RegistroRequest
import com.example.alertamujer.data.dto.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {
    @POST("api/auth/login-movil")
    suspend fun login(@Body datos: LoginRequest): Response<AuthResponse>

    @POST("api/usuarios/guardar")
    suspend fun registrar(@Body datos: RegistroRequest): Response<AuthResponse>

    @POST("api/usuarios/actualizar-token")
    suspend fun actualizarToken(
        @Query("idUsuario") idUsuario: Int,
        @Query("token") token: String
    ): Response<Void>
}