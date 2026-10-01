package com.example.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 horas

    public String generarToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generarToken(String email) {
        return generarToken(email, "ROLE_USUARIO");
    }

    // Refresca el token leyendo datos del token vencido
    public String refrescarToken(String tokenViejo) {
        Claims claims = extraerClaimsAunSiExpiring(tokenViejo);
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

    private Claims obtenerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Claims extraerClaimsAunSiExpiring(String token) {
        try {
            return obtenerClaims(token);
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
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