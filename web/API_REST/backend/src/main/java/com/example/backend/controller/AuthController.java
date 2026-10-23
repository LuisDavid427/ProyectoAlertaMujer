package com.example.backend.controller;

import com.example.backend.dto.FcmTokenRequest;
import com.example.backend.dto.LoginRequest;
import com.example.backend.model.UsuarioModel;
import com.example.backend.security.JwtUtil;
import com.example.backend.service.AuthService;
import com.example.backend.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, UsuarioService usuarioService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login-movil")
    public ResponseEntity<?> loginMovil(@RequestBody LoginRequest loginData) {
        Map<String, Object> datosUsuario = authService.validarMovil(loginData.getEmail(), loginData.getPassword());

        if (datosUsuario != null) {
            String rolUsuario = (String) datosUsuario.getOrDefault("rol", "ROLE_USUARIO");
            String tokenGenerado = jwtUtil.generarToken(loginData.getEmail(), rolUsuario);

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

    @PostMapping("/login")
    public ResponseEntity<?> loginWeb(@RequestBody LoginRequest loginData) {
        String token = authService.autenticarAdmin(loginData.getEmail(), loginData.getPassword());

        if (token != null) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "mensaje", "Acceso concedido como Administrador",
                "token", token
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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> solicitarRecuperacion(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "error", "El correo electrónico es obligatorio"
            ));
        }

        boolean enviado = authService.procesarRecuperacionPassword(email);

        if (!enviado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "mensaje", "El correo ingresado no está registrado en el sistema"
            ));
        }

        return ResponseEntity.ok(Map.of(
            "success", true,
            "mensaje", "Código enviado con éxito"
        ));
    }

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
