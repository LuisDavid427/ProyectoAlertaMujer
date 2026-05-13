package com.example.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;


@Entity
@Table(name = "usuarios_contactos")
public class UsuarioContactoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_uc;

    // La usuaria que pide ayuda (Dueña de la red)
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioModel usuario;

    // El usuario que actúa como protector
    @ManyToOne
    @JoinColumn(name = "id_contacto", nullable = false)
    private UsuarioModel contacto;
}