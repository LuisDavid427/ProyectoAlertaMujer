package com.example.alertamujer.data.dto

// Lo que envías cuando presionas el botón SOS
data class AlertaRequest(
    val id_usuario: Int,
    val mensaje: String,
    val latitud: Double,
    val longitud: Double
)

// Lo que el servidor te responde (te da el id_alerta para seguir mandando GPS)
data class AlertaResponse(
    val success: Boolean,
    val mensaje: String,
    val id_alerta: Int
)

// Lo que envías cada 5 segundos (el latido del GPS)
data class UbicacionRequest(
    val latitud: Double,
    val longitud: Double
)

data class AlertaRecibidaDTO(
    val id_alerta: Int,
    val id_usuario: Int,
    val nombre_usuario: String, // Tu backend debería enviar el nombre para saber quién es
    val mensaje: String,
    val latitud: Double,
    val longitud: Double,
    val fecha_hora: String // Fecha en la que se emitió
)