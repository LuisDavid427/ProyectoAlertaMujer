package com.example.alertamujer.data.network.services

import com.example.alertamujer.data.dto.FcmTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UsuarioService {
    @POST("api/usuarios/actualizar-token")
    suspend fun actualizarToken(@Body request: FcmTokenRequest): Response<Void>
}