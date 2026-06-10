package com.example.alertamujer.data.network.services

import com.example.alertamujer.data.dto.AlertaRequest
import com.example.alertamujer.data.dto.AlertaResponse
import com.example.alertamujer.data.dto.UbicacionRequest
import com.example.alertamujer.data.dto.AlertaRecibidaDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Multipart
import retrofit2.http.Part

interface AlertaService {
    @POST("api/alertas/emitir")
    suspend fun enviarAlertaSOS(@Body alerta: AlertaRequest): Response<AlertaResponse>

    @POST("api/alertas/{id}/ubicacion")
    suspend fun enviarUbicacionContinua(
        @Path("id") idAlerta: Int,
        @Body request: UbicacionRequest
    ): Response<Map<String, Any>>

    @PUT("api/alertas/{id}/desactivar")
    suspend fun desactivarAlerta(@Path("id") idAlerta: Int): Response<Map<String, Any>>

    @Multipart
    @POST("api/alertas/{id}/evidencias")
    suspend fun subirEvidencia(
        @Path("id") idAlerta: Int,
        @Part archivo: MultipartBody.Part,
        @Part("tipo") tipo: RequestBody
    ): Response<Map<String, Any>>
}
