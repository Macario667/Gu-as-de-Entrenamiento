package com.inklusport.resilience;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import io.smallrye.faulttolerance.api.RateLimit;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped 
public class ResilienciaService {

    private final AtomicInteger intentos = new AtomicInteger();

    public void reiniciarIntentos() {
        intentos.set(0);
    }

    /**
     * falla dos veces y tiene éxito en el tercer intento.
     * @Retry(maxRetries = 2) significa dos reintentos adicionales, 
     * no dos ejecuciones totales. El método puede ejecutarse tres 
     * veces.
     */
    @Retry(maxRetries = 2, delay = 100)
    public String consultarConReintentos() {
        int intentoActual = intentos.incrementAndGet();

        if (intentoActual < 3) {
            throw new IllegalStateException(
                "Fallo simulado en el intento " + intentoActual
            );
        }
        return "Servicio recuperado en el intento " + intentoActual;
    }

    /**
     * falla y devuelve una respuesta alternativa.
     */
    @Fallback(fallbackMethod = "respuestaAlternativa")
    public String consultarConFallback() {
        throw new IllegalStateException(
            "El servicio externo no está disponible"
        );
    }
    
    public String respuestaAlternativa() {
        return "Respuesta alternativa: eventos temporalmente limitados";
    } 

    @Timeout(value = 200, unit = ChronoUnit.MILLIS)
    public String consultarOperacionLenta() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                "La operación fue interrumpida", e
            );
        }

        return "Operación completada";
    }

    @CircuitBreaker(
        requestVolumeThreshold = 4,
        failureRatio = 0.5,
        delay = 5000
    )
    public String consultarServicioProtegido() {
        throw new IllegalStateException(
            "Fallo simulado del servicio protegido"
        );
    }

    @Bulkhead(value = 1, waitingTaskQueue = 1)
    public String consultarOperacionLimitada(CountDownLatch inicio) {
        inicio.countDown();

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Operación interrumpida", e);
        }

        return "Operación ejecutada correctamente";
    }

    /**
     * @Bulkhead(1): solo una ejecución simultánea. 
     * Si otra intenta entrar mientras la primera sigue 
     * ejecutándose, se rechaza.
     * @RateLimit(value = 2, window = 10, ...): permite como máximo 
     * dos llamadas durante una ventana de diez segundos. La tercera 
     * puede ser rechazada con RateLimitException
     * @return "Solicitud aceptada"
     */
    @RateLimit(value = 2, window = 10, windowUnit = ChronoUnit.SECONDS)
    public String consultarConLimiteDeSolicitudes() {
        return "Solicitud aceptada";
    }
}
