package com.example.backend.controller;

import com.example.backend.dto.FcmTokenRequest;
import com.example.backend.dto.LoginRequest;
import com.example.backend.model.UsuarioModel;
import com.example.backend.security.JwtUtil;
import com.example.backend.service.AuthService;
import com.example.backend.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private JwtUtil jwtUtil;

    // POST: http://localhost:8080/api/auth/login-movil
    @PostMapping("/login-movil")
    public ResponseEntity<?> loginMovil(@RequestBody LoginRequest loginData) {
        
        // 1. Validamos credenciales en la base de datos
        Map<String, Object> datosUsuario = authService.validarMovil(loginData.getEmail(), loginData.getPassword());

        if (datosUsuario != null) {
            String rolUsuario = (String) datosUsuario.getOrDefault("rol", "ROLE_USUARIO");
            
            // 2. ¡AQUÍ ESTÁ EL CAMBIO! Generamos el Token INCLUYENDO EL ROL
            String tokenGenerado = jwtUtil.generarToken(loginData.getEmail(), rolUsuario);

            // 3. Empaquetamos la respuesta para Android
            return ResponseEntity.ok(Map.of(
                "success", true,
                "id_usuario", datosUsuario.get("id_usuario"),
                "nombre", datosUsuario.get("nombre"),
                "token", tokenGenerado,
                "mensaje", "Acceso concedido"
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "error", "Correo o contraseña incorrectos"
            ));
        }
    }

// POST: http://localhost:8080/api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> loginWeb(@RequestBody LoginRequest loginData) {
        // 1. Llamamos a la nueva función que valida clave + rol ADMIN y genera el JWT
        String token = authService.autenticarAdmin(loginData.getEmail(), loginData.getPassword());
        
        // 2. Si token NO es nulo, el login fue exitoso y devolvemos el token a React
        if (token != null) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "mensaje", "Acceso concedido como Administrador",
                "token", token // <--- Retornamos el JWT
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "error", "Credenciales incorrectas o permisos insuficientes"
            ));
        }
    }

    @PostMapping("/actualizar-token")
    public ResponseEntity<?> actualizarToken(@RequestBody FcmTokenRequest request) {
        try {
            usuarioService.actualizarTokenFcm(request.getIdUsuario(), request.getToken());
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "mensaje", "Token de FCM actualizado correctamente"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false, 
                "error", e.getMessage()
            ));
        }
    }
}