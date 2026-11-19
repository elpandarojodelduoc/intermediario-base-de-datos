package com.example.pasteleria.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pasteleria.models.entities.RetiroTienda;

@Repository
public interface RetiroTiendaRepository extends JpaRepository<RetiroTienda, Integer> {

}
