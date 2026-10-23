package com.example.alertamujer.data.dto

import com.google.gson.annotations.SerializedName

// Lo que envías para loguearte
data class LoginRequest(
    val email: String,
    val password: String
)

// Lo que envías para registrarte
data class RegistroRequest(
    val nombre: String,
    val email: String,
    val contrasena: String,
    val fechaNacimiento: String
)

data class AuthResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("mensaje")
    val mensaje: String?,

    @SerializedName("token")
    val token: String?,

    @SerializedName("refreshToken")
    val refreshToken: String?,

    @SerializedName("id_usuario")
    val id_usuario: Int?,

    @SerializedName("nombre")
    val nombre: String?
)

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val email: String,
    val codigo: String,
    val nuevaPassword: String
)

data class VerifyCodeRequest(
    val email: String,
    val codigo: String
)
