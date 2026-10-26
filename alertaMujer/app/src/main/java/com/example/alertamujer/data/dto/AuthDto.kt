package com.example.alertamujer.data.dto

import com.google.gson.annotations.SerializedName

// Lo que envías para loguearte
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

// Lo que envías para registrarte
data class RegistroRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("contrasena") val contrasena: String, // Debe llamarse igual a la variable de Java
    @SerializedName("fechaNacimiento") val fechaNacimiento: String
)

typealias RegisterRequest = RegistroRequest

data class AuthResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("mensaje") val mensaje: String?,
    @SerializedName("token") val token: String?,
    @SerializedName("refreshToken") val refreshToken: String?,
    @SerializedName("id_usuario") val id_usuario: Int?,
    @SerializedName("nombre") val nombre: String?
)

data class ForgotPasswordRequest(
    @SerializedName("email") val email: String
)

data class ResetPasswordRequest(
    @SerializedName("email") val email: String,
    @SerializedName("codigo") val codigo: String,
    @SerializedName("nuevaPassword") val nuevaPassword: String
)

data class VerifyCodeRequest(
    @SerializedName("email") val email: String,
    @SerializedName("codigo") val codigo: String
)
