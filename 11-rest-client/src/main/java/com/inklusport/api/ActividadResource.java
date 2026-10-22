package com.inklusport.api;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.Context;
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
import jakarta.ws.rs.core.UriInfo;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.inklusport.model.Actividad;
import com.inklusport.model.ActividadInput;
import com.inklusport.service.ActividadService;

@Path("/api/actividades")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Actividades", description = "Gestión de actividades deportivas")
public class ActividadResource {

    @Inject
    ActividadService service;

    @GET
    @Operation(summary = "Listar actividades", description = "Permite filtrar por deporte.")
    @APIResponse(responseCode = "200", description = "Lista de actividades")
    public List<Actividad> listar(@QueryParam("deporte") String deporte) {
        return service.listar(deporte);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Consultar una actividad")
    @APIResponse(responseCode = "200", description = "Actividad encontrada")
    @APIResponse(responseCode = "404", description = "No existe la actividad")
    public Response buscar(@PathParam("id") Long id) {
        return service.buscar(id)
                .map(Response::ok)
                .map(Response.ResponseBuilder::build)
                .orElseGet(() -> noEncontrada(id));
    }

    @POST
    @Operation(summary = "Crear una actividad")
    @APIResponse(responseCode = "201", description = "Actividad creada")
    @APIResponse(responseCode = "400", description = "Datos de entrada no válidos")
    public Response crear(ActividadInput input, @Context UriInfo uriInfo) {
        Optional<String> error = mensajeValidacion(input);
        if (error.isPresent()) {
            return errorValidacion(error.get());
        }
        Actividad creada = service.crear(input);
        URI location = uriInfo.getAbsolutePathBuilder().path(creada.id.toString()).build();
        return Response.created(location).entity(creada).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar una actividad")
    @APIResponse(responseCode = "200", description = "Actividad actualizada")
    @APIResponse(responseCode = "400", description = "Datos de entrada no válidos")
    @APIResponse(responseCode = "404", description = "No existe la actividad")
    public Response actualizar(@PathParam("id") Long id, ActividadInput input) {
        Optional<String> error = mensajeValidacion(input);
        if (error.isPresent()) {
            return errorValidacion(error.get());
        }
        return service.actualizar(id, input)
                .map(Response::ok)
                .map(Response.ResponseBuilder::build)
                .orElseGet(() -> noEncontrada(id));
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar una actividad")
    @APIResponse(responseCode = "204", description = "Actividad eliminada")
    @APIResponse(responseCode = "404", description = "No existe la actividad")
    public Response eliminar(@PathParam("id") Long id) {
        return service.eliminar(id)
                ? Response.noContent().build()
                : noEncontrada(id);
    }

    private Optional<String> mensajeValidacion(ActividadInput input) {
        if (input == null || input.nombre == null || input.nombre.isBlank()
                || input.deporte == null || input.deporte.isBlank()) {
            return Optional.of("nombre y deporte son obligatorios");
        }
        if (input.capacidad == null || input.capacidad < 1 || input.capacidad > 500) {
            return Optional.of("capacidad debe estar entre 1 y 500");
        }
        return Optional.empty();
    }

    private Response errorValidacion(String mensaje) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", mensaje))
                .build();
    }

    private Response noEncontrada(Long id) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "No existe la actividad " + id))
                .build();
    }
}
