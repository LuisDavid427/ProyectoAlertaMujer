package com.example.alertamujer.data.network

import com.example.alertamujer.data.network.services.AuthService
import com.example.alertamujer.data.network.services.AlertaService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.22:8080/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    val alertaService: AlertaService by lazy {
        retrofit.create(AlertaService::class.java)
    }
}