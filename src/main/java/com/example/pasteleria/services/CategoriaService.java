package com.example.pasteleria.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.pasteleria.models.entities.Categoria;
import com.example.pasteleria.repositories.CategoriaRepository;

@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepo;

    public List<Categoria> obtenerCategorias() {
        return categoriaRepo.findAll();
    }

    public Optional<Categoria> buscarCategoria(Integer id) {
        return categoriaRepo.findById(id);
    }

    public Categoria guardarCategoria(Categoria categoria) {
        categoria.setIdCategoria(null);
        return categoriaRepo.save(categoria);
    }

    public Optional<Categoria> actualizarCategoria(Integer id, Categoria categoria) {
        if (!categoriaRepo.existsById(id)) {
            return Optional.empty();
        }
        categoria.setIdCategoria(id);
        return Optional.of(categoriaRepo.save(categoria));
    }

    public boolean eliminarCategoria(Integer id) {
        if (!categoriaRepo.existsById(id)) {
            return false;
        }
        categoriaRepo.deleteById(id);
        return true;
    }
}
