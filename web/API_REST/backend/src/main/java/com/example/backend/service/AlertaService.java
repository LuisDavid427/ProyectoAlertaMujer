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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AlertaService {

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FcmService fcmService;

    // FASE 1: Registrar alerta y notificar a los contactos mediante el Stored Procedure
    @Transactional(rollbackFor = Exception.class)
    public AlertaModel procesarNuevaAlerta(AlertaRequest request) throws Exception {

        if (request.getIdUsuario() == null) {
            throw new Exception("El ID de usuario es obligatorio.");
        }

        Integer idUsuario = request.getIdUsuario();
        Optional<UsuarioModel> usuarioOpt = usuarioRepository.findById(idUsuario);

        if (!usuarioOpt.isPresent()) {
            throw new Exception("Usuario emisor no encontrado.");
        }

        UsuarioModel victima = usuarioOpt.get();

        AlertaModel nuevaAlerta = new AlertaModel();
        nuevaAlerta.setUsuario(victima);
        nuevaAlerta.setMensaje(request.getMensaje());
        nuevaAlerta.setEstadoAlerta("activa");

        UbicacionModel primeraUbicacion = new UbicacionModel();
        primeraUbicacion.setLatitud(BigDecimal.valueOf(request.getLatitud()));
        primeraUbicacion.setLongitud(BigDecimal.valueOf(request.getLongitud()));
        primeraUbicacion.setAlerta(nuevaAlerta);

        nuevaAlerta.setUbicaciones(new ArrayList<>());
        nuevaAlerta.getUbicaciones().add(primeraUbicacion);

        AlertaModel alertaGuardada = alertaRepository.save(nuevaAlerta);

        // FASE 1.1: Obtención de tokens FCM vía SP y envío de notificaciones push
        try {
            List<String> correosNotificar = request.getContactosNotificar();

            if (correosNotificar != null && !correosNotificar.isEmpty()) {
                
                for (String correo : correosNotificar) {
                    if (correo == null || correo.trim().isEmpty()) continue;

                    String correoLimpio = correo.trim();
                    System.out.println("🔍 Consultando token FCM para: " + correoLimpio);

                    List<String> tokensFcm = usuarioRepository.obtenerTokensPorEmails(correoLimpio);

                    if (tokensFcm != null && !tokensFcm.isEmpty()) {
                        System.out.println("🟢 Tokens FCM encontrados (" + tokensFcm.size() + ") para: " + correoLimpio);

                        for (String tokenFcm : tokensFcm) {
                            if (tokenFcm != null && !tokenFcm.trim().isEmpty()) {
                                fcmService.enviarAlertaSegura(
                                        tokenFcm,
                                        alertaGuardada.getId(),
                                        victima.getId(),
                                        victima.getNombre(),
                                        request.getMensaje(),
                                        request.getLatitud(),
                                        request.getLongitud()
                                );
                            }
                        }
                    } else {
                        System.err.println("⚠️ El correo " + correoLimpio + " no retornó un token FCM válido.");
                    }
                }
            } else {
                System.err.println("⚠️ La petición SOS fue procesada pero la lista 'contactosNotificar' llegó vacía.");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error durante la consulta del SP o envío de FCM: " + e.getMessage());
            e.printStackTrace();
        }


        return alertaGuardada;
    }

    // FASE 2: Rastreo continuo
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

    // FASE 3: Desactivar alerta
    @Transactional
    public void desactivarAlerta(Integer idAlerta) throws Exception {
        AlertaModel alerta = alertaRepository.findById(idAlerta)
                .orElseThrow(() -> new Exception("La alerta especificada no existe."));

        alerta.setEstadoAlerta("inactiva");
        alertaRepository.save(alerta);
        alertaRepository.flush();
    }

    // FASE 4: Evidencias multimedia
    @Transactional(rollbackFor = Exception.class)
    public void guardarEvidencia(Integer idAlerta, MultipartFile archivo, String tipo) throws Exception {
        Optional<AlertaModel> alertaOpt = alertaRepository.findById(idAlerta);
        if (!alertaOpt.isPresent()) {
            throw new Exception("La alerta especificada no existe.");
        }
        AlertaModel alerta = alertaOpt.get();

        String carpetaDestino = "C://AlertaMujer//evidencias//";
        Path rutaDirectorio = Paths.get(carpetaDestino);

        if (!Files.exists(rutaDirectorio)) {
            Files.createDirectories(rutaDirectorio);
        }

        String nombreUnico = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
        Path rutaCompleta = rutaDirectorio.resolve(nombreUnico);

        Files.copy(archivo.getInputStream(), rutaCompleta);

        EvidenciaModel nuevaEvidencia = new EvidenciaModel();
        nuevaEvidencia.setAlerta(alerta);
        nuevaEvidencia.setUrl(rutaCompleta.toString());
        nuevaEvidencia.setTipo(tipo);

        alerta.getEvidencias().add(nuevaEvidencia);
        alertaRepository.save(alerta);
    }
}