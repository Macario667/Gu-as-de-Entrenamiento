package com.inklusport.resilience;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;
import org.eclipse.microprofile.faulttolerance.exceptions.BulkheadException;
import org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.faulttolerance.api.RateLimitException;

@QuarkusTest 
class ResilienciaServiceTest {
    
    @Inject 
    ResilienciaService resilienciaService;

    @Test 
    void debeRecuperarseDespuesDeDosFallos() {
        resilienciaService.reiniciarIntentos();

        String resultado =
            resilienciaService.consultarConReintentos();

        assertEquals(
            "Servicio recuperado en el intento 3", 
            resultado
        );
    }

    @Test 
    void debeDevolverRespuestaAlternativaCuandoFalla() {
        String resultado =
            resilienciaService.consultarConFallback();
        
        assertEquals(
            "Respuesta alternativa: eventos temporalmente limitados", 
            resultado
        );
    }

    @Test 
    void debeAplicarTimeoutAUnaOperacionLenta() {
        assertThrows(
            TimeoutException.class,
            () -> resilienciaService.consultarOperacionLenta()
        );
    }

    @Test 
    void debeAbrirElCircuitoTrasFallosRepetidos() {
        for (int i = 0; i < 4; i++) {
            assertThrows(
                IllegalStateException.class,
                () -> resilienciaService.consultarServicioProtegido()
            );
        }

        assertThrows(
            CircuitBreakerOpenException.class,
            () -> resilienciaService.consultarServicioProtegido()
        );
    }

    @Test 
    void debeRechazarUnaSegundaEjecucionConcurrente() throws Exception{
        CountDownLatch inicio = new CountDownLatch(1);

        CompletableFuture<String> primera = 
            CompletableFuture.supplyAsync(
                () -> resilienciaService.consultarOperacionLimitada(inicio)
            );
        
        inicio.await();

        assertThrows(
            BulkheadException.class,
            () -> resilienciaService.consultarOperacionLimitada(
                new CountDownLatch(1)
            )
        );

        assertEquals(
            "Operación ejecutada correctamente",
            primera.get()
        );
    }

    @Test
    void debeAceptarDosSolicitudesYRechazarLaTercera() {
        assertEquals(
            "Solicitud aceptada",
            resilienciaService.consultarConLimiteDeSolicitudes()
        );

        assertEquals(
            "Solicitud aceptada",
            resilienciaService.consultarConLimiteDeSolicitudes()
        );

        assertThrows(
            RateLimitException.class,
            () -> resilienciaService.consultarConLimiteDeSolicitudes()
        );
    }
}
