package com.example.alertamujer.domain.model

// --- MOLDES DE AUTH ---
data class LoginRequest(val email: String, val password: String)
data class RegistroRequest(val nombre: String, val email: String, val contrasena: String)
data class AuthResponse(val success: Boolean, val mensaje: String, val id_usuario: Int? = null)

// --- MOLDES DE ALERTA ---
data class AlertaRequest(
    val id_usuario: Int,
    val mensaje: String,
    val latitud: Double,
    val longitud: Double
)

data class AlertaResponse(
    val success: Boolean,
    val mensaje: String,
    val id_alerta: Int
)

data class UbicacionRequest(
    val latitud: Double,
    val longitud: Double
)