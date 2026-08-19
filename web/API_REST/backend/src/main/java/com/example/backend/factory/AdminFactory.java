package com.example.backend.factory;

import com.example.backend.model.UsuarioModel;
import com.example.backend.model.UsuarioRolModel;
import com.example.backend.model.RolModel; // Reemplaza por tu entidad o enum de Rol
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class AdminFactory {

    private final PasswordEncoder passwordEncoder;

    public AdminFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioModel crearAdmin(String nombre, String email, String rawPassword, RolModel rolAdmin) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El correo no puede estar vacío.");
        }
        if (rawPassword == null || rawPassword.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }

        UsuarioModel admin = new UsuarioModel();
        admin.setNombre(nombre.trim());
        admin.setEmail(email.trim().toLowerCase());
        
        // Cifrado obligatorio en BCrypt sobre tu campo 'contrasena_hash'
        admin.setContrasena_hash(passwordEncoder.encode(rawPassword));
        admin.setEstadoUsuario(true);

        // Instanciamos la relación intermedia UsuarioRolModel
        UsuarioRolModel usuarioRol = new UsuarioRolModel();
        usuarioRol.setUsuario(admin);
        usuarioRol.setRol(rolAdmin); // Asignamos la entidad Rol de Admin

        if (admin.getRolesAsignados() == null) {
            admin.setRolesAsignados(new ArrayList<>());
        }
        admin.getRolesAsignados().add(usuarioRol);

        return admin;
    }
}