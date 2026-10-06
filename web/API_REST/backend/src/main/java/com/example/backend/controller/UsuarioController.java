package com.example.backend.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.FcmTokenRequest;
import com.example.backend.dto.LoginRequest;
import com.example.backend.model.RolModel;
import com.example.backend.model.UsuarioModel;
import com.example.backend.model.UsuarioRolModel;
import com.example.backend.repository.RolRepository;
import com.example.backend.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")  
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService; 

    @Autowired
    private RolRepository rolRepository;

    @PostMapping("/guardar")
    public ResponseEntity<?> guardar(@Valid @RequestBody UsuarioModel usuario) {
        try {
            RolModel rolUsuaria = rolRepository.findByNombreRol("usuaria")
                .orElseThrow(() -> new RuntimeException("Error: El rol 'USUARIA' no existe en la DB. ¡Ejecuta el INSERT!"));

            UsuarioRolModel relacion = new UsuarioRolModel();
            relacion.setUsuario(usuario);
            relacion.setRol(rolUsuaria);

            usuario.setRolesAsignados(List.of(relacion));

            usuarioService.guardar(usuario);
            
            return ResponseEntity.ok(Map.of("success", true, "mensaje", "Registrada con éxito"));

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
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

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstadoUsuario(
            @PathVariable("id") Integer id, 
            @RequestParam("activo") boolean activo) {
        try {
            usuarioService.actualizarEstado(id, activo);
            
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "mensaje", "Estado actualizado exitosamente"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
}