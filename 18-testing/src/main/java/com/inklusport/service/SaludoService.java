package com.inklusport.service;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped 
public class SaludoService {
    
    public String generarSaludo(String nombre) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                "El nombre es obligatorio"
            );
        }

        return "Hola, " + nombre;
    }
}
