package com.inklusport.api;

import com.inklusport.model.ActividadMongo;
import com.inklusport.service.ActividadMongoReactiveService;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/mongodb/reactivas/actividades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ActividadMongoReactiveResource {

    @Inject 
    ActividadMongoReactiveService service;

    @GET 
    public Multi<ActividadMongo> listar() {
        return service.listar();
    }

    @GET
    @Path("/{id}")
    public Uni<Response> buscarPorId(@PathParam("id") String id) {
        return service.buscarPorId(id)
                .map(actividad -> actividad == null
                        ? Response.status(Response.Status.NOT_FOUND).build()
                        : Response.ok(actividad).build()
                );
    }

    @POST
    public Uni<Response> crear(ActividadMongo actividad) {
        if (actividad == null
                || actividad.nombre == null
                || actividad.nombre.isBlank()
                || actividad.deporte == null
                || actividad.deporte.isBlank()
                || actividad.capacidad == null
                || actividad.capacidad < 0) {
            return Uni.createFrom().item(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("Datos de actividad no válidos")
                            .build()
            );
        }

        return service.crear(actividad)
                .map(creada -> Response.status(Response.Status.CREATED)
                        .entity(creada)
                        .build());
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> eliminar(@PathParam("id") String id) {
        return service.eliminar(id)
                .map(eliminada -> eliminada
                        ? Response.noContent().build()
                        : Response.status(Response.Status.NOT_FOUND).build());
    }
}
