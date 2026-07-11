package com.example.alertamujer.data.repository

import com.example.alertamujer.data.dto.AlertaRequest
import com.example.alertamujer.data.dto.AlertaResponse
import com.example.alertamujer.data.dto.UbicacionRequest
import com.example.alertamujer.data.dto.AlertaRecibidaDTO
import com.example.alertamujer.data.network.RetrofitClient
import retrofit2.Response
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AlertaRepository {

    // Ahora recibimos el token como primer parámetro en todas las funciones
    suspend fun enviarAlertaSOS(token: String, request: AlertaRequest): Response<AlertaResponse> {
        return RetrofitClient.alertaService.enviarAlertaSOS(token, request)
    }

    suspend fun subirEvidencia(
        token: String,
        idAlerta: Int,
        archivo: MultipartBody.Part,
        tipo: RequestBody
    ): Response<Map<String, Any>> {
        return RetrofitClient.alertaService.subirEvidencia(token, idAlerta, archivo, tipo)
    }

    suspend fun enviarUbicacionContinua(
        token: String,
        idAlerta: Int,
        request: UbicacionRequest
    ): Response<Map<String, Any>> {
        return RetrofitClient.alertaService.enviarUbicacionContinua(token, idAlerta, request)
    }

    suspend fun desactivarAlerta(
        token: String,
        idAlerta: Int
    ): Response<Map<String, Any>> {
        return RetrofitClient.alertaService.desactivarAlerta(token, idAlerta)
    }
}