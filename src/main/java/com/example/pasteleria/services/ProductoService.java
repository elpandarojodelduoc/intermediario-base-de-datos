package com.example.pasteleria.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.pasteleria.models.entities.Producto;
import com.example.pasteleria.repositories.CategoriaRepository;
import com.example.pasteleria.repositories.ProductoRepository;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepo;

    @Autowired
    private CategoriaRepository categoriaRepo;

    private void validarCategoria(Producto producto) {
        Integer idCategoria = producto.getCategoria() == null ? null : producto.getCategoria().getIdCategoria();
        if (idCategoria == null || !categoriaRepo.existsById(idCategoria)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoria indicada no existe");
        }
    }

    public List<Producto> obtenerProductos() {
        return productoRepo.findAll();
    }

    public Optional<Producto> buscarProducto(Integer id) {
        return productoRepo.findById(id);
    }

    public Producto guardarProducto(Producto producto) {
        validarCategoria(producto);
        producto.setIdProducto(null);
        return productoRepo.save(producto);
    }

    public Optional<Producto> actualizarProducto(Integer id, Producto producto) {
        if (!productoRepo.existsById(id)) {
            return Optional.empty();
        }
        validarCategoria(producto);
        producto.setIdProducto(id);
        return Optional.of(productoRepo.save(producto));
    }

    public boolean eliminarProducto(Integer id) {
        if (!productoRepo.existsById(id)) {
            return false;
        }
        productoRepo.deleteById(id);
        return true;
    }
}
