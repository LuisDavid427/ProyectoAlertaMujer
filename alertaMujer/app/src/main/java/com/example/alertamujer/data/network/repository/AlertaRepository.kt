package com.example.alertamujer.data.repository

import com.example.alertamujer.data.dto.AlertaRequest
import com.example.alertamujer.data.dto.AlertaResponse
import com.example.alertamujer.data.network.RetrofitClient
import retrofit2.Response
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AlertaRepository {

    // El repositorio usa la instancia que creamos en el RetrofitClient
    suspend fun enviarAlertaSOS(request: AlertaRequest): Response<AlertaResponse> {
        return RetrofitClient.alertaService.enviarAlertaSOS(request)
    }
    suspend fun subirEvidencia(
        idAlerta: Int,
        archivo: MultipartBody.Part,
        tipo: RequestBody
    ): Response<Map<String, Any>> {
        return RetrofitClient.alertaService.subirEvidencia(idAlerta, archivo, tipo)
    }
}
