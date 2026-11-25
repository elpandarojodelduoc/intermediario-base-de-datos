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

import com.example.pasteleria.models.entities.Delivery;
import com.example.pasteleria.services.DeliveryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Delivery", description = "Datos de despacho (los crea el cliente al comprar, los administra el ADMIN)")
@RequestMapping("/delivery")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @Operation(summary = "Listar deliveries")
    @GetMapping("")
    public List<Delivery> obtenerDeliveries() {
        return deliveryService.obtenerDeliveries();
    }

    @Operation(summary = "Buscar delivery por id")
    @GetMapping("/{id}")
    public ResponseEntity<Delivery> buscarDelivery(@PathVariable Integer id) {
        return deliveryService.buscarDelivery(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear delivery")
    @PostMapping("")
    public ResponseEntity<Delivery> agregarDelivery(@Valid @RequestBody Delivery delivery) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.guardarDelivery(delivery));
    }

    @Operation(summary = "Actualizar delivery")
    @PutMapping("/{id}")
    public ResponseEntity<Delivery> actualizarDelivery(@PathVariable Integer id, @Valid @RequestBody Delivery delivery) {
        return deliveryService.actualizarDelivery(id, delivery)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar delivery")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDelivery(@PathVariable Integer id) {
        return deliveryService.eliminarDelivery(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
