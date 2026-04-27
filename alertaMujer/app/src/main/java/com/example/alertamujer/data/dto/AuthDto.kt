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

// Lo que el servidor te responde en ambos casos
data class AuthResponse(
    val success: Boolean,
    val mensaje: String,
    val id_usuario: Int? = null
)