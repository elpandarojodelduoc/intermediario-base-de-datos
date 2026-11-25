package com.example.pasteleria.models.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "orden")
public class Orden {
    public static final String PAGADA = "pagada";
    public static final String FALLIDA = "fallida";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idOrden;

    private String numero;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String tipoEntrega;

    @Column(nullable = false)
    private Integer subtotal;

    @Column(nullable = false)
    private Integer descuento;

    @Column(nullable = false)
    private Integer total;

    @Column(nullable = false)
    private String estado;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleOrden> items = new ArrayList<>();
}
