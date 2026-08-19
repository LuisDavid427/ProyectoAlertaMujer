package com.example.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "AlertaMujer2026_ClaveSecretaSuperSegura!";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    // Sobrecarga para incluir el Rol en las Claims del token
    public String generarToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role) // <--- Agregamos el rol
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generarToken(String email) {
        return generarToken(email, "ROLE_USUARIO");
    }

    public String extraerEmail(String token) {
        return obtenerClaims(token).getSubject();
    }

    // Nuevo método para extraer el Rol
    public String extraerRol(String token) {
        return (String) obtenerClaims(token).get("role");
    }

    private Claims obtenerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}