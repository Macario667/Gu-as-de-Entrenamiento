package com.inklusport.api;

import java.time.Duration;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;

import io.smallrye.mutiny.Multi;

/**
 * Server-Sent Events (SSE): el servidor envia eventos de forma continua
 * sobre una unica conexion HTTP.
 *
 * El cliente:
 *   curl.exe -N http://localhost:8080/api/sse/reloj
 *
 * El objeto Sse es inyectable: aporta el constructor de eventos SSE.
 */
@Path("/api/sse")
public class EventoSseResource {

    @Inject
    Sse sse;

    /**
     * GET /api/sse/reloj
     *
     * Envia un evento cada 2 segundos con un contador.
     */
    @GET
    @Path("/reloj")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Multi<OutboundSseEvent> reloj() {
        return Multi.createFrom()
                .ticks().every(Duration.ofSeconds(2))
                .onItem().transform(tick -> sse.newEventBuilder()
                        .name("tick")
                        .id(String.valueOf(tick))
                        .data("Tick " + tick)
                        .build());
    }

    /**
     * GET /api/sse/eventos
     *
     * Envia un evento por cada elemento de la secuencia.
     */
    @GET
    @Path("/eventos")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Multi<OutboundSseEvent> eventos() {
        return Multi.createFrom()
                .items(1, 2, 3)
                .onItem().transform(i -> sse.newEventBuilder()
                        .name("evento")
                        .data("Evento " + i)
                        .build());
    }
}
