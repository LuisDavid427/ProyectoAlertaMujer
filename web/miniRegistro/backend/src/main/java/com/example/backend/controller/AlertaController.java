package com.example.backend.controller;

import com.example.backend.dto.AlertaRequest;
import com.example.backend.model.AlertaModel;
import com.example.backend.service.AlertaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alertas")
@CrossOrigin(origins = "*")
public class AlertaController {

    @Autowired
    private AlertaService alertaService;

    // POST: http://localhost:8080/api/alertas/emitir (Lo usará Android)
    @PostMapping("/emitir")
    public ResponseEntity<Map<String, Object>> emitirAlerta(@RequestBody AlertaRequest peticion) {
        Map<String, Object> respuesta = new HashMap<>();
        
        try {
            AlertaModel alertaGuardada = alertaService.procesarNuevaAlerta(peticion);
            
            respuesta.put("success", true);
            respuesta.put("mensaje", "Alerta SOS emitida correctamente");
            respuesta.put("id_alerta", alertaGuardada.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
            
        } catch (Exception e) {
            e.printStackTrace(); // <--- AGREGA ESTA LÍNEA AQUÍ
            respuesta.put("success", false);
            respuesta.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }

    // GET: http://localhost:8080/api/alertas/activas (Lo usará panel web)
    @GetMapping("/activas")
    public ResponseEntity<List<AlertaModel>> listarAlertasActivas() {
        List<AlertaModel> alertas = alertaService.obtenerAlertasActivas();
        return ResponseEntity.ok(alertas);
    }
}