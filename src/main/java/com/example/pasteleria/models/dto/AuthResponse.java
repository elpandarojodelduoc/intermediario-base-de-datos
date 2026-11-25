package com.example.pasteleria.models.dto;

public record AuthResponse(
        String token,
        String refreshToken,
        String tipo,
        long expiraEnSegundos,
        UsuarioDto usuario) {
}
