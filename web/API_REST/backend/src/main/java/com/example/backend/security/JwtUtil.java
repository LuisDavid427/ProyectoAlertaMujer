package com.example.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMs;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    public String generarToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generarToken(String email) {
        return generarToken(email, "ROLE_USUARIO");
    }

    public String refrescarToken(String tokenViejo) {
        Claims claims = extraerClaimsAunqueEsteExpirado(tokenViejo);
        String email = claims.getSubject();
        String rol = (String) claims.get("role");

        if (rol == null || rol.trim().isEmpty()) {
            rol = "ROLE_USUARIO";
        }

        return generarToken(email, rol);
    }

    public String extraerEmail(String token) {
        return obtenerClaims(token).getSubject();
    }

    public String extraerRol(String token) {
        return (String) obtenerClaims(token).get("role");
    }

    public boolean validarToken(String token) {
        try {
            obtenerClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims obtenerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Claims extraerClaimsAunqueEsteExpirado(String token) {
        try {
            return obtenerClaims(token);
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
