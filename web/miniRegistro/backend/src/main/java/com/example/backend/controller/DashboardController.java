package com.example.backend.controller;

import com.example.backend.dto.AlertaDashboardDTO;
import com.example.backend.dto.UsuarioDashboardDTO;
import com.example.backend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*") // Clave para que React se conecte
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioDashboardDTO>> listarUsuarios(@RequestParam(required = false) String q) {
        // q es el parámetro 'busqueda' que manda React
        return ResponseEntity.ok(dashboardService.listarUsuarios(q));
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<AlertaDashboardDTO>> listarAlertas(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(dashboardService.listarAlertas(q));
    }
}