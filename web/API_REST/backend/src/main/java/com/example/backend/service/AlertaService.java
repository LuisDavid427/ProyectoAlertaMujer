package com.example.backend.service;

import com.example.backend.dto.AlertaRequest;
import com.example.backend.dto.UbicacionRequest;
import com.example.backend.model.AlertaModel;
import com.example.backend.model.EvidenciaModel;
import com.example.backend.model.UbicacionModel;
import com.example.backend.model.UsuarioModel;
import com.example.backend.repository.AlertaRepository;
import com.example.backend.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
public class AlertaService {

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


    

// FASE 1: Crear la alerta inicial
    @Transactional(rollbackFor = Exception.class)
    public AlertaModel procesarNuevaAlerta(AlertaRequest request) throws Exception {
        
        // 1. Validamos que el ID de usuario no venga nulo en la petición
        if (request.getIdUsuario() == null) {
            throw new Exception("El ID de usuario es obligatorio y llegó nulo desde la aplicación.");
        }

        // 2. Obtenemos directamente el ID sin conversiones innecesarias
        Integer idUsuario = request.getIdUsuario();
        
        Optional<UsuarioModel> usuarioOpt = usuarioRepository.findById(idUsuario);
        
        if (!usuarioOpt.isPresent()) {
            throw new Exception("Usuario no encontrado");
        }

        // 3. Preparamos la Alerta
        AlertaModel nuevaAlerta = new AlertaModel();
        nuevaAlerta.setUsuario(usuarioOpt.get());
        nuevaAlerta.setMensaje(request.getMensaje());
        nuevaAlerta.setEstadoAlerta("activa"); 

        // 4. Preparamos la primera Ubicación GPS
        UbicacionModel primeraUbicacion = new UbicacionModel();
        primeraUbicacion.setLatitud(BigDecimal.valueOf(request.getLatitud()));
        primeraUbicacion.setLongitud(BigDecimal.valueOf(request.getLongitud()));
        primeraUbicacion.setAlerta(nuevaAlerta);

        // 5. Empacamos la ubicación dentro de la lista de la alerta
        nuevaAlerta.setUbicaciones(new ArrayList<>());
        nuevaAlerta.getUbicaciones().add(primeraUbicacion);

        // 6. Guardamos en MySQL y retornamos
        return alertaRepository.save(nuevaAlerta);
    }
    // FASE 2: Agregar nueva ubicación al rastreo (Cada 5 segundos)
    @Transactional(rollbackFor = Exception.class)
    public void agregarUbicacionContinua(Integer idAlerta, UbicacionRequest request) throws Exception {
        
        Optional<AlertaModel> alertaOpt = alertaRepository.findById(idAlerta);
        
        if (!alertaOpt.isPresent()) {
            throw new Exception("La alerta especificada no existe.");
        }

        AlertaModel alerta = alertaOpt.get();

        if ("inactiva".equals(alerta.getEstadoAlerta())) {
            throw new Exception("No se pueden agregar ubicaciones a una alerta inactiva.");
        }

        UbicacionModel nuevaUbicacion = new UbicacionModel();
        nuevaUbicacion.setLatitud(BigDecimal.valueOf(request.getLatitud()));
        nuevaUbicacion.setLongitud(BigDecimal.valueOf(request.getLongitud()));
        nuevaUbicacion.setAlerta(alerta);

        alerta.getUbicaciones().add(nuevaUbicacion);
        alertaRepository.save(alerta);
    }

    @Transactional 
    public void desactivarAlerta(Integer idAlerta) throws Exception {
        AlertaModel alerta = alertaRepository.findById(idAlerta)
                .orElseThrow(() -> new Exception("La alerta especificada no existe."));
        
        alerta.setEstadoAlerta("inactiva");
        alertaRepository.save(alerta);
        alertaRepository.flush(); 
    }
    // FASE 4: Recibir y guardar archivos físicos (Fotos y Audios)
    @Transactional(rollbackFor = Exception.class)
    public void guardarEvidencia(Integer idAlerta, MultipartFile archivo, String tipo) throws Exception {
        
        Optional<AlertaModel> alertaOpt = alertaRepository.findById(idAlerta);
        if (!alertaOpt.isPresent()) {
            throw new Exception("La alerta especificada no existe.");
        }
        AlertaModel alerta = alertaOpt.get();

        // Directorio local para guardar los archivos
        String carpetaDestino = "C://AlertaMujer//evidencias//"; 
        Path rutaDirectorio = Paths.get(carpetaDestino);

        if (!Files.exists(rutaDirectorio)) {
            Files.createDirectories(rutaDirectorio);
        }

        String nombreUnico = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
        Path rutaCompleta = rutaDirectorio.resolve(nombreUnico);

        // Copiar el archivo al disco duro
        Files.copy(archivo.getInputStream(), rutaCompleta);

        // Preparar el registro para la BD
        EvidenciaModel nuevaEvidencia = new EvidenciaModel();
        nuevaEvidencia.setAlerta(alerta);
        nuevaEvidencia.setUrl(rutaCompleta.toString());
        nuevaEvidencia.setTipo(tipo);

        alerta.getEvidencias().add(nuevaEvidencia);
        alertaRepository.save(alerta);
    }
}