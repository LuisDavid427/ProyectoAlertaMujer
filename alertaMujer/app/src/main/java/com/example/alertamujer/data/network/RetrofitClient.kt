package com.example.alertamujer.data.network

import android.content.Context
import com.example.alertamujer.data.network.services.AuthService
import com.example.alertamujer.data.network.services.AlertaService
import com.example.alertamujer.data.network.services.UsuarioService
import com.example.alertamujer.util.SessionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.22:8080/"

    private val clientWithoutAuth = OkHttpClient.Builder().build()
    private val retrofitWithoutAuth = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(clientWithoutAuth)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authService: AuthService = retrofitWithoutAuth.create(AuthService::class.java)

    private fun getRetrofitWithAuth(context: Context): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val sessionManager = SessionManager(context)
                val token = sessionManager.obtenerToken()

                val requestBuilder = chain.request().newBuilder()
                if (!token.isNullOrBlank()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                var response = chain.proceed(requestBuilder.build())

                // Captura el 403 (o 401) cuando el token expira y fuerza la renovación
                if (response.code == 403 || response.code == 401) {
                    response.close() // Liberar recursos de la respuesta fallida

                    val authenticator = TokenAuthenticator(context, sessionManager)
                    val nuevaPeticion = authenticator.authenticate(null, response)

                    if (nuevaPeticion != null) {
                        response = chain.proceed(nuevaPeticion)
                    }
                }

                response
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getAlertaService(context: Context): AlertaService {
        return getRetrofitWithAuth(context).create(AlertaService::class.java)
    }

    fun getUsuarioService(context: Context): UsuarioService {
        return getRetrofitWithAuth(context).create(UsuarioService::class.java)
    }
}