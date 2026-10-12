package com.example.backend.service;

import com.example.backend.model.UsuarioModel;
import com.example.backend.security.JwtUtil;
import com.example.backend.util.InterfaceUtil;
import com.example.backend.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- AGREGAR ESTA IMPORTACIÓN
import org.threeten.bp.LocalDateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // LOGIN MÓVIL (Android)
@Transactional(readOnly = true)
    public Map<String, Object> validarMovil(String email, String passwordPlano) {
        Optional<UsuarioModel> userOpt = usuarioService.buscarPorEmail(email);
        
        if (userOpt.isPresent() && passwordEncoder.matches(passwordPlano, userOpt.get().getContrasena_hash())) {
            UsuarioModel usuario = userOpt.get();
            
            try {
                InterfaceUtil.validarEstadoActivo(usuario);
            } catch (Exception e) {
                // Si la cuenta está bloqueada, puedes relanzarla como una excepción en runtime 
                // o manejarla según lo requiera tu controlador de autenticación.
                throw new RuntimeException(e.getMessage());
            }

            Map<String, Object> datos = new HashMap<>();
            datos.put("id_usuario", usuario.getId());
            datos.put("nombre", usuario.getNombre());
            
            // Extraemos el rol (si tiene colección de roles) o asignamos ROLE_USUARIO
            String rol = "ROLE_USUARIO";
            if (usuario.getRolesAsignados() != null && !usuario.getRolesAsignados().isEmpty()) {
                rol = usuario.getRolesAsignados().iterator().next().getRol().getNombreRol();
            }
            
            datos.put("rol", rol);
            return datos;
        }
        return null;
    }
    // AÑADIR @Transactional PARA MANTENER LA SESIÓN JPA ABIERTA Y CARGAR ROLES
    @Transactional(readOnly = true)
    public String autenticarAdmin(String email, String passwordPlano) {
        Optional<UsuarioModel> usuarioOpt = usuarioService.buscarPorEmail(email);

        if (usuarioOpt.isEmpty()) {
            return null;
        }

        UsuarioModel usuario = usuarioOpt.get();

        // 1. Validar contraseña
        if (!passwordEncoder.matches(passwordPlano, usuario.getContrasena_hash())) {
            return null;
        }

        // 2. Control nulo de seguridad por si no tiene roles asignados
        if (usuario.getRolesAsignados() == null || usuario.getRolesAsignados().isEmpty()) {
            return null;
        }

        // 3. BLINDAJE DE ROL: Verificar si realmente tiene asignado el rol ADMIN
        boolean esAdmin = usuario.getRolesAsignados().stream()
                .filter(ur -> ur.getRol() != null)
                .anyMatch(ur -> {
                    String nombreRol = ur.getRol().getNombreRol(); // Ver si en RolModel se llama getNombreRol() o getNombre()
                    return nombreRol != null && (
                        nombreRol.equalsIgnoreCase("ROLE_ADMIN") || 
                        nombreRol.equalsIgnoreCase("ADMIN") ||
                        nombreRol.equalsIgnoreCase("ADMINISTRADOR")
                    );
                });

        if (!esAdmin) {
            return null; 
        }

        // 4. Generar token pasando el correo Y el rol explícito
        return jwtUtil.generarToken(usuario.getEmail(), "ROLE_ADMIN");
    }

// 5. PASO 1: Procesar solicitud de recuperación (Generar código)
    @Transactional
    public boolean procesarRecuperacionPassword(String email) {
        Optional<UsuarioModel> userOpt = usuarioService.buscarPorEmail(email);
        
        if (userOpt.isPresent()) {
            UsuarioModel usuario = userOpt.get();
            
            try {

                InterfaceUtil.validarEstadoActivo(usuario);

            } catch (Exception e) {

                throw new RuntimeException(e.getMessage());

            }
            String codigo = String.format("%06d", new java.util.Random().nextInt(999999));
            java.time.LocalDateTime expiracion = java.time.LocalDateTime.now().plusMinutes(15);
            
            // 🛑 Actualizamos ÚNICAMENTE el código y la expiración sin tocar la contraseña
            usuarioRepository.actualizarCodigoRecuperacion(email, codigo, expiracion);
            
            // Envío del correo real
            try {
                emailService.enviarCorreoCodigo(email, codigo);
            } catch (Exception e) {
                System.err.println("Error al enviar el correo: " + e.getMessage());
                return false;
            }
            
            return true;
        }
        
        return false;
    }


    // 6. PASO 2 Y 3: Validar código y actualizar contraseña delegando el cifrado a UsuarioService
    @Transactional
    public boolean actualizarPasswordConCodigo(String email, String codigo, String nuevaPassword) {
        Optional<UsuarioModel> userOpt = usuarioService.buscarPorEmail(email);
        
        if (userOpt.isPresent()) {
            UsuarioModel usuario = userOpt.get();
            
            // Validar que el código coincida y que NO sea nulo
            if (usuario.getCodigoRecuperacion() != null && usuario.getCodigoRecuperacion().equals(codigo)) {
                
                // VALIDACIÓN DE EXPIRACIÓN
                if (usuario.getExpiracionCodigo() == null || java.time.LocalDateTime.now().isAfter(usuario.getExpiracionCodigo())) {
                    usuario.setCodigoRecuperacion(null);
                    usuario.setExpiracionCodigo(null);
                    usuarioService.guardar(usuario);
                    return false; 
                }

                // Limpiamos espacios y pasamos la contraseña en texto plano; UsuarioService.guardar se encarga de cifrarla
                String passwordLimpia = nuevaPassword.trim();
                usuario.setContrasena_hash(passwordLimpia);
                
                // Limpiar el código para que no se pueda reutilizar
                usuario.setCodigoRecuperacion(null);
                usuario.setExpiracionCodigo(null);
                usuario.setFechaActualizacion(java.time.LocalDateTime.now());
                
                // Al llamar a guardar, UsuarioService detectará que no es un hash y la cifrará correctamente
                usuarioService.guardar(usuario);
                return true;
            }
        }
        
        return false;
    }

    public boolean verificarCodigoRecuperacion(String email, String codigo) {
    Optional<UsuarioModel> userOpt = usuarioService.buscarPorEmail(email);
    if (userOpt.isPresent()) {
        UsuarioModel usuario = userOpt.get();
        // Validar que el código coincida y no sea nulo
        if (usuario.getCodigoRecuperacion() != null && usuario.getCodigoRecuperacion().equals(codigo)) {
            // Validar que no haya expirado
            if (usuario.getExpiracionCodigo() != null && java.time.LocalDateTime.now().isAfter(usuario.getExpiracionCodigo())) {
                return false; 
            }
            return true;
        }
    }
    return false;
}
}