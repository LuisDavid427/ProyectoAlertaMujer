package com.example.backend.service;

import com.example.backend.model.UsuarioModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // LOGIN MÓVIL (Android)
    public Map<String, Object> validarMovil(String email, String passwordPlano) {
        Optional<UsuarioModel> userOpt = usuarioService.buscarPorEmail(email);
        
        if (userOpt.isPresent() && passwordEncoder.matches(passwordPlano, userOpt.get().getContrasena_hash())) {
            Map<String, Object> datos = new HashMap<>();
            datos.put("id_usuario", userOpt.get().getId());
            datos.put("nombre", userOpt.get().getNombre());
            return datos;
        }
        return null;
    }

    // LOGIN WEB (React)
    public boolean validarAdmin(String email, String passwordPlano) {
        return usuarioService.buscarPorEmail(email)
            .map(u -> passwordEncoder.matches(passwordPlano, u.getContrasena_hash()))
            .orElse(false);
    }
}