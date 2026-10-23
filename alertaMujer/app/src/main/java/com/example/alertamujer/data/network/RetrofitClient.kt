package com.example.alertamujer.data.network

import android.content.Context
import com.example.alertamujer.BuildConfig
import com.example.alertamujer.data.network.services.AlertaService
import com.example.alertamujer.data.network.services.AuthService
import com.example.alertamujer.data.network.services.UsuarioService
import com.example.alertamujer.util.SessionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private fun buildClient(context: Context): OkHttpClient {
        val appContext = context.applicationContext
        val sessionManager = SessionManager(appContext)

        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = sessionManager.obtenerToken()
                val originalRequest = chain.request()

                val request = if (!token.isNullOrBlank()) {
                    originalRequest.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                } else {
                    originalRequest
                }

                chain.proceed(request)
            }
            .authenticator(TokenAuthenticator(appContext, sessionManager))
            .build()
    }

    private fun buildRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(buildClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getAuthService(context: Context): AuthService {
        return buildRetrofit(context).create(AuthService::class.java)
    }

    fun getAlertaService(context: Context): AlertaService {
        return buildRetrofit(context).create(AlertaService::class.java)
    }

    fun getUsuarioService(context: Context): UsuarioService {
        return buildRetrofit(context).create(UsuarioService::class.java)
    }
}
