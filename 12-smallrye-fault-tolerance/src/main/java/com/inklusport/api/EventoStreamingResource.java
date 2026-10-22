package com.inklusport.api;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestMulti;

import com.inklusport.model.Evento;
import com.inklusport.service.EventoService;

import io.smallrye.mutiny.Multi;

/**
 * Streaming de datos con Multi (Mutiny).
 *
 * La respuesta se envia por partes mientras el servidor sigue produciendo,
 * en lugar de esperar a tener la lista completa.
 */
@Path("/api/stream")
public class EventoStreamingResource {

    @Inject
    EventoService service;

    /**
     * GET /api/stream/eventos
     *
     * Devuelve un array JSON con todos los eventos.
     */
    @GET
    @Path("/eventos")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Evento> eventos() {
        return Multi.createFrom().items(service.listar().toArray(new Evento[0]));
    }

    /**
     * GET /api/stream/eventos-plano
     *
     * encodeAsJsonArray(false) emite un objeto JSON por elemento,
     * en lugar de un array.
     */
    @GET
    @Path("/eventos-plano")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Evento> eventosPlano() {
        return RestMulti.fromMultiData(Multi.createFrom().items(service.listar().toArray(new Evento[0])))
                .encodeAsJsonArray(false)
                .build();
    }

    /**
     * GET /api/stream/eventos-personalizado
     *
     * Status y cabeceras propios en una respuesta en streaming.
     */
    @GET
    @Path("/eventos-personalizado")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Evento> eventosPersonalizado() {
        return RestMulti.fromMultiData(Multi.createFrom().items(service.listar().toArray(new Evento[0])))
                .status(207)
                .header("X-Stream", "true")
                .build();
    }

    /**
     * GET /api/stream/secuencia
     *
     * Stream de numeros generados con retardo entre elementos.
     */
    @GET
    @Path("/secuencia")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Integer> secuencia() {
        return Multi.createFrom().items(1, 2, 3, 4, 5)
                .onItem().transformToUni(i -> io.smallrye.mutiny.Uni.createFrom().item(i)
                        .onItem().delayIt().by(java.time.Duration.ofMillis(200)))
                .concatenate();
    }

    /**
     * GET /api/stream/nombres
     */
    @GET
    @Path("/nombres")
    @Produces(MediaType.TEXT_PLAIN)
    public Multi<String> nombres() {
        List<Evento> eventos = service.listar();
        return Multi.createFrom().items(
                eventos.stream().map(e -> e.nombre).toArray(String[]::new));
    }
}
