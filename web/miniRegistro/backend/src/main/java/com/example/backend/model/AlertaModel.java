package com.example.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "alertas")
@Getter
@Setter
public class AlertaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioModel usuario;

    @Column(name = "fecha", insertable = false, updatable = false)
    private LocalDateTime fecha;

    @Column(name = "url_imagen", length = 255)
    private String urlImagen;

    @Column(name = "url_audio", length = 255)
    private String urlAudio;

    @Column(name = "mensaje", length = 250, nullable = false)
    private String mensaje;

    @Column(name = "estado_alerta", length = 50)
    private String estadoAlerta = "activa";

    @OneToMany(mappedBy = "alerta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UbicacionModel> ubicaciones;
}