package com.example.registro.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.registro.dto.LoginRequest;
import com.example.registro.model.RolModel;
import com.example.registro.model.UsuarioModel;
import com.example.registro.model.UsuarioRolModel;
import com.example.registro.repository.RolRepository;
import com.example.registro.service.UsuarioService;

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
        RolModel rolUsuaria = rolRepository.findByNombreRol("USUARIA")
            .orElseThrow(() -> new RuntimeException("Error: El rol 'USUARIA' no existe en la DB. ¡Ejecuta el INSERT!"));

        UsuarioRolModel relacion = new UsuarioRolModel();
        relacion.setUsuario(usuario);
        relacion.setRol(rolUsuaria);

        usuario.setRolesAsignados(List.of(relacion));

        usuarioService.guardar(usuario);
        
        return ResponseEntity.ok(Map.of("success", true, "mensaje", "Registrada con éxito"));

    } catch (Exception e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}



    @GetMapping("/listar")
    public List<UsuarioModel> listar() {
        return usuarioService.listarTodos();
    }
}