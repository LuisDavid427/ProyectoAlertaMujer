package com.example.backend.service;

import com.example.backend.model.UsuarioModel;
import com.example.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // 1. Mantenemos la funcionalidad de listar todos los usuarios
    public List<UsuarioModel> listarTodos() {
        return usuarioRepository.findAll();
    }

    // 2. Mantenemos la funcionalidad de guardar (útil para registros o actualizaciones)
    public UsuarioModel guardar(UsuarioModel usuario) {
        return usuarioRepository.save(usuario);
    }

    // 3. NUEVO: Un método limpio para que AuthService pueda buscar un usuario por su correo
    public Optional<UsuarioModel> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // Agrega este método dentro de UsuarioService
    public void actualizarTokenFcm(Integer idUsuario, String token) throws Exception {
        UsuarioModel usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new Exception("Usuario no encontrado"));
            
        usuario.setFcmToken(token);
        usuarioRepository.save(usuario);
    }
}