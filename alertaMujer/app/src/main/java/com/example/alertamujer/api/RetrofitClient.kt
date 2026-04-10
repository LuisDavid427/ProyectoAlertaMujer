package com.example.alertamujer.api

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST



data class LoginRequest(
    val email: String,
    val password: String
)

data class RegistroRequest(
    val nombre: String,
    val email: String,
    val contrasena: String,
)

data class AuthResponse(
    val success: Boolean,
    val mensaje: String,
    val id_usuario: Int? = null,
    val error: String? = null
)

data class AlertaRequest(
    val latitud: Double,
    val longitud: Double,
    val id_usuario: Int,
    val mensaje: String
)


interface ApiService {
    @POST("api/auth/login-movil")
    suspend fun login(@Body datos: LoginRequest): Response<AuthResponse>

    @POST("api/usuarios/guardar")
    suspend fun registrar(@Body datos: RegistroRequest): Response<AuthResponse>


    @POST("api/alertas/emitir")
    suspend fun enviarAlertaSOS(@Body alerta: AlertaRequest): Response<Void>
}


object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.15:8080/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val instance: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}