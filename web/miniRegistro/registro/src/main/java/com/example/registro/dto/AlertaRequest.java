package com.example.registro.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlertaRequest {
    // Solo pedimos lo que el móvil necesita enviar al momento del pánico
    private Integer idUsuario;
    private String mensaje;
    private String urlImagen;
    private String urlAudio;
}