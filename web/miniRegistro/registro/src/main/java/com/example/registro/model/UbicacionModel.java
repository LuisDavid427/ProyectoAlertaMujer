package com.example.registro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ubicaciones")
@Getter
@Setter
public class UbicacionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ubicacion")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_alerta", nullable = false)
    private AlertaModel alerta;

    @Column(name = "latitud", precision = 10, scale = 7, nullable = false)
    private BigDecimal latitud;

    @Column(name = "longitud", precision = 10, scale = 7, nullable = false)
    private BigDecimal longitud;

    @Column(name = "velocidad", precision = 5, scale = 2)
    private BigDecimal velocidad;

    @Column(name = "precision_gps", precision = 5, scale = 2)
    private BigDecimal precisionGps;

    @Column(name = "fecha_hora_registro", insertable = false, updatable = false)
    private LocalDateTime fechaHoraRegistro;
}