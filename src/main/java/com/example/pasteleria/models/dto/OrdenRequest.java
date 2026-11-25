package com.example.pasteleria.models.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record OrdenRequest(
        @NotNull(message = "Selecciona el tipo de entrega")
        @Pattern(regexp = "retiro|delivery", message = "Tipo de entrega invalido (retiro o delivery)") String tipoEntrega,
        @NotEmpty(message = "El carrito esta vacio") List<@Valid ItemOrdenRequest> items,
        Boolean forzarRechazo) {
}
