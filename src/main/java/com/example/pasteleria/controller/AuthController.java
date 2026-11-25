package com.example.pasteleria.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pasteleria.models.dto.AuthResponse;
import com.example.pasteleria.models.dto.LoginRequest;
import com.example.pasteleria.models.dto.RefreshRequest;
import com.example.pasteleria.models.dto.RegistroRequest;
import com.example.pasteleria.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Login, registro y renovación de tokens JWT (endpoints públicos)")
@SecurityRequirements 
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Devuelve un access token (15 min), un refresh token (7 días) y los datos del usuario con su rol.")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar cliente", description = "Crea una cuenta con rol CLIENTE y deja la sesión iniciada. Calcula descuento y beneficios.")
    public ResponseEntity<AuthResponse> registro(@Valid @RequestBody RegistroRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(req));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar tokens", description = "Cambia un refresh token válido por un nuevo access token y un nuevo refresh token.")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest req) {
        return authService.renovar(req.refreshToken());
    }
}
