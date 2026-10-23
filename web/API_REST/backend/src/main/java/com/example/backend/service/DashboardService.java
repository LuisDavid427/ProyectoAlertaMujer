package com.example.backend.service;

import com.example.backend.dto.AlertaDashboardDTO;
import com.example.backend.dto.UsuarioDashboardDTO;
import com.example.backend.repository.AlertaRepository;
import com.example.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final UsuarioRepository usuarioRepo;
    private final AlertaRepository alertaRepo;

    public DashboardService(UsuarioRepository usuarioRepo, AlertaRepository alertaRepo) {
        this.usuarioRepo = usuarioRepo;
        this.alertaRepo = alertaRepo;
    }

    public List<UsuarioDashboardDTO> listarUsuarios(String busqueda) {
        String filtro = busqueda == null ? "" : busqueda;
        List<Object[]> resultados = usuarioRepo.llamarSpUsuarios(filtro);

        List<UsuarioDashboardDTO> usuarios = new ArrayList<>();
        for (Object[] row : resultados) {
            Integer id = obtenerInteger(row, 0);
            String nombre = obtenerString(row, 1);
            String email = obtenerString(row, 2);
            boolean activo = obtenerBoolean(row, 3);

            usuarios.add(new UsuarioDashboardDTO(id, nombre, email, activo));
        }

        return usuarios;
    }

    public List<AlertaDashboardDTO> listarAlertas(String busqueda) {
        String filtro = busqueda == null ? "" : busqueda;
        List<Object[]> resultados = alertaRepo.llamarSpAlertas(filtro);

        List<AlertaDashboardDTO> alertas = new ArrayList<>();
        for (Object[] fila : resultados) {
            Integer idAlerta = obtenerInteger(fila, 0);
            String nombreVictima = obtenerString(fila, 1);
            String mensaje = obtenerString(fila, 2);
            String estadoAlerta = obtenerString(fila, 3);
            LocalDateTime fecha = convertirFecha(fila[4]);
            BigDecimal latitud = convertirBigDecimal(fila[5]);
            BigDecimal longitud = convertirBigDecimal(fila[6]);

            alertas.add(new AlertaDashboardDTO(idAlerta, nombreVictima, mensaje, estadoAlerta, fecha, latitud, longitud));
        }

        return alertas;
    }

    private Integer obtenerInteger(Object[] fila, int index) {
        Object valor = fila[index];
        if (valor == null) return null;
        if (valor instanceof Number numero) {
            return numero.intValue();
        }
        return Integer.valueOf(valor.toString());
    }

    private String obtenerString(Object[] fila, int index) {
        Object valor = fila[index];
        return valor == null ? null : valor.toString();
    }

    private boolean obtenerBoolean(Object[] fila, int index) {
        Object valor = fila[index];
        if (valor instanceof Boolean bool) {
            return bool;
        }
        if (valor instanceof Number numero) {
            return numero.intValue() == 1;
        }
        if (valor != null) {
            return Boolean.parseBoolean(valor.toString());
        }
        return false;
    }

    private LocalDateTime convertirFecha(Object valor) {
        if (valor == null) return null;
        if (valor instanceof java.sql.Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }
        if (valor instanceof java.sql.Date fecha) {
            return fecha.toLocalDate().atStartOfDay();
        }
        return null;
    }

    private BigDecimal convertirBigDecimal(Object valor) {
        if (valor == null) return null;
        return new BigDecimal(valor.toString());
    }
}
