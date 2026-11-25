package com.example.pasteleria.models.dto;

import java.time.LocalDate;

import com.example.pasteleria.models.entities.Usuario;

public record UsuarioDto(
        Integer id,
        String nombre,
        String apellidos,
        String correo,
        String rol,
        String telefono,
        LocalDate nacimiento,
        Integer descuento,
        Boolean tortaCumple,
        Boolean activo) {

    public static UsuarioDto de(Usuario u) {
        return new UsuarioDto(u.getIdUsuario(), u.getNombre(), u.getApellidos(), u.getCorreo(),
                u.getRol().name().toLowerCase(), u.getTelefono(), u.getNacimiento(),
                u.getDescuento(), u.getTortaCumple(), u.getActivo());
    }
}
