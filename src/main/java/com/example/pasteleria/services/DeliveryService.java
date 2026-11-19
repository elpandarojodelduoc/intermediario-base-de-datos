package com.example.pasteleria.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.pasteleria.models.entities.Delivery;
import com.example.pasteleria.repositories.DeliveryRepository;

@Service
public class DeliveryService {
    @Autowired
    private DeliveryRepository deliveryRepo;

    public List<Delivery> obtenerDeliveries() {
        return deliveryRepo.findAll();
    }

    public Optional<Delivery> buscarDelivery(Integer id) {
        return deliveryRepo.findById(id);
    }

    public Delivery guardarDelivery(Delivery delivery) {
        delivery.setIdDelivery(null);
        return deliveryRepo.save(delivery);
    }

    public Optional<Delivery> actualizarDelivery(Integer id, Delivery delivery) {
        if (!deliveryRepo.existsById(id)) {
            return Optional.empty();
        }
        delivery.setIdDelivery(id);
        return Optional.of(deliveryRepo.save(delivery));
    }

    public boolean eliminarDelivery(Integer id) {
        if (!deliveryRepo.existsById(id)) {
            return false;
        }
        deliveryRepo.deleteById(id);
        return true;
    }
}
