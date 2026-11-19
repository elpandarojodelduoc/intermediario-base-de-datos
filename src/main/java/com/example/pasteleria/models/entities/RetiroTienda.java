package com.example.pasteleria.models.entities;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Data
@Table(name = "retiro_tienda")
public class RetiroTienda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRetiroTienda;

    private String numeroOrden;

    @NotBlank(message = "Ingresa el nombre de quien retira")
    @Column(nullable = false)
    private String nombreRetira;

    @NotBlank(message = "Ingresa el correo")
    @Email(message = "Ingresa un correo valido")
    @Column(nullable = false)
    private String correo;

    @NotBlank(message = "Selecciona una sucursal")
    @Column(nullable = false)
    private String sucursal;

    @NotNull(message = "Selecciona la fecha de retiro")
    @Column(nullable = false)
    private LocalDate fecha;

    @NotNull(message = "Selecciona la hora de retiro")
    @Column(nullable = false)
    private LocalTime hora;

    @Pattern(regexp = "pendiente|listo|retirado|cancelado", message = "Estado invalido")
    @Column(nullable = false)
    private String estado = "pendiente";
}
