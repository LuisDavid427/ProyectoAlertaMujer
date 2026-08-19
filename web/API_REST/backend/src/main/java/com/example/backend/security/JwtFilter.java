package com.example.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        String token = authHeader.substring(7);

        try {
            if (jwtUtil.validarToken(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                String email = jwtUtil.extraerEmail(token);
                String rol = jwtUtil.extraerRol(token);

                // Si por alguna razón el token viejo no traía rol, le asignamos ROLE_USUARIO por defecto
                if (rol == null || rol.trim().isEmpty()) {
                    rol = "ROLE_USUARIO";
                } else if (!rol.startsWith("ROLE_")) {
                    rol = "ROLE_" + rol;
                }

                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(rol));

                UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(email, null, authorities);
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                System.out.println("--> EMAIL AUTENTICADO: " + email);
                System.out.println("--> ROL ASIGNADO: " + rol);
            }
        } catch (Exception e) {
            System.err.println("--> ERROR VALIDANDO TOKEN JWT: " + e.getMessage());
        }
    }
    
    chain.doFilter(request, response);
}
}