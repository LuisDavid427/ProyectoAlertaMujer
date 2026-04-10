package com.example.backend.service;

import com.example.backend.model.UsuarioModel;
import com.example.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional; 
import java.util.List;      


@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<UsuarioModel> listarTodos() {
        return usuarioRepository.findAll();
    }

    public UsuarioModel guardar(UsuarioModel usuario) {
        return usuarioRepository.save(usuario);
    }

    public boolean esAdminValido(String email, String password) {
        Optional<Object[]> resultado = usuarioRepository.validarAccesoAdmin(email, password);
    
    // Verificamos que el Optional no esté vacío Y que el contenido no sea nulo
        if (resultado.isPresent()) {
            Object[] datos = resultado.get();
            // Si el procedimiento no encontró nada, el array suele estar vacío o el primer elemento es null
            return datos != null && datos.length > 0;
    }
    
        return false;
    }
    
    public Map<String, Object> validarUsuarioMovil(String email, String password) {
        // 1. Ahora recibimos una LISTA de filas
        List<Object[]> resultados = usuarioRepository.validarLoginUsuario(email, password);

        // 2. Verificamos si la lista tiene al menos una fila
        if (resultados != null && !resultados.isEmpty()) {
            Object[] datos = resultados.get(0); // Tomamos la primera (y única) fila
            
            // 3. Verificamos que la fila tenga las 3 columnas que esperas
            if (datos.length >= 3) {
                Map<String, Object> respuesta = new HashMap<>();
                respuesta.put("id_usuario", datos[0]); 
                respuesta.put("nombre", datos[1]);     
                respuesta.put("email", datos[2]);      
                return respuesta;
            }
        }
        
        return null; // Credenciales incorrectas o usuario no encontrado
    }
}