package com.example.pasteleria.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pasteleria.models.entities.Orden;

public interface OrdenRepository extends JpaRepository<Orden, Integer> {
    List<Orden> findByUsuarioCorreoIgnoreCaseOrderByFechaDesc(String correo);

    List<Orden> findAllByOrderByFechaDesc();
}
