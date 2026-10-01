package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@Getter
@Setter
public class AlertaRequest {
    
    @JsonProperty("id_usuario")
    private Integer idUsuario;
    
    private String mensaje;
    private Double latitud;
    private Double longitud;

    @JsonProperty("contactosNotificar")
    private List<String> contactosNotificar;
}