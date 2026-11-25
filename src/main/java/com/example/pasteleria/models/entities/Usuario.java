package com.example.pasteleria.models.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false, length = 100)
    private String clave;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol = Rol.CLIENTE;

    private String telefono;

    private LocalDate nacimiento;

    @Column(nullable = false)
    private Integer descuento = 0;

    @Column(nullable = false)
    private Boolean tortaCumple = false;

    @Column(nullable = false)
    private Boolean activo = true;
}
