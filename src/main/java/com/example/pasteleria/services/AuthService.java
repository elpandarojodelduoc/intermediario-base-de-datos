package com.example.pasteleria.services;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.pasteleria.models.dto.AuthResponse;
import com.example.pasteleria.models.dto.LoginRequest;
import com.example.pasteleria.models.dto.RegistroRequest;
import com.example.pasteleria.models.dto.UsuarioDto;
import com.example.pasteleria.models.entities.Rol;
import com.example.pasteleria.models.entities.Usuario;
import com.example.pasteleria.repositories.UsuarioRepository;
import com.example.pasteleria.security.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final BeneficioService beneficioService;

    public AuthService(AuthenticationManager authenticationManager, UsuarioRepository usuarioRepo,
            PasswordEncoder passwordEncoder, JwtService jwtService, BeneficioService beneficioService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.beneficioService = beneficioService;
    }

    public AuthResponse login(LoginRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.correo().trim(), req.clave()));
        } catch (DisabledException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tu cuenta está desactivada.");
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos.");
        }
        Usuario usuario = usuarioRepo.findByCorreoIgnoreCase(req.correo().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos."));
        return emitir(usuario);
    }

    public AuthResponse registrar(RegistroRequest req) {
        if (usuarioRepo.existsByCorreoIgnoreCase(req.correo().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese correo.");
        }
        var beneficios = beneficioService.calcular(req.nacimiento(), req.codigo(), req.correo());
        Usuario u = new Usuario();
        u.setNombre(req.nombre().trim());
        u.setApellidos(req.apellidos().trim());
        u.setCorreo(req.correo().trim());
        u.setClave(passwordEncoder.encode(req.clave()));
        u.setRol(Rol.CLIENTE); 
        u.setNacimiento(req.nacimiento());
        u.setTelefono(req.telefono() == null ? "" : req.telefono().trim());
        u.setDescuento(beneficios.descuento());
        u.setTortaCumple(beneficios.tortaCumple());
        u.setActivo(true);
        return emitir(usuarioRepo.save(u));
    }

    public AuthResponse renovar(String refreshToken) {
        try {
            Claims claims = jwtService.leer(refreshToken);
            if (!JwtService.TIPO_REFRESH.equals(claims.get("tipo", String.class))) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El token no es de renovacion.");
            }
            Usuario usuario = usuarioRepo.findByCorreoIgnoreCase(claims.getSubject())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado."));
            if (!usuario.getActivo()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tu cuenta está desactivada.");
            }
            return emitir(usuario);
        } catch (JwtException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token invalido o expirado.");
        }
    }

    private AuthResponse emitir(Usuario usuario) {
        return new AuthResponse(
                jwtService.generarAccessToken(usuario),
                jwtService.generarRefreshToken(usuario),
                "Bearer",
                jwtService.segundosAccess(),
                UsuarioDto.de(usuario));
    }
}
