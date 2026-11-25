package com.example.pasteleria.models.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotBlank(message = "Los apellidos son obligatorios") String apellidos,
        @NotBlank(message = "El correo es obligatorio") @Email(message = "Ingresa un correo válido") String correo,
        @NotBlank(message = "La contraseña es obligatoria") @Size(min = 6, max = 60, message = "La contraseña debe tener entre 6 y 60 caracteres") String clave,
        @NotNull(message = "La fecha de nacimiento es obligatoria") @Past(message = "La fecha de nacimiento debe ser pasada") LocalDate nacimiento,
        String telefono,
        String codigo) {
}
