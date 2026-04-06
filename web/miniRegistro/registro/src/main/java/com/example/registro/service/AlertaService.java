package com.example.registro.service;

import com.example.registro.dto.AlertaRequest;
import com.example.registro.model.AlertaModel;
import com.example.registro.model.UsuarioModel;
import com.example.registro.repository.AlertaRepository;
import com.example.registro.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AlertaService {

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public AlertaModel procesarNuevaAlerta(AlertaRequest request) throws Exception {
        // 1. Verificamos que el usuario exista
        Optional<UsuarioModel> usuarioOpt = usuarioRepository.findById(Long.valueOf(request.getIdUsuario()));
        
        if (!usuarioOpt.isPresent()) {
            throw new Exception("Usuario no encontrado");
        }

        // 2. Construimos la entidad para la base de datos
        AlertaModel nuevaAlerta = new AlertaModel();
        nuevaAlerta.setUsuario(usuarioOpt.get());
        nuevaAlerta.setMensaje(request.getMensaje());
        nuevaAlerta.setUrlImagen(request.getUrlImagen());
        nuevaAlerta.setUrlAudio(request.getUrlAudio());
        
        // 3. Valores por defecto (Seguridad del lado del servidor)
        nuevaAlerta.setFecha(LocalDateTime.now());
        nuevaAlerta.setEstadoAlerta("activa");

        // 4. Guardamos
        return alertaRepository.save(nuevaAlerta);
    }

    public List<AlertaModel> obtenerAlertasActivas() {
        // Buscamos las alertas que estén en estado "activa" (en minúsculas como en tu DB)
        return alertaRepository.findByEstadoAlerta("activa");
    }
}