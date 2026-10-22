package com.inklusport.api;

import java.net.URI;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.inklusport.model.Evento;
import com.inklusport.model.EventoInput;
import com.inklusport.service.EventoService;

/**
 * Recurso REST clásico.
 *
 * En RESTEasy Classic la respuesta se construye siempre con
 * jakarta.ws.rs.core.Response: se elige el status, las cabeceras y el cuerpo
 * de forma explícita. En la guía 05 (RESTEasy Reactive) se verá la variante
 * declarativa con RestResponse.
 */
@Path("/api/eventos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EventoResource {

    @Inject
    EventoService service;

    /**
     * GET /api/eventos?deporte=baloncesto
     */
    @GET
    public Response listar(@QueryParam("deporte") String deporte) {
        List<Evento> eventos = (deporte == null || deporte.isBlank())
                ? service.listar()
                : service.buscarPorDeporte(deporte);

        return Response.ok(eventos)
                .header("X-Total-Count", eventos.size())
                .build();
    }

    /**
     * GET /api/eventos/{id}
     */
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        Evento evento = service.buscarPorId(id)
                .orElseThrow(() -> new EventoNoEncontradoException(id));

        return Response.ok(evento).build();
    }

    /**
     * POST /api/eventos
     */
    @POST
    public Response crear(EventoInput input) {
        Evento creado = service.crear(input);

        return Response.created(URI.create("/api/eventos/" + creado.id))
                .entity(creado)
                .build();
    }

    /**
     * PUT /api/eventos/{id}
     */
    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") Long id, EventoInput input) {
        Evento actualizado = service.actualizar(id, input)
                .orElseThrow(() -> new EventoNoEncontradoException(id));

        return Response.ok(actualizado).build();
    }

    /**
     * DELETE /api/eventos/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") Long id) {
        if (!service.eliminar(id)) {
            throw new EventoNoEncontradoException(id);
        }

        return Response.noContent().build();
    }
}
