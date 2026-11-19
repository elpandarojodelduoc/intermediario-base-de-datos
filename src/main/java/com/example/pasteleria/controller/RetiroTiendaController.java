package com.example.pasteleria.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pasteleria.models.entities.RetiroTienda;
import com.example.pasteleria.services.RetiroTiendaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/retiro-tienda")
public class RetiroTiendaController {

    @Autowired
    private RetiroTiendaService retiroTiendaService;

    @GetMapping("")
    public List<RetiroTienda> obtenerRetiros() {
        return retiroTiendaService.obtenerRetiros();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RetiroTienda> buscarRetiroTienda(@PathVariable Integer id) {
        return retiroTiendaService.buscarRetiroTienda(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<RetiroTienda> agregarRetiroTienda(@Valid @RequestBody RetiroTienda retiroTienda) {
        return ResponseEntity.status(HttpStatus.CREATED).body(retiroTiendaService.guardarRetiroTienda(retiroTienda));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RetiroTienda> actualizarRetiroTienda(@PathVariable Integer id, @Valid @RequestBody RetiroTienda retiroTienda) {
        return retiroTiendaService.actualizarRetiroTienda(id, retiroTienda)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRetiroTienda(@PathVariable Integer id) {
        return retiroTiendaService.eliminarRetiroTienda(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
