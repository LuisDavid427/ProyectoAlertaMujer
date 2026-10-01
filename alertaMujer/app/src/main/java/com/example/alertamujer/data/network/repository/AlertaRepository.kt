package com.example.alertamujer.data.repository

import android.content.Context
import com.example.alertamujer.data.dto.AlertaRequest
import com.example.alertamujer.data.dto.AlertaResponse
import com.example.alertamujer.data.dto.UbicacionRequest
import com.example.alertamujer.data.network.RetrofitClient
import retrofit2.Response
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AlertaRepository(private val context: Context) {

    suspend fun enviarAlertaSOS(request: AlertaRequest): Response<AlertaResponse> {
        return RetrofitClient.getAlertaService(context).enviarAlertaSOS(request)
    }

    suspend fun subirEvidencia(
        idAlerta: Int,
        archivo: MultipartBody.Part,
        tipo: RequestBody
    ): Response<Map<String, Any>> {
        return RetrofitClient.getAlertaService(context).subirEvidencia(idAlerta, archivo, tipo)
    }

    suspend fun enviarUbicacionContinua(
        idAlerta: Int,
        request: UbicacionRequest
    ): Response<Map<String, Any>> {
        return RetrofitClient.getAlertaService(context).enviarUbicacionContinua(idAlerta, request)
    }

    suspend fun desactivarAlerta(
        idAlerta: Int
    ): Response<Map<String, Any>> {
        return RetrofitClient.getAlertaService(context).desactivarAlerta(idAlerta)
    }
}