package com.example.backend.service;

import com.example.backend.dto.AlertaDashboardDTO;
import com.example.backend.dto.UsuarioDashboardDTO;
import com.example.backend.repository.AlertaRepository;
import com.example.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

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
        String filtro = (busqueda == null) ? "" : busqueda;
        List<Object[]> resultados = alertaRepo.llamarSpAlertas(filtro);
        List<AlertaDashboardDTO> lista = new ArrayList<>();

        for (Object[] fila : resultados) {
            Integer idAlerta = (Integer) fila[0];
            String nombreVictima = (String) fila[1];
            String mensaje = (String) fila[2];
            String estadoAlerta = (String) fila[3];
            
            LocalDateTime fecha = null;
            if (fila[4] != null) {
                if (fila[4] instanceof java.sql.Timestamp) {
                    fecha = ((java.sql.Timestamp) fila[4]).toLocalDateTime();
                } else if (fila[4] instanceof java.sql.Date) {
                    fecha = ((java.sql.Date) fila[4]).toLocalDate().atStartOfDay();
                }
            }

            BigDecimal latitud = fila[5] != null ? new BigDecimal(fila[5].toString()) : null;
            BigDecimal longitud = fila[6] != null ? new BigDecimal(fila[6].toString()) : null;

            lista.add(new AlertaDashboardDTO(idAlerta, nombreVictima, mensaje, estadoAlerta, fecha, latitud, longitud));
        }

        return lista;
    }
}