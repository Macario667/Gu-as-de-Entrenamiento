package com.inklusport.api;

import java.net.URI;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.Cache;
import org.jboss.resteasy.reactive.NoCache;
import org.jboss.resteasy.reactive.ResponseHeader;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;

import com.inklusport.model.Evento;
import com.inklusport.model.EventoInput;
import com.inklusport.service.EventoService;

/**
 * API REST con el motor reactivo (Quarkus REST).
 *
 * Diferencias frente a RESTEasy Classic (guía 03):
 *
 *   - La respuesta se declara con RestResponse en lugar de construir
 *     un jakarta.ws.rs.core.Response a mano.
 *   - Los parámetros se inyectan con @RestQuery / @RestPath / @RestHeader /
 *     @RestCookie / @RestMatrix, que además hacen la conversion de tipo.
 *   - Se puede devolver Uni<...> o Multi<...> sin bloquear el hilo.
 */
@Path("/api/eventos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EventoResource {

    @Inject
    EventoService service;

    /**
     * GET /api/eventos?offset=0&limit=20
     *
     * @Cache(maxAge = 60) añade Cache-Control: max-age=60 a la respuesta.
     */
    @GET
    @Cache(maxAge = 60)
    public RestResponse<List<Evento>> listar(
            @RestQuery @DefaultValue("0") int offset,
            @RestQuery @DefaultValue("20") int limit) {

        List<Evento> todos = service.listar();
        List<Evento> pagina = todos.stream()
                .skip(offset)
                .limit(limit)
                .toList();

        return RestResponse.ResponseBuilder
                .ok(pagina)
                .header("X-Total-Count", todos.size())
                .build();
    }

    /**
     * GET /api/eventos?deporte=baloncesto
     */
    @GET
    @Path("/buscar")
    public RestResponse<List<Evento>> buscar(@RestQuery String deporte) {
        return RestResponse.ok(service.buscarPorDeporte(deporte));
    }

    /**
     * GET /api/eventos/{id}
     *
     * Si no existe, la excepcion EventoNoEncontradoException la transforma
     * el ExceptionMapper en un 404 con cuerpo JSON.
     */
    @GET
    @Path("/{id}")
    public RestResponse<Evento> buscarPorId(@RestPath Long id) {
        return RestResponse.ok(service.buscarPorId(id)
                .orElseThrow(() -> new EventoNoEncontradoException(id)));
    }

    /**
     * GET /api/eventos/{id}/detalle
     *
     * @NoCache desactiva la caché de la respuesta.
     */
    @GET
    @Path("/{id}/detalle")
    @NoCache
    public Evento detalle(@RestPath Long id) {
        return service.buscarPorId(id)
                .orElseThrow(() -> new EventoNoEncontradoException(id));
    }

    /**
     * POST /api/eventos
     *
     * @ResponseStatus(201) fija el codigo de respuesta y @ResponseHeader
     * anade una cabecera propia.
     */
    @POST
    @ResponseStatus(201)
    @ResponseHeader(name = "X-Created-By", value = "EventoResource")
    public Evento crear(EventoInput input) {
        return service.crear(input);
    }

    /**
     * PUT /api/eventos/{id}
     */
    @PUT
    @Path("/{id}")
    public RestResponse<Evento> actualizar(@RestPath Long id, EventoInput input) {
        return RestResponse.ok(service.actualizar(id, input)
                .orElseThrow(() -> new EventoNoEncontradoException(id)));
    }

    /**
     * DELETE /api/eventos/{id}
     */
    @DELETE
    @Path("/{id}")
    public RestResponse<Void> eliminar(@RestPath Long id) {
        if (service.eliminar(id)) {
            return RestResponse.noContent();
        }
        return RestResponse.notFound();
    }

    /**
     * POST /api/eventos/{id}/acciones/crear
     *
     * 303 See Other: redireccion al recurso recien creado.
     */
    @POST
    @Path("/{id}/acciones/crear")
    public RestResponse<Void> crearConRedirect(@RestPath Long id, EventoInput input) {
        Evento nuevo = service.crear(input);
        return RestResponse.seeOther(URI.create("/api/eventos/" + nuevo.id));
    }
}
