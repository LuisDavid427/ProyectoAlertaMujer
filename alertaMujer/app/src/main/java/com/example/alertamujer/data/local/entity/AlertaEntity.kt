package com.example.alertamujer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.alertamujer.data.model.MensajeAlerta

@Entity(tableName = "tabla_alertas")
data class AlertaEntity(
    @PrimaryKey(autoGenerate = true) val id_local: Int = 0,
    val id_alerta: Int,
    val nombre_usuario: String,
    val mensaje: String,
    val latitud: Double,
    val longitud: Double,
    val timestamp: Long = System.currentTimeMillis()
)

// Función de extensión para convertir Entity a Modelo
fun AlertaEntity.toMensajeAlerta() = MensajeAlerta(
    id_alerta = this.id_alerta,
    nombre_usuario = this.nombre_usuario,
    mensaje = this.mensaje,
    latitud = this.latitud,
    longitud = this.longitud
)