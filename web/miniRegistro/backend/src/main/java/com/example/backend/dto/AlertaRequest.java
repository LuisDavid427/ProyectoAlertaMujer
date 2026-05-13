package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter


public class AlertaRequest {
    
    @JsonProperty("id_usuario") // Esto amarra el nombre de Android con el de Java
    private long idUsuario;
    private String mensaje;
    // --- NUEVOS CAMPOS GPS ---
    private Double latitud;
    private Double longitud;
}