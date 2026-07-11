package com.example.backend.service;

import com.example.backend.model.UsuarioModel;
import com.example.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Listar todos los usuarios
    public List<UsuarioModel> listarTodos() {
        return usuarioRepository.findAll();
    }

    // Registrar usuario
    public UsuarioModel guardar(UsuarioModel usuario) {

        // Cifrar la contraseña antes de guardarla
        usuario.setContrasena_hash(
                passwordEncoder.encode(usuario.getContrasena_hash())
        );

        return usuarioRepository.save(usuario);
    }

    // Buscar por correo
    public Optional<UsuarioModel> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // Actualizar el token de Firebase
    public void actualizarTokenFcm(Integer idUsuario, String token) throws Exception {

        UsuarioModel usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        usuario.setFcmToken(token);

        usuarioRepository.save(usuario);
    }
}