package com.example.pasteleria.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.pasteleria.models.dto.ItemOrdenRequest;
import com.example.pasteleria.models.dto.OrdenRequest;
import com.example.pasteleria.models.dto.OrdenResponse;
import com.example.pasteleria.models.entities.DetalleOrden;
import com.example.pasteleria.models.entities.Orden;
import com.example.pasteleria.models.entities.Producto;
import com.example.pasteleria.models.entities.Usuario;
import com.example.pasteleria.repositories.OrdenRepository;
import com.example.pasteleria.repositories.ProductoRepository;
import com.example.pasteleria.repositories.UsuarioRepository;

@Service
public class OrdenService {
    private final OrdenRepository ordenRepo;
    private final ProductoRepository productoRepo;
    private final UsuarioRepository usuarioRepo;

    public OrdenService(OrdenRepository ordenRepo, ProductoRepository productoRepo, UsuarioRepository usuarioRepo) {
        this.ordenRepo = ordenRepo;
        this.productoRepo = productoRepo;
        this.usuarioRepo = usuarioRepo;
    }

    static int precioFinal(Producto p) {
        return (int) Math.round(p.getPrecio() * (100 - p.getOferta()) / 100.0);
    }

    @Transactional
    public OrdenResponse crear(String correo, OrdenRequest req) {
        Usuario usuario = usuarioRepo.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        Map<Integer, Integer> cantidades = new LinkedHashMap<>();
        for (ItemOrdenRequest item : req.items()) {
            cantidades.merge(item.productoId(), item.cantidad(), Integer::sum);
        }

        Orden orden = new Orden();
        orden.setFecha(LocalDateTime.now());
        orden.setUsuario(usuario);
        orden.setTipoEntrega(req.tipoEntrega());

        int subtotal = 0;
        for (var entrada : cantidades.entrySet()) {
            Producto p = productoRepo.findById(entrada.getKey())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "El producto " + entrada.getKey() + " no existe"));
            if (p.getStock() < entrada.getValue()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Stock insuficiente para " + p.getNombre() + " (disponible: " + p.getStock() + ")");
            }
            int precio = precioFinal(p);
            DetalleOrden d = new DetalleOrden();
            d.setOrden(orden);
            d.setProductoId(p.getIdProducto());
            d.setNombre(p.getNombre());
            d.setImagen(p.getImagen());
            d.setPrecio(precio);
            d.setCantidad(entrada.getValue());
            d.setSubtotal(precio * entrada.getValue());
            orden.getItems().add(d);
            subtotal += d.getSubtotal();
        }

        int descuento = (int) Math.round(subtotal * usuario.getDescuento() / 100.0);
        orden.setSubtotal(subtotal);
        orden.setDescuento(descuento);
        orden.setTotal(subtotal - descuento);

        boolean rechazado = Boolean.TRUE.equals(req.forzarRechazo()); 
        orden.setEstado(rechazado ? Orden.FALLIDA : Orden.PAGADA);

        orden = ordenRepo.save(orden);
        orden.setNumero(generarNumero(orden));
        if (!rechazado) {
            descontarStock(orden);
        }
        return OrdenResponse.de(ordenRepo.save(orden));
    }

    @Transactional
    public OrdenResponse reintentarPago(Integer id, String correo, boolean esAdmin) {
        Orden orden = obtenerAutorizada(id, correo, esAdmin);
        if (!Orden.FALLIDA.equals(orden.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se puede reintentar el pago de ordenes fallidas");
        }
        for (DetalleOrden d : orden.getItems()) {
            Producto p = productoRepo.findById(d.getProductoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "El producto " + d.getNombre() + " ya no existe"));
            if (p.getStock() < d.getCantidad()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente para " + p.getNombre());
            }
        }
        orden.setEstado(Orden.PAGADA);
        descontarStock(orden);
        return OrdenResponse.de(ordenRepo.save(orden));
    }

    @Transactional(readOnly = true)
    public List<OrdenResponse> listarTodas() {
        return ordenRepo.findAllByOrderByFechaDesc().stream().map(OrdenResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public List<OrdenResponse> listarDe(String correo) {
        return ordenRepo.findByUsuarioCorreoIgnoreCaseOrderByFechaDesc(correo).stream().map(OrdenResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public OrdenResponse buscar(Integer id, String correo, boolean esAdmin) {
        return OrdenResponse.de(obtenerAutorizada(id, correo, esAdmin));
    }

    private Orden obtenerAutorizada(Integer id, String correo, boolean esAdmin) {
        Orden orden = ordenRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));
        if (!esAdmin && !orden.getUsuario().getCorreo().equalsIgnoreCase(correo)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes acceder a esta orden");
        }
        return orden;
    }

    private void descontarStock(Orden orden) {
        for (DetalleOrden d : orden.getItems()) {
            productoRepo.findById(d.getProductoId()).ifPresent(p -> {
                p.setStock(Math.max(p.getStock() - d.getCantidad(), 0));
                productoRepo.save(p);
            });
        }
    }

    private String generarNumero(Orden orden) {
        return orden.getFecha().format(DateTimeFormatter.BASIC_ISO_DATE) + "-" + String.format("%04d", orden.getIdOrden());
    }
}
