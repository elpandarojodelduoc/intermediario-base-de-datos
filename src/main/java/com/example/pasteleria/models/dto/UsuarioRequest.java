package com.example.pasteleria.models.dto;

import java.time.LocalDate;

import com.example.pasteleria.models.entities.Rol;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotBlank(message = "Los apellidos son obligatorios") String apellidos,
        @NotBlank(message = "El correo es obligatorio") @Email(message = "Ingresa un correo válido") String correo,
        String clave,
        @NotNull(message = "El rol es obligatorio") Rol rol,
        String telefono,
        LocalDate nacimiento,
        @Min(0) @Max(100) Integer descuento,
        Boolean activo) {
}
