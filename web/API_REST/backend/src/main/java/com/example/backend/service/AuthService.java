package com.example.backend.service;

import com.example.backend.model.UsuarioModel;
import com.example.backend.repository.UsuarioRepository;
import com.example.backend.security.JwtUtil;
import com.example.backend.util.InterfaceUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioService usuarioService,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       EmailService emailService,
                       UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> validarMovil(String email, String passwordPlano) {
        Optional<UsuarioModel> usuarioOpt = usuarioService.buscarPorEmail(email);

        if (usuarioOpt.isEmpty()) {
            return null;
        }

        UsuarioModel usuario = usuarioOpt.get();
        if (!passwordEncoder.matches(passwordPlano, usuario.getContrasena_hash())) {
            return null;
        }

        try {
            InterfaceUtil.validarEstadoActivo(usuario);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        Map<String, Object> datos = new HashMap<>();
        datos.put("id_usuario", usuario.getId());
        datos.put("nombre", usuario.getNombre());
        datos.put("rol", obtenerRolPrincipal(usuario));
        return datos;
    }

    @Transactional(readOnly = true)
    public String autenticarAdmin(String email, String passwordPlano) {
        Optional<UsuarioModel> usuarioOpt = usuarioService.buscarPorEmail(email);

        if (usuarioOpt.isEmpty()) {
            return null;
        }

        UsuarioModel usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(passwordPlano, usuario.getContrasena_hash())) {
            return null;
        }

        if (!tieneRolAdmin(usuario)) {
            return null;
        }

        return jwtUtil.generarToken(usuario.getEmail(), "ROLE_ADMIN");
    }

    @Transactional
    public boolean procesarRecuperacionPassword(String email) {
        Optional<UsuarioModel> usuarioOpt = usuarioService.buscarPorEmail(email);

        if (usuarioOpt.isEmpty()) {
            return false;
        }

        UsuarioModel usuario = usuarioOpt.get();

        try {
            InterfaceUtil.validarEstadoActivo(usuario);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        String codigo = String.format("%06d", new java.util.Random().nextInt(999999));
        java.time.LocalDateTime expiracion = java.time.LocalDateTime.now().plusMinutes(15);
        usuarioRepository.actualizarCodigoRecuperacion(email, codigo, expiracion);

        try {
            emailService.enviarCorreoCodigo(email, codigo);
            return true;
        } catch (Exception e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
            return false;
        }
    }

    @Transactional
    public boolean actualizarPasswordConCodigo(String email, String codigo, String nuevaPassword) {
        Optional<UsuarioModel> usuarioOpt = usuarioService.buscarPorEmail(email);

        if (usuarioOpt.isEmpty()) {
            return false;
        }

        UsuarioModel usuario = usuarioOpt.get();

        if (usuario.getCodigoRecuperacion() == null || !usuario.getCodigoRecuperacion().equals(codigo)) {
            return false;
        }

        if (usuario.getExpiracionCodigo() == null || java.time.LocalDateTime.now().isAfter(usuario.getExpiracionCodigo())) {
            usuario.setCodigoRecuperacion(null);
            usuario.setExpiracionCodigo(null);
            usuarioService.guardar(usuario);
            return false;
        }

        String passwordLimpia = nuevaPassword.trim();
        usuario.setContrasena_hash(passwordLimpia);
        usuario.setCodigoRecuperacion(null);
        usuario.setExpiracionCodigo(null);
        usuario.setFechaActualizacion(java.time.LocalDateTime.now());

        usuarioService.guardar(usuario);
        return true;
    }

    public boolean verificarCodigoRecuperacion(String email, String codigo) {
        Optional<UsuarioModel> usuarioOpt = usuarioService.buscarPorEmail(email);

        if (usuarioOpt.isEmpty()) {
            return false;
        }

        UsuarioModel usuario = usuarioOpt.get();
        if (usuario.getCodigoRecuperacion() == null || !usuario.getCodigoRecuperacion().equals(codigo)) {
            return false;
        }

        return usuario.getExpiracionCodigo() != null && !java.time.LocalDateTime.now().isAfter(usuario.getExpiracionCodigo());
    }

    private boolean tieneRolAdmin(UsuarioModel usuario) {
        if (usuario.getRolesAsignados() == null || usuario.getRolesAsignados().isEmpty()) {
            return false;
        }

        return usuario.getRolesAsignados().stream()
                .filter(ur -> ur.getRol() != null)
                .anyMatch(ur -> {
                    String nombreRol = ur.getRol().getNombreRol();
                    return nombreRol != null && (
                            nombreRol.equalsIgnoreCase("ROLE_ADMIN") ||
                            nombreRol.equalsIgnoreCase("ADMIN") ||
                            nombreRol.equalsIgnoreCase("ADMINISTRADOR")
                    );
                });
    }

    private String obtenerRolPrincipal(UsuarioModel usuario) {
        if (usuario.getRolesAsignados() == null || usuario.getRolesAsignados().isEmpty()) {
            return "ROLE_USUARIO";
        }

        return usuario.getRolesAsignados().iterator().next().getRol().getNombreRol();
    }
}
