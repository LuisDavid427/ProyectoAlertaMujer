package com.example.registro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios_roles")
@Data
public class UsuarioRolModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ur")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioModel usuario;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private RolModel rol;

    @Column(name = "fechaasignacion", updatable = false)
    private LocalDateTime fechaAsignacion;

    @PrePersist
    protected void onCreate() {
        this.fechaAsignacion = LocalDateTime.now();
    }
}