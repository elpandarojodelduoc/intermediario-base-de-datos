package com.example.pasteleria.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "detalle_orden")
public class DetalleOrden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDetalle;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_orden", nullable = false)
    private Orden orden;

    @Column(nullable = false)
    private Integer productoId;

    @Column(nullable = false)
    private String nombre;

    private String imagen;

    @Column(nullable = false)
    private Integer precio;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Integer subtotal;
}
