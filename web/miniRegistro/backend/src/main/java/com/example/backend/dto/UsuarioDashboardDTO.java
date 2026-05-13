package com.example.backend.dto;

// Usando la estructura moderna que discutimos
public record UsuarioDashboardDTO(
    Integer idUsuario, 
    String nombre, 
    String email, 
    Boolean activo // Para saber si la cuenta está vigente
) {}