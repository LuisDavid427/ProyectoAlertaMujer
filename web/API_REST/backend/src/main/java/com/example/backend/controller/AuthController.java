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



    @PostMapping("/refresh")
    public ResponseEntity<?> refrescarToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String tokenViejo = authHeader.substring(7);
            try {
                String nuevoToken = jwtUtil.refrescarToken(tokenViejo);
                Map<String, String> respuesta = new HashMap<>();
                respuesta.put("token", nuevoToken);
                return ResponseEntity.ok(respuesta);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "INVALID_TOKEN", "mensaje", "No se pudo renovar el token"));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "MISSING_HEADER", "mensaje", "Encabezado Authorization no presente o invalido"));
    }

    // POST: http://localhost:8080/api/auth/forgot-password
    @PostMapping("/forgot-password")
    public ResponseEntity<?> solicitarRecuperacion(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "error", "El correo electrónico es obligatorio"
            ));
        }

        // Llamamos al servicio para buscar el correo y generar el código
        boolean enviado = authService.procesarRecuperacionPassword(email);

        // CORRECCIÓN: Si el servicio retorna false (el correo no existe), frenamos aquí y mandamos 404
        if (!enviado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "mensaje", "El correo ingresado no está registrado en el sistema"
            ));
        }

        // Si el correo sí existe, responde con éxito y envía el código
        return ResponseEntity.ok(Map.of(
            "success", true,
            "mensaje", "Código enviado con éxito"
        ));
    }

    // POST: http://localhost:8080/api/auth/reset-password
    @PostMapping("/reset-password")
    public ResponseEntity<?> cambiarPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String codigo = request.get("codigo");
        String nuevaPassword = request.get("nuevaPassword");

        if (email == null || codigo == null || nuevaPassword == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "error", "Todos los campos son obligatorios"
            ));
        }

        boolean actualizado = authService.actualizarPasswordConCodigo(email, codigo, nuevaPassword);

        if (actualizado) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "mensaje", "Contraseña actualizada exitosamente"
            ));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "error", "Código inválido o expirado"
            ));
        }
    }

    @PostMapping("/verificar-codigo")
    public ResponseEntity<?> verificarCodigo(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String codigo = request.get("codigo");
        
        boolean esValido = authService.verificarCodigoRecuperacion(email, codigo);
        if (esValido) {
            return ResponseEntity.ok(Map.of("success", true, "mensaje", "Código válido"));
        } else {
            return ResponseEntity.status(400).body(Map.of("success", false, "mensaje", "Código inválido o expirado"));
        }
    }
}