package com.example.pasteleria.models.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemOrdenRequest(
        @NotNull(message = "Falta el producto") Integer productoId,
        @NotNull(message = "Falta la cantidad") @Min(value = 1, message = "La cantidad debe ser al menos 1") Integer cantidad) {
}
