package com.example.backend.controller;

import com.example.backend.dto.AlertaDashboardDTO;
import com.example.backend.dto.UsuarioDashboardDTO;
import com.example.backend.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(
        origins = {
                "http://localhost:5173",
                "http://127.0.0.1:5173"
        },
        methods = {RequestMethod.GET, RequestMethod.OPTIONS},
        allowedHeaders = {"Authorization", "Content-Type"}
)
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioDashboardDTO>> listarUsuarios(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(dashboardService.listarUsuarios(q));
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<AlertaDashboardDTO>> listarAlertas(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(dashboardService.listarAlertas(q));
    }
}
