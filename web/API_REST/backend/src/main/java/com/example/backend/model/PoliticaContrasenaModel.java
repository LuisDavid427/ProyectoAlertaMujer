package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "politicas_contrasenas")
@Data
public class PoliticaContrasenaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_politica")
    private Integer id;

    @Column(name = "nombre_politica", unique = true, nullable = false, length = 100)
    private String nombrePolitica;

    @Column(columnDefinition = "int default 8")
    private Integer minlongitud = 8;

    @Column(columnDefinition = "int default 20")
    private Integer maxlongitud = 20;

    @Column(name = "requiere_mayusculas")
    private Boolean requiereMayusculas = true;

    @Column(name = "requiere_numeros")
    private Boolean requiereNumeros = true;

    @Column(name = "requiere_simbolos")
    private Boolean requiereSimbolos = true;

    @Column(name = "caducidad_dias")
    private Integer caducidadDias = 90;

    @Column(name = "esta_activa")
    private Boolean estaActiva = true;
}