package com.example.alertamujer.alerta

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// 1. El modelo de datos (Lo que enviaremos a Spring Boot)
data class AlertaRequest(
    val latitud: Double,
    val longitud: Double
)

// 2. La interfaz que define la ruta (Endpoint)
interface ApiService {
    // Cambiamos "enviar" por "emitir" para que coincida con tu controlador
    @POST("api/alertas/emitir")
    suspend fun enviarAlertaSOS(@Body alerta: AlertaRequest): Response<Void>
}

// 3. El cliente de Retrofit
object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.15:8080/"
    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}