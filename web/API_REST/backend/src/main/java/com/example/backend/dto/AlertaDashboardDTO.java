package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlertaDashboardDTO {
    private Integer idAlerta;
    private String nombreVictima;
    private String mensaje;
    private String estadoAlerta;       // 4to campo (fila[3])
    private LocalDateTime fecha;       // 5to campo (fila[4])
    private BigDecimal latitud;        // 6to campo (fila[5])
    private BigDecimal longitud;       // 7mo campo (fila[6])
}