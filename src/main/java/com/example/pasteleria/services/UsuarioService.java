package com.example.pasteleria.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.pasteleria.models.dto.UsuarioDto;
import com.example.pasteleria.models.dto.UsuarioRequest;
import com.example.pasteleria.models.entities.Usuario;
import com.example.pasteleria.repositories.UsuarioRepository;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;
    private final BeneficioService beneficioService;

    public UsuarioService(UsuarioRepository usuarioRepo, PasswordEncoder passwordEncoder, BeneficioService beneficioService) {
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
        this.beneficioService = beneficioService;
    }

    public List<UsuarioDto> listar() {
        return usuarioRepo.findAll().stream().map(UsuarioDto::de).toList();
    }

    public UsuarioDto buscar(Integer id) {
        return UsuarioDto.de(obtener(id));
    }

    public UsuarioDto buscarPorCorreo(String correo) {
        return UsuarioDto.de(usuarioRepo.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado")));
    }

    public UsuarioDto crear(UsuarioRequest req) {
        if (req.clave() == null || req.clave().length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 6 caracteres");
        }
        if (usuarioRepo.existsByCorreoIgnoreCase(req.correo().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese correo.");
        }
        Usuario u = new Usuario();
        aplicar(u, req);
        u.setClave(passwordEncoder.encode(req.clave()));
        return UsuarioDto.de(usuarioRepo.save(u));
    }

    public UsuarioDto actualizar(Integer id, UsuarioRequest req) {
        Usuario u = obtener(id);
        usuarioRepo.findByCorreoIgnoreCase(req.correo().trim()).ifPresent(otro -> {
            if (!otro.getIdUsuario().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese correo.");
            }
        });
        aplicar(u, req);
        if (req.clave() != null && !req.clave().isBlank()) {
            if (req.clave().length() < 6) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 6 caracteres");
            }
            u.setClave(passwordEncoder.encode(req.clave()));
        }
        return UsuarioDto.de(usuarioRepo.save(u));
    }

    public void eliminar(Integer id, String correoQuienElimina) {
        Usuario u = obtener(id);
        if (u.getCorreo().equalsIgnoreCase(correoQuienElimina)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes eliminar tu propia cuenta");
        }
        usuarioRepo.delete(u);
    }

    private Usuario obtener(Integer id) {
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private void aplicar(Usuario u, UsuarioRequest req) {
        var beneficios = beneficioService.calcular(req.nacimiento(), null, req.correo());
        u.setNombre(req.nombre().trim());
        u.setApellidos(req.apellidos().trim());
        u.setCorreo(req.correo().trim());
        u.setRol(req.rol());
        u.setTelefono(req.telefono() == null ? "" : req.telefono().trim());
        u.setNacimiento(req.nacimiento());
        u.setDescuento(req.descuento() != null ? req.descuento() : beneficios.descuento());
        u.setTortaCumple(beneficios.tortaCumple());
        u.setActivo(req.activo() == null || req.activo());
    }
}
