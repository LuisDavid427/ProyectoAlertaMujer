package com.example.backend.util;

import com.example.backend.model.UsuarioModel;

public class InterfaceUtil {

    public static void validarEstadoActivo(UsuarioModel usuario) throws Exception {
        if (usuario != null && Boolean.FALSE.equals(usuario.getEstadoUsuario())) {
            throw new Exception("Acceso denegado: Tu cuenta se encuentra bloqueada por la administración.");
        }
    }
}