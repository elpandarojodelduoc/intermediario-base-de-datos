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

import com.example.pasteleria.models.entities.Categoria;
import com.example.pasteleria.services.CategoriaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categoria")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("")
    public List<Categoria> obtenerCategorias() {
        return categoriaService.obtenerCategorias();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscarCategoria(@PathVariable Integer id) {
        return categoriaService.buscarCategoria(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("")
    public ResponseEntity<Categoria> agregarCategoria(@Valid @RequestBody Categoria categoria) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.guardarCategoria(categoria));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizarCategoria(@PathVariable Integer id, @Valid @RequestBody Categoria categoria) {
        return categoriaService.actualizarCategoria(id, categoria)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Integer id) {
        return categoriaService.eliminarCategoria(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
