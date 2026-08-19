package com.example.backend.service;

import com.example.backend.model.UsuarioModel;
import com.example.backend.security.JwtUtil; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- AGREGAR ESTA IMPORTACIÓN

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // LOGIN MÓVIL (Android)
    @Transactional(readOnly = true)
    public Map<String, Object> validarMovil(String email, String passwordPlano) {
        Optional<UsuarioModel> userOpt = usuarioService.buscarPorEmail(email);
        
        if (userOpt.isPresent() && passwordEncoder.matches(passwordPlano, userOpt.get().getContrasena_hash())) {
            UsuarioModel usuario = userOpt.get();
            Map<String, Object> datos = new HashMap<>();
            datos.put("id_usuario", usuario.getId());
            datos.put("nombre", usuario.getNombre());
            
            // Extraemos el rol (si tiene colección de roles) o asignamos ROLE_USUARIO
            String rol = "ROLE_USUARIO";
            if (usuario.getRolesAsignados() != null && !usuario.getRolesAsignados().isEmpty()) {
                rol = usuario.getRolesAsignados().iterator().next().getRol().getNombreRol();
            }
            
            datos.put("rol", rol);
            return datos;
        }
        return null;
    }

    // AÑADIR @Transactional PARA MANTENER LA SESIÓN JPA ABIERTA Y CARGAR ROLES
    @Transactional(readOnly = true)
    public String autenticarAdmin(String email, String passwordPlano) {
        Optional<UsuarioModel> usuarioOpt = usuarioService.buscarPorEmail(email);

        if (usuarioOpt.isEmpty()) {
            return null;
        }

        UsuarioModel usuario = usuarioOpt.get();

        // 1. Validar contraseña
        if (!passwordEncoder.matches(passwordPlano, usuario.getContrasena_hash())) {
            return null;
        }

        // 2. Control nulo de seguridad por si no tiene roles asignados
        if (usuario.getRolesAsignados() == null || usuario.getRolesAsignados().isEmpty()) {
            return null;
        }

        // 3. BLINDAJE DE ROL: Verificar si realmente tiene asignado el rol ADMIN
        boolean esAdmin = usuario.getRolesAsignados().stream()
                .filter(ur -> ur.getRol() != null)
                .anyMatch(ur -> {
                    String nombreRol = ur.getRol().getNombreRol(); // Ver si en RolModel se llama getNombreRol() o getNombre()
                    return nombreRol != null && (
                        nombreRol.equalsIgnoreCase("ROLE_ADMIN") || 
                        nombreRol.equalsIgnoreCase("ADMIN") ||
                        nombreRol.equalsIgnoreCase("ADMINISTRADOR")
                    );
                });

        if (!esAdmin) {
            return null; 
        }

        // 4. Generar token pasando el correo Y el rol explícito
        return jwtUtil.generarToken(usuario.getEmail(), "ROLE_ADMIN");
    }
}