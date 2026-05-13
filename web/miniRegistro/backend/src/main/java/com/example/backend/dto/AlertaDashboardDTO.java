package com.example.backend.dto;

import java.time.LocalDateTime;

// Molde actualizado para coincidir con tu tabla original
public record AlertaDashboardDTO(
    Integer id_alerta,
    String nombre_victima, // Sacado de la tabla usuarios mediante la relación
    String mensaje,
    String estado_alerta,
    LocalDateTime fecha
) {}