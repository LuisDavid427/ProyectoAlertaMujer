package com.example.registro.controller;

import com.example.registro.dto.LoginRequest;
import com.example.registro.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginData) {
        
        boolean esValido = usuarioService.esAdminValido(loginData.getEmail(), loginData.getPassword());
        
        if (esValido) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "mensaje", "Acceso concedido como Administrador"
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                    "success", false,
                    "error", "Credenciales incorrectas o permisos insuficientes"
                ));
        }
    }

    // POST: http://localhost:8080/api/auth/login-movil
    @PostMapping("/login-movil")
    public ResponseEntity<?> loginMovil(@RequestBody LoginRequest loginData) {
        
        Map<String, Object> datosUsuario = usuarioService.validarUsuarioMovil(
            loginData.getEmail(), 
            loginData.getPassword()
        );

        if (datosUsuario != null) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "id_usuario", datosUsuario.get("id_usuario"),
                "nombre", datosUsuario.get("nombre"),
                "mensaje", "Acceso concedido"
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "error", "Correo o contraseña incorrectos"
            ));
        }
    }
}