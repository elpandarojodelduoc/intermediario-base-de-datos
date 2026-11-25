package com.example.pasteleria.models.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.pasteleria.models.entities.DetalleOrden;
import com.example.pasteleria.models.entities.Orden;

public record OrdenResponse(
        Integer idOrden,
        String numero,
        LocalDateTime fecha,
        Integer usuarioId,
        String clienteNombre,
        String clienteCorreo,
        String tipoEntrega,
        List<Item> items,
        Integer subtotal,
        Integer descuento,
        Integer total,
        String estado) {

    public record Item(Integer productoId, String nombre, String imagen, Integer precio, Integer cantidad, Integer subtotal) {
        static Item de(DetalleOrden d) {
            return new Item(d.getProductoId(), d.getNombre(), d.getImagen(), d.getPrecio(), d.getCantidad(), d.getSubtotal());
        }
    }

    public static OrdenResponse de(Orden o) {
        return new OrdenResponse(o.getIdOrden(), o.getNumero(), o.getFecha(),
                o.getUsuario().getIdUsuario(),
                o.getUsuario().getNombre() + " " + o.getUsuario().getApellidos(),
                o.getUsuario().getCorreo(), o.getTipoEntrega(),
                o.getItems().stream().map(Item::de).toList(),
                o.getSubtotal(), o.getDescuento(), o.getTotal(), o.getEstado());
    }
}
