package com.example.registro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "permisos")
@Data
public class PermisoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permiso")
    private Integer id;

    @Column(name = "nombre_permiso", unique = true, nullable = false, length = 50)
    private String nombrePermiso;

    @Column(length = 255)
    private String descripcion;

    // Relación inversa: Un permiso puede estar en muchos roles
    @ManyToMany(mappedBy = "permisos")
    private List<RolModel> roles;
}