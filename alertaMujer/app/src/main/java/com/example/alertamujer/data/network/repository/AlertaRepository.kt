package com.example.alertamujer.data.network.repository

import android.content.Context
import com.example.alertamujer.data.dto.AlertaRequest
import com.example.alertamujer.data.dto.AlertaResponse
import com.example.alertamujer.data.dto.UbicacionRequest
import com.example.alertamujer.data.local.AppDatabase
import com.example.alertamujer.data.local.entity.AlertaEntity
import com.example.alertamujer.data.local.entity.ContactoEntity
import com.example.alertamujer.data.network.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class AlertaRepository(private val context: Context) {

    private val alertaService = RetrofitClient.getAlertaService(context)
    private val db = AppDatabase.getDatabase(context)

    // --- Operaciones Locales (Room) ---
    suspend fun obtenerContactosLocales(): List<ContactoEntity> {
        return db.contactoDao().obtenerTodosLosContactosSincrono()
    }

    suspend fun guardarAlertaLocal(alerta: AlertaEntity) {
        try {
            db.alertaDao().insertarAlerta(alerta)
        } catch (e: Exception) {
            // Manejo de error local si ocurre
        }
    }

    // --- Operaciones Remotas (Retrofit / Backend) ---
    suspend fun enviarAlertaSOS(request: AlertaRequest): Response<AlertaResponse> {
        return alertaService.enviarAlertaSOS(request)
    }

    suspend fun subirEvidencia(
        idAlerta: Int,
        archivo: MultipartBody.Part,
        tipo: RequestBody,
        listaEmails: List<String>? = null
    ): Response<Map<String, Any>> {
        // El repositorio solo coordina la llamada.
        // Si queremos enviar los emails como texto multipart,
        // la conversión se puede manejar limpiamente o delegar a una extensión,
        // pero el repositorio no debe ensuciarse con detalles de RequestBody de red.
        val contactosString = listaEmails?.joinToString(",") ?: ""
        val contactosBody = contactosString.toRequestBody("text/plain".toMediaTypeOrNull())

        return alertaService.subirEvidencia(idAlerta, archivo, tipo, contactosBody)
    }

    suspend fun enviarUbicacionContinua(
        idAlerta: Int,
        request: UbicacionRequest
    ): Response<Map<String, Any>> {
        return alertaService.enviarUbicacionContinua(idAlerta, request)
    }

    suspend fun desactivarAlerta(
        idAlerta: Int,
        body: Map<String, List<String>>? = null
    ): Response<Map<String, Any>> {
        return alertaService.desactivarAlerta(idAlerta, body)
    }
}