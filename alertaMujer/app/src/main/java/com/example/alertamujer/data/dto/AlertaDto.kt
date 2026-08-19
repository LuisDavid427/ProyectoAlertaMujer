package com.example.alertamujer.data.dto
import com.google.gson.annotations.SerializedName


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




data class AlertaRequest(
    @SerializedName("id_usuario") // <--- Si tu DTO backend usa id_usuario / @JsonProperty("id_usuario")
    val idUsuario: Int?,

    @SerializedName("mensaje")
    val mensaje: String?,

    @SerializedName("latitud")
    val latitud: Double,

    @SerializedName("longitud")
    val longitud: Double,

    @SerializedName("contactosNotificar") // O "contactos_notificar" según como esté en Java
    val contactosNotificar: List<String>
)

// Asegúrate de que este archivo tenga exactamente los mismos parámetros
data class MensajeAlerta(
    val id_alerta: Int,
    val nombre_usuario: String,
    val mensaje: String,
    val latitud: Double,
    val longitud: Double // <--- DEBE coincidir con el nombre aquí
)