package com.example.pasteleria.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.pasteleria.models.entities.RetiroTienda;
import com.example.pasteleria.repositories.RetiroTiendaRepository;

@Service
public class RetiroTiendaService {
    @Autowired
    private RetiroTiendaRepository retiroTiendaRepo;

    public List<RetiroTienda> obtenerRetiros() {
        return retiroTiendaRepo.findAll();
    }

    public Optional<RetiroTienda> buscarRetiroTienda(Integer id) {
        return retiroTiendaRepo.findById(id);
    }

    public RetiroTienda guardarRetiroTienda(RetiroTienda retiroTienda) {
        retiroTienda.setIdRetiroTienda(null);
        return retiroTiendaRepo.save(retiroTienda);
    }

    public Optional<RetiroTienda> actualizarRetiroTienda(Integer id, RetiroTienda retiroTienda) {
        if (!retiroTiendaRepo.existsById(id)) {
            return Optional.empty();
        }
        retiroTienda.setIdRetiroTienda(id);
        return Optional.of(retiroTiendaRepo.save(retiroTienda));
    }

    public boolean eliminarRetiroTienda(Integer id) {
        if (!retiroTiendaRepo.existsById(id)) {
            return false;
        }
        retiroTiendaRepo.deleteById(id);
        return true;
    }
}
