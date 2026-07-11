package com.example.alertamujer.data.dto

// Lo que envías para loguearte
data class LoginRequest(
    val email: String,
    val password: String
)

// Lo que envías para registrarte
data class RegistroRequest(
    val nombre: String,
    val email: String,
    val contrasena: String
)


data class AuthResponse(
    val success: Boolean,
    val id_usuario: Int?,
    val nombre: String?,
    val token: String?, // <--- Esta es la propiedad que mapeará el JWT del backend
    val mensaje: String?
)