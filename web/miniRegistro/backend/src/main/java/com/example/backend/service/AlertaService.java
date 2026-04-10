package com.example.backend.service;

import com.example.backend.dto.AlertaRequest;
import com.example.backend.model.AlertaModel;
import com.example.backend.model.UsuarioModel;
import com.example.backend.repository.AlertaRepository;
import com.example.backend.repository.UsuarioRepository;

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
        Optional<UsuarioModel> usuarioOpt = usuarioRepository.findById(Long.valueOf(request.getIdUsuario()));
        
        if (!usuarioOpt.isPresent()) {
            throw new Exception("Usuario no encontrado");
        }

        AlertaModel nuevaAlerta = new AlertaModel();
        nuevaAlerta.setUsuario(usuarioOpt.get());
        nuevaAlerta.setMensaje(request.getMensaje());
        nuevaAlerta.setUrlImagen(request.getUrlImagen());
        nuevaAlerta.setUrlAudio(request.getUrlAudio());
        
        nuevaAlerta.setFecha(LocalDateTime.now());
        nuevaAlerta.setEstadoAlerta("activa");

        return alertaRepository.save(nuevaAlerta);
    }

    public List<AlertaModel> obtenerAlertasActivas() {
        return alertaRepository.findByEstadoAlerta("activa");
    }
}