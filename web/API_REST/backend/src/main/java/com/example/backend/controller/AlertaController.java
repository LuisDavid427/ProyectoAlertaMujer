package com.example.backend.controller;

import com.example.backend.dto.AlertaRequest;
import com.example.backend.dto.UbicacionRequest;
import com.example.backend.model.AlertaModel;
import com.example.backend.service.AlertaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alertas")
@CrossOrigin(origins = "*")
public class AlertaController {

    @Autowired
    private AlertaService alertaService;

    // POST: http://localhost:8080/api/alertas/emitir
    @PostMapping("/emitir")
    public ResponseEntity<Map<String, Object>> emitirAlerta(@RequestBody AlertaRequest peticion) {
        Map<String, Object> respuesta = new HashMap<>();
        System.out.println(">>> Entró al controlador emitirAlerta");
        try {
            AlertaModel alertaGuardada = alertaService.procesarNuevaAlerta(peticion);
            
            respuesta.put("success", true);
            respuesta.put("mensaje", "Alerta SOS emitida correctamente");
            respuesta.put("id_alerta", alertaGuardada.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
            
        } catch (Exception e) {
            e.printStackTrace();
            respuesta.put("success", false);
            respuesta.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }

    // POST: http://localhost:8080/api/alertas/{id}/ubicacion
    @PostMapping("/{id}/ubicacion")
    public ResponseEntity<Map<String, Object>> actualizarUbicacion(
            @PathVariable("id") Integer idAlerta, 
            @RequestBody UbicacionRequest peticion) {
        
        Map<String, Object> respuesta = new HashMap<>();
        
        try {
            alertaService.agregarUbicacionContinua(idAlerta, peticion);
            respuesta.put("success", true);
            respuesta.put("mensaje", "Coordenada actualizada");
            return ResponseEntity.ok(respuesta);
            
        } catch (Exception e) {
            respuesta.put("success", false);
            respuesta.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }

    // PUT: http://localhost:8080/api/alertas/{id}/desactivar
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Map<String, Object>> apagarEmergencia(
            @PathVariable("id") Integer idAlerta,
            @RequestBody(required = false) Map<String, List<String>> body) {
        
        Map<String, Object> respuesta = new HashMap<>();
        
        try {
            // Extraemos la lista de correos a notificar si viene en el body
            List<String> correosNotificar = body != null ? body.get("contactosNotificar") : null;
            
            alertaService.desactivarAlerta(idAlerta, correosNotificar);
            
            respuesta.put("success", true);
            respuesta.put("mensaje", "Alerta desactivada correctamente");
            return ResponseEntity.ok(respuesta);
            
        } catch (Exception e) {
            respuesta.put("success", false);
            respuesta.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }

// POST: http://localhost:8080/api/alertas/{id}/evidencias
    @PostMapping("/{id}/evidencias")
    public ResponseEntity<Map<String, Object>> subirEvidencia(
            @PathVariable("id") Integer idAlerta,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("tipo") String tipo,
            @RequestParam(value = "contactosNotificar", required = false) String contactosCsv) {
        
        Map<String, Object> respuesta = new HashMap<>();
        
        try {
            // Convertimos el string separado por comas en una Lista de Java
            List<String> contactosNotificar = null;
            if (contactosCsv != null && !contactosCsv.trim().isEmpty()) {
                contactosNotificar = java.util.Arrays.asList(contactosCsv.split(","));
            }

            System.out.println(">>> Contactos procesados para evidencia: " + contactosNotificar);
            
            alertaService.guardarEvidencia(idAlerta, archivo, tipo, contactosNotificar);
            
            respuesta.put("success", true);
            respuesta.put("mensaje", "Evidencia guardada exitosamente");
            return ResponseEntity.ok(respuesta);
            
        } catch (Exception e) {
            respuesta.put("success", false);
            respuesta.put("error", "Error al guardar el archivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }
}