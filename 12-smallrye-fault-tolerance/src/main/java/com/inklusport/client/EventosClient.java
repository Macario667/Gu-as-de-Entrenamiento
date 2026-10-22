package com.inklusport.client;

import java.util.List;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import com.inklusport.model.Evento;

@RegisterRestClient(configKey = "eventos-api")
@Path("/api/eventos")
@Produces(MediaType.APPLICATION_JSON)
public interface EventosClient {

    @GET
    @Timeout(2000)
    @Retry(maxRetries = 3, delay = 500)
    @CircuitBreaker(
        requestVolumeThreshold = 10, 
        failureRatio = 0.5, 
        delay = 10000
    )
    @Fallback(fallbackMethod = "listarFallback")
    List<Evento> listar();

    @GET
    CompletionStage<List<Evento>> listarAsync();

    /**
     * Consulta un evento por id con resiliencia.
     * Si el servicio remoto falla, el fallback devuelve un Evento temporal.
     */
    @GET
    @Path("/{id}")
    @Timeout(2000)
    @Retry(maxRetries = 2, delay = 300)
    @CircuitBreaker(requestVolumeThreshold = 5, failureRatio = 0.5, delay = 5000)
    @Fallback(fallbackMethod = "buscarFallback")
    Evento buscar(@PathParam("id") Long id);

    // ---- Fallbacks ----
    default List<Evento> listarFallback() {
        return List.of(
                new Evento(-1L, "Evento en caché (servicio no disponible)", "fallback", 0),
                new Evento(-2L, "Evento en caché (servicio no disponible)", "fallback", 0)
        );
    }

    default Evento buscarFallback(Long id) {
        return new Evento(id, "Evento temporal (servicio no disponible)", "fallback", 0);
    }
}