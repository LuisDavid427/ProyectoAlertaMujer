package com.example.alertamujer.data.model

data class MensajeAlerta(
    val id_alerta: Int,
    val nombre_usuario: String,
    val mensaje: String,
    val latitud: Double,
    val longitud: Double
)