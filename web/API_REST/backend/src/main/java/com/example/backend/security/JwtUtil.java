package com.example.backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Clave maestra (debe tener al menos 32 caracteres para HS256)
    private static final String SECRET_KEY = "AlertaMujer2026_ClaveSecretaSuperSegura!";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // Tiempo de validez del token: 24 horas (en milisegundos)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    public String generarToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extraerEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // Token expirado, alterado o inválido
        }
    }
}