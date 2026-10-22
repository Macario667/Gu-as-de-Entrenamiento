package com.inklusport.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestPath;

import com.inklusport.model.Evento;
import com.inklusport.service.EventoService;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;

/**
 * Respuestas reactivas con Uni.
 *
 * Uni representa un valor asincrono: el hilo de la peticion no se bloquea
 * mientras se espera el resultado.
 *
 * @Blocking es lo contrario: marca el endpoint como bloqueante, para el caso
 * de que la operacion underlying si lo sea (por ejemplo, JDBC).
 */
@Path("/api/reactivo")
@Produces(MediaType.APPLICATION_JSON)
public class BusquedaResource {

    @Inject
    EventoService service;

    /**
     * GET /api/reactivo/eventos/{id}
     */
    @GET
    @Path("/eventos/{id}")
    public Uni<Evento> buscarAsync(@RestPath Long id) {
        return service.buscarAsync(id);
    }

    /**
     * GET /api/reactivo/eventos/{id}/bloqueante
     */
    @GET
    @Path("/eventos/{id}/bloqueante")
    @Blocking
    public Uni<Evento> buscarBloqueante(@RestPath Long id) {
        return Uni.createFrom().item(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return service.buscarPorId(id).orElseThrow();
        });
    }
}
