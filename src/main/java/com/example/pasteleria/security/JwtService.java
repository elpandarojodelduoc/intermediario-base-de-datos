package com.example.pasteleria.security;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.pasteleria.models.entities.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Service
public class JwtService {
    public static final String TIPO_ACCESS = "access";
    public static final String TIPO_REFRESH = "refresh";

    private final SecretKey clave;
    private final Duration duracionAccess;
    private final Duration duracionRefresh;

    public JwtService(
            @Value("${app.jwt.secret}") String secreto,
            @Value("${app.jwt.access-expiration-minutes:15}") long minutosAccess,
            @Value("${app.jwt.refresh-expiration-days:7}") long diasRefresh) {
        byte[] bytes = secreto.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("app.jwt.secret debe tener al menos 32 caracteres");
        }
        this.clave = Keys.hmacShaKeyFor(bytes);
        this.duracionAccess = Duration.ofMinutes(minutosAccess);
        this.duracionRefresh = Duration.ofDays(diasRefresh);
    }

    public String generarAccessToken(Usuario usuario) {
        return construir(usuario, TIPO_ACCESS, duracionAccess);
    }

    public String generarRefreshToken(Usuario usuario) {
        return construir(usuario, TIPO_REFRESH, duracionRefresh);
    }

    public long segundosAccess() {
        return duracionAccess.toSeconds();
    }

    private String construir(Usuario usuario, String tipo, Duration duracion) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(usuario.getCorreo())
                .claim("rol", usuario.getRol().name())
                .claim("tipo", tipo)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + duracion.toMillis()))
                .signWith(clave)
                .compact();
    }


    public Claims leer(String token) throws JwtException {
        return Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
    }
}
