package com.inklusport.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest 
class SaludoServiceTest {

    @Inject 
    SaludoService service;

    @Test 
    void debeGenerarSaludoPersonalizado() {
        assertEquals(
            "Hola, Ana",
            service.generarSaludo("Ana")
        );
    }

    @Test 
    void debeRechazarUnNombreVacío() {
        var excepcion = org.junit.jupiter.api.Assertions
            .assertThrows(
                IllegalArgumentException.class,
                () -> service.generarSaludo(" ")
            );
        
        assertEquals("El nombre es obligatorio", excepcion.getMessage());
    }
}
