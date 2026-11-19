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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/delivery")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @GetMapping("")
    public List<Delivery> obtenerDeliveries() {
        return deliveryService.obtenerDeliveries();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Delivery> buscarDelivery(@PathVariable Integer id) {
        return deliveryService.buscarDelivery(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<Delivery> agregarDelivery(@Valid @RequestBody Delivery delivery) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.guardarDelivery(delivery));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Delivery> actualizarDelivery(@PathVariable Integer id, @Valid @RequestBody Delivery delivery) {
        return deliveryService.actualizarDelivery(id, delivery)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDelivery(@PathVariable Integer id) {
        return deliveryService.eliminarDelivery(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
