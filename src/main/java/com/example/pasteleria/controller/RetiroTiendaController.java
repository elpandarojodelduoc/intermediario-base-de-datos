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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Retiro en tienda", description = "Datos de retiro (los crea el cliente al comprar, los administra el ADMIN)")
@RequestMapping("/retiro-tienda")
public class RetiroTiendaController {

    @Autowired
    private RetiroTiendaService retiroTiendaService;

    @Operation(summary = "Listar retiros en tienda")
    @GetMapping("")
    public List<RetiroTienda> obtenerRetiros() {
        return retiroTiendaService.obtenerRetiros();
    }

    @Operation(summary = "Buscar retiro en tienda por id")
    @GetMapping("/{id}")
    public ResponseEntity<RetiroTienda> buscarRetiroTienda(@PathVariable Integer id) {
        return retiroTiendaService.buscarRetiroTienda(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear retiro en tienda")
    @PostMapping("")
    public ResponseEntity<RetiroTienda> agregarRetiroTienda(@Valid @RequestBody RetiroTienda retiroTienda) {
        return ResponseEntity.status(HttpStatus.CREATED).body(retiroTiendaService.guardarRetiroTienda(retiroTienda));
    }

    @Operation(summary = "Actualizar retiro en tienda")
    @PutMapping("/{id}")
    public ResponseEntity<RetiroTienda> actualizarRetiroTienda(@PathVariable Integer id, @Valid @RequestBody RetiroTienda retiroTienda) {
        return retiroTiendaService.actualizarRetiroTienda(id, retiroTienda)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar retiro en tienda")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRetiroTienda(@PathVariable Integer id) {
        return retiroTiendaService.eliminarRetiroTienda(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
