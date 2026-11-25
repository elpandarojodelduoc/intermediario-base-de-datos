package com.example.pasteleria.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pasteleria.models.dto.OrdenRequest;
import com.example.pasteleria.models.dto.OrdenResponse;
import com.example.pasteleria.services.OrdenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/orden")
@Tag(name = "Órdenes", description = "Compras: el backend calcula precios, descuento, total y stock")
public class OrdenController {
    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    private static boolean esAdmin(Authentication a) {
        return a.getAuthorities().stream().anyMatch(g -> g.getAuthority().equals("ROLE_ADMIN"));
    }

    @PostMapping("")
    @Operation(summary = "Crear orden (usuario autenticado)", description = "Envía solo productoId y cantidad. Responde 409 si no hay stock suficiente.")
    public ResponseEntity<OrdenResponse> crear(@Valid @RequestBody OrdenRequest req, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenService.crear(authentication.getName(), req));
    }

    @GetMapping("")
    @Operation(summary = "Listar todas las órdenes (ADMIN)")
    public List<OrdenResponse> listarTodas() {
        return ordenService.listarTodas();
    }

    @GetMapping("/mis-ordenes")
    @Operation(summary = "Listar mis órdenes (usuario autenticado)")
    public List<OrdenResponse> misOrdenes(Authentication authentication) {
        return ordenService.listarDe(authentication.getName());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver una orden", description = "Un cliente solo puede ver sus propias órdenes; un ADMIN puede ver cualquiera.")
    public OrdenResponse buscar(@PathVariable Integer id, Authentication authentication) {
        return ordenService.buscar(id, authentication.getName(), esAdmin(authentication));
    }

    @PostMapping("/{id}/reintentar-pago")
    @Operation(summary = "Reintentar pago de una orden fallida")
    public OrdenResponse reintentarPago(@PathVariable Integer id, Authentication authentication) {
        return ordenService.reintentarPago(id, authentication.getName(), esAdmin(authentication));
    }
}
