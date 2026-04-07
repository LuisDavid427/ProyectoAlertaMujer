package com.example.registro.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlertaRequest {
    private Integer idUsuario;
    private String mensaje;
    private String urlImagen;
    private String urlAudio;
}