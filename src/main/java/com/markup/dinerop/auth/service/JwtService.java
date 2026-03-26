package com.markup.dinerop.auth.service;

import com.markup.dinerop.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Genera un token JWT SOLO con identidad y rol
     */
    public String generateToken(User user) {

        var builder = Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim("userId", user.getIdUser());

        // ðŸ”‘ SOLO para cooperativas
        if (user.getRole() == com.markup.dinerop.auth.entity.Role.COOPERATIVE) {

            if (user.getCooperativaId() == null) {
                throw new IllegalStateException(
                        "Usuario COOPERATIVE sin cooperativa_id asignado"
                );
            }

            builder.claim("cooperativaId", user.getCooperativaId());
        }

        return builder
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ========================= EXTRACTION =========================

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    public Long extractUserId(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    /**
     * Valida que el token pertenezca al usuario
     */

    private boolean isTokenExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token, User user) {
        final String username = extractUsername(token);
        return username.equals(user.getEmail()) && !isTokenExpired(token);
    }

    // ========================= INTERNAL =========================

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes;

        if (isValidBase64(secret)) {
            try {
                keyBytes = Decoders.BASE64.decode(secret);
            } catch (Exception e) {
                keyBytes = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            }
        } else {
            keyBytes = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                    "jwt.secret debe tener al menos 32 bytes para HS256. Actual: " + keyBytes.length
            );
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean isValidBase64(String str) {
        return str.matches("^[A-Za-z0-9+/]*={0,2}$");
    }
}
