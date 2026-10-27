package com.inklusport.api;

import com.inklusport.model.ActividadMongo;
import com.inklusport.service.ActividadMongoService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/mongodb/actividades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ActividadMongoResource {
    
    @Inject 
    ActividadMongoService service;

    @GET 
    public Response listar() {
        return Response.ok(service.listar()).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") String id) {
        ActividadMongo actividad = service.buscarPorId(id);

        if (actividad == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(actividad).build();
    }

    @POST 
    public Response crear(ActividadMongo actividad) {
        if (actividad == null
                || actividad.nombre == null
                || actividad.nombre.isBlank()
                || actividad.deporte == null
                || actividad.deporte.isBlank()
                || actividad.capacidad == null
                || actividad.capacidad < 1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Nombre, deporte y capacidad válida son obligatorios")
                    .build();
        }

        actividad.id = null;

        return Response.status(Response.Status.CREATED)
                .entity(service.crear(actividad))
                .build();
    }

    @PUT 
    @Path("/{id}")
    public Response actualizar(
            @PathParam("id") String id,
            ActividadMongo actividad) {

        if (actividad == null
                || actividad.nombre == null
                || actividad.nombre.isBlank()
                || actividad.deporte == null
                || actividad.deporte.isBlank()
                || actividad.capacidad == null
                || actividad.capacidad < 1) {

            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ActividadMongo actualizada = service.actualizar(id, actividad);

        if (actualizada == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(actualizada).build();
    }

    @DELETE 
    @Path("/{id}")
    public Response eliminar(@PathParam("id") String id) {
        if (!service.eliminar(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.noContent().build();
    }

    @GET 
    @Path("/buscar")
    public Response buscar(
        @QueryParam("nombre") String nombre,
        @QueryParam("deporte") String deporte,
        @QueryParam("capacidadMinima") Integer capacidadMinima) {

        if (capacidadMinima != null && capacidadMinima < 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La capacidad mínima no puede ser negativa")
                    .build();
        }

        return Response.ok(
                service.buscarConFiltros(nombre, deporte, capacidadMinima)
        ).build();
    }
}
