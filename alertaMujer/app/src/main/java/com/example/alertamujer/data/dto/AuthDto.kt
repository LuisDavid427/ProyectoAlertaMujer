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
    val mensaje: String?,     // Hacemos opcional por si algún endpoint no lo envía
    val id_usuario: Int? = null,
    val nombre: String? = null // Añadido para atrapar el nombre que envía Spring Boot
)