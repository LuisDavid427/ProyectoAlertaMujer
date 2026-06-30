package com.example.alertamujer.data.model

data class MensajeAlerta(
    val idAlerta: String,        // Un ID único para que Android no confunda los mensajes
    val nombreVictima: String,
    val mensaje: String,
    val latitud: Double,
    val longitud: Double,
    val timestamp: Long          // Guardar el tiempo en milisegundos es mejor para ordenar el chat
)