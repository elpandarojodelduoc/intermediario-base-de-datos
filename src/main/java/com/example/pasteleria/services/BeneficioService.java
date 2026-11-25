package com.example.pasteleria.services;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.stereotype.Service;

@Service
public class BeneficioService {
    public static final String CODIGO_PROMOCIONAL = "FELICES50";
    public static final String DOMINIO_DUOC = "@duocuc.cl";

    public record Beneficios(int descuento, boolean tortaCumple) {
    }

    public Beneficios calcular(LocalDate nacimiento, String codigo, String correo) {
        int descuento = 0;
        if (nacimiento != null && Period.between(nacimiento, LocalDate.now()).getYears() >= 50) {
            descuento = 50;
        } else if (codigo != null && CODIGO_PROMOCIONAL.equalsIgnoreCase(codigo.trim())) {
            descuento = 10;
        }
        boolean tortaCumple = correo != null && correo.trim().toLowerCase().endsWith(DOMINIO_DUOC);
        return new Beneficios(descuento, tortaCumple);
    }
}
