package com.example.pasteleria.models.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Data
@Table(name = "delivery")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDelivery;

    private String numeroOrden;

    @NotBlank(message = "Ingresa el nombre del destinatario")
    @Column(nullable = false)
    private String nombreDestinatario;

    @NotBlank(message = "Ingresa un telefono de contacto")
    @Pattern(regexp = "^\\+?[0-9\\s]{8,15}$", message = "Ingresa un telefono valido")
    @Column(nullable = false)
    private String telefono;

    @NotBlank(message = "Ingresa la calle y numero")
    @Column(nullable = false)
    private String calle;

    private String departamento;

    @NotBlank(message = "Selecciona una region")
    @Column(nullable = false)
    private String region;

    @NotBlank(message = "Selecciona una comuna")
    @Column(nullable = false)
    private String comuna;

    @Column(length = 500)
    private String indicaciones;

    @NotNull(message = "Selecciona la fecha de entrega")
    @Column(nullable = false)
    private LocalDate fecha;

    @Pattern(regexp = "pendiente|en_camino|entregado|cancelado", message = "Estado invalido")
    @Column(nullable = false)
    private String estado = "pendiente";
}
