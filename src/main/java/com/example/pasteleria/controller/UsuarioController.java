package com.example.pasteleria.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pasteleria.models.dto.UsuarioDto;
import com.example.pasteleria.models.dto.UsuarioRequest;
import com.example.pasteleria.services.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuario")
@Tag(name = "Usuarios", description = "Administración de usuarios (solo ADMIN, excepto /usuario/me)")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/me")
    @Operation(summary = "Datos del usuario autenticado", description = "Sirve para restaurar la sesión en el frontend al recargar la página.")
    public UsuarioDto me(Authentication authentication) {
        return usuarioService.buscarPorCorreo(authentication.getName());
    }

    @GetMapping("")
    @Operation(summary = "Listar usuarios (ADMIN)")
    public List<UsuarioDto> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por id (ADMIN)")
    public UsuarioDto buscar(@PathVariable Integer id) {
        return usuarioService.buscar(id);
    }

    @PostMapping("")
    @Operation(summary = "Crear usuario (ADMIN)")
    public ResponseEntity<UsuarioDto> crear(@Valid @RequestBody UsuarioRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario (ADMIN)", description = "Si no se envía clave, se mantiene la actual.")
    public UsuarioDto actualizar(@PathVariable Integer id, @Valid @RequestBody UsuarioRequest req) {
        return usuarioService.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario (ADMIN)")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id, Authentication authentication) {
        usuarioService.eliminar(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
