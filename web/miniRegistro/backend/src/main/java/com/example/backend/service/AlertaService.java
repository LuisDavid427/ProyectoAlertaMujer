package com.example.backend.service;

import com.example.backend.dto.AlertaRequest;
import com.example.backend.dto.UbicacionRequest;
import com.example.backend.model.AlertaModel;
import com.example.backend.model.UbicacionModel;
import com.example.backend.model.UsuarioModel;
import com.example.backend.repository.AlertaRepository;
import com.example.backend.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.example.backend.model.EvidenciaModel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class AlertaService {

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Usamos @Transactional para que, si algo falla, no se guarde la alerta a medias
    @Transactional(rollbackFor = Exception.class)
    public AlertaModel procesarNuevaAlerta(AlertaRequest request) throws Exception {
        
        Optional<UsuarioModel> usuarioOpt = usuarioRepository.findById(Long.valueOf(request.getIdUsuario()));
        
        if (!usuarioOpt.isPresent()) {
            throw new Exception("Usuario no encontrado");
        }

        // 1. Preparamos la Alerta
        AlertaModel nuevaAlerta = new AlertaModel();
        nuevaAlerta.setUsuario(usuarioOpt.get());
        nuevaAlerta.setMensaje(request.getMensaje());
        // Se maneja en minúsculas como lo definiste en el script SQL
        nuevaAlerta.setEstadoAlerta("activa"); 

        // 2. Preparamos la primera Ubicación GPS
        UbicacionModel primeraUbicacion = new UbicacionModel();
        
        // Usamos BigDecimal.valueOf() para hacer la conversión segura
        primeraUbicacion.setLatitud(BigDecimal.valueOf(request.getLatitud()));
        primeraUbicacion.setLongitud(BigDecimal.valueOf(request.getLongitud()));
        
        // Conectamos la ubicación con la alerta
        primeraUbicacion.setAlerta(nuevaAlerta);
        

        // 3. Empacamos la ubicación dentro de la lista de la alerta
        // (Como configuraste CascadeType.ALL en el modelo, al guardar la alerta se guardará la ubicación automáticamente)
        nuevaAlerta.setUbicaciones(new ArrayList<>());
        nuevaAlerta.getUbicaciones().add(primeraUbicacion);

        // 4. Guardamos en MySQL y retornamos
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

        // Si la alerta ya fue apagada, no deberíamos seguir guardando ubicaciones
        if (alerta.getEstadoAlerta().equals("inactiva")) {
            throw new Exception("No se pueden agregar ubicaciones a una alerta inactiva.");
        }

        UbicacionModel nuevaUbicacion = new UbicacionModel();
        nuevaUbicacion.setLatitud(BigDecimal.valueOf(request.getLatitud()));
        nuevaUbicacion.setLongitud(BigDecimal.valueOf(request.getLongitud()));
        nuevaUbicacion.setAlerta(alerta);

        // Agregamos la nueva ubicación a la lista y guardamos
        alerta.getUbicaciones().add(nuevaUbicacion);
        alertaRepository.save(alerta);
    }

    // FASE 3: Apagar la alerta (Desactivar)
    public void desactivarAlerta(Integer idAlerta) throws Exception {
        Optional<AlertaModel> alertaOpt = alertaRepository.findById(idAlerta);
        
        if (!alertaOpt.isPresent()) {
            throw new Exception("La alerta especificada no existe.");
        }

        AlertaModel alerta = alertaOpt.get();
        alerta.setEstadoAlerta("inactiva");
        
        alertaRepository.save(alerta);
    }

    // FASE 4: Recibir y guardar archivos físicos (Fotos y Audios)
    @Transactional(rollbackFor = Exception.class)
    public void guardarEvidencia(Integer idAlerta, MultipartFile archivo, String tipo) throws Exception {
        
        Optional<AlertaModel> alertaOpt = alertaRepository.findById(idAlerta);
        if (!alertaOpt.isPresent()) {
            throw new Exception("La alerta especificada no existe.");
        }
        AlertaModel alerta = alertaOpt.get();

        // 1. Definir la carpeta donde se guardarán los archivos en tu PC
        // OJO: Asegúrate de usar dobles barras en Windows
        String carpetaDestino = "C://AlertaMujer//evidencias//"; 
        Path rutaDirectorio = Paths.get(carpetaDestino);

        // Si la carpeta no existe, Spring Boot la crea automáticamente
        if (!Files.exists(rutaDirectorio)) {
            Files.createDirectories(rutaDirectorio);
        }

        // 2. Generar un nombre único para que no se sobrescriban las fotos
        // UUID genera un código raro tipo "a1b2c3d4-..."
        String nombreUnico = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
        Path rutaCompleta = rutaDirectorio.resolve(nombreUnico);

        // 3. Copiar el archivo físico de la memoria RAM al Disco Duro
        Files.copy(archivo.getInputStream(), rutaCompleta);

        // 4. Preparar el registro para MySQL
        EvidenciaModel nuevaEvidencia = new EvidenciaModel();
        nuevaEvidencia.setAlerta(alerta);
        nuevaEvidencia.setUrl(rutaCompleta.toString()); // Guardamos la ruta "C://AlertaMujer/evidencias/foto.jpg"
        nuevaEvidencia.setTipo(tipo); // "foto" o "audio"

        // 5. Guardar en la base de datos
        alerta.getEvidencias().add(nuevaEvidencia);
        alertaRepository.save(alerta);
    }

    public List<AlertaModel> obtenerAlertasActivas() {
        return alertaRepository.findByEstadoAlerta("activa");
    }
}