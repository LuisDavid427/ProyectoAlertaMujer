package com.example.backend.service;

import com.example.backend.dto.AlertaDashboardDTO;
import com.example.backend.dto.UsuarioDashboardDTO;
import com.example.backend.repository.AlertaRepository;
import com.example.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired 
    private UsuarioRepository usuarioRepo;
    
    @Autowired 
    private AlertaRepository alertaRepo;

    public List<UsuarioDashboardDTO> listarUsuarios(String busqueda) {
        String filtro = (busqueda == null) ? "" : busqueda;
        List<Object[]> resultados = usuarioRepo.llamarSpUsuarios(filtro);

        return resultados.stream().map(row -> {
            // 1. Mapeo de campos básicos
            Integer id = (Integer) row[0];
            String nombre = (String) row[1];
            String email = (String) row[2];
            
            // 2. Manejo SEGURO del booleano (Evita el ClassCastException)
            Object valorActivo = row[3];
            boolean activo = false;

            if (valorActivo instanceof Boolean) {
                // Si Hibernate ya lo mandó como Boolean
                activo = (Boolean) valorActivo;
            } else if (valorActivo instanceof Number) {
                // Si Hibernate lo mandó como 1 o 0 (Number/Integer/Byte)
                activo = ((Number) valorActivo).intValue() == 1;
            }

            return new UsuarioDashboardDTO(id, nombre, email, activo);
        }).collect(Collectors.toList());
    }

    public List<AlertaDashboardDTO> listarAlertas(String busqueda) {
        List<Object[]> resultados = alertaRepo.llamarSpAlertas(busqueda);
        
        return resultados.stream().map(obj -> {
            Integer id = ((Number) obj[0]).intValue();
            String victima = (String) obj[1];
            String mensaje = (String) obj[2];
            String estado = (String) obj[3];
            
            // Manejo seguro de la fecha que viene de la base de datos
            java.time.LocalDateTime fecha = ((Timestamp) obj[4]).toLocalDateTime();

            return new AlertaDashboardDTO(id, victima, mensaje, estado, fecha);
        }).collect(Collectors.toList());
    }
}