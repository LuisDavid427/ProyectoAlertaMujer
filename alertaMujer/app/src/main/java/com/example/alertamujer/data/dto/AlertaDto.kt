package com.example.alertamujer.data.dto

import com.google.gson.annotations.SerializedName

data class AlertaResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("id_alerta") val idAlerta: Int
)

data class UbicacionRequest(
    @SerializedName("latitud") val latitud: Double,
    @SerializedName("longitud") val longitud: Double
)

data class AlertaRecibidaDTO(
    @SerializedName("id_alerta") val idAlerta: Int,
    @SerializedName("id_usuario") val idUsuario: Int,
    @SerializedName("nombre_usuario") val nombreUsuario: String,
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("latitud") val latitud: Double,
    @SerializedName("longitud") val longitud: Double,
    @SerializedName("fecha_hora") val fechaHora: String
)

data class AlertaRequest(
    @SerializedName("id_usuario") val idUsuario: Int?,
    @SerializedName("mensaje") val mensaje: String?,
    @SerializedName("latitud") val latitud: Double,
    @SerializedName("longitud") val longitud: Double,
    @SerializedName("contactosNotificar") val contactosNotificar: List<String>
)

data class MensajeAlerta(
    @SerializedName("id_alerta") val idAlerta: Int,
    @SerializedName("nombre_usuario") val nombreUsuario: String,
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("latitud") val latitud: Double,
    @SerializedName("longitud") val longitud: Double
)

data class NotificacionEvidenciaDto(
    @SerializedName("id_alerta") val idAlerta: Int,
    @SerializedName("nombre_victima") val nombreVictima: String,
    @SerializedName("tipo_evidencia") val tipoEvidencia: String,
    @SerializedName("url_evidencia") val urlEvidencia: String,
    @SerializedName("accion") val accion: String
)
