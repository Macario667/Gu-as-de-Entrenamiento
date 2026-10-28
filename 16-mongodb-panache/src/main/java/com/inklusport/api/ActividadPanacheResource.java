package com.inklusport.api;

import com.inklusport.model.ActividadPanache;
import com.inklusport.service.ActividadPanacheService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/mongodb/panache/actividades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ActividadPanacheResource {
    
    @Inject 
    ActividadPanacheService service;

    @GET 
    public Response listar() {
        return Response.ok(service.listar()).build();
    }

    @GET 
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") String id) {
        return service.buscarPorId(id)
                .map(actividad -> Response.ok(actividad).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST 
    public Response crear(@Valid ActividadPanache actividad) {
        if (actividad == null
                || actividad.nombre == null || actividad.nombre.isBlank()
                || actividad.deporte == null || actividad.deporte.isBlank()
                || actividad.capacidad == null || actividad.capacidad < 1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Nombre, deporte y capacidad válida son obligatorios")
                    .build();
        }

        ActividadPanache creada = service.crear(actividad);

        return Response.status(Response.Status.CREATED)
                .entity(creada)
                .build();
    }

    @DELETE 
    @Path("/{id}")
    public Response eliminar(@PathParam("id") String id) {
        return service.eliminar(id)
                ? Response.noContent().build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/buscar/deporte")
    public Response buscarPorDeporte(@QueryParam("valor") String valor) {
        if (valor == null || valor.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok(service.buscarPorDeporte(valor)).build();
    }

    @GET
    @Path("/buscar/capacidad")
    public Response buscarPorCapacidad(
            @QueryParam("minima") @DefaultValue("1") int minima) {
        if (minima < 1) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok(service.buscarConCapacidadMinima(minima)).build();
    }

    @GET
    @Path("/buscar/nombre")
    public Response buscarPorNombre(@QueryParam("valor") String valor) {
        if (valor == null || valor.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok(service.buscarPorNombre(valor)).build();
    }

    @GET
    @Path("/pagina")
    public Response pagina(
            @QueryParam("numero") @DefaultValue("0") int numero,
            @QueryParam("tamano") @DefaultValue("10") int tamano) {
        try {
            return Response.ok(service.listarPagina(numero, tamano)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response actualizar(
            @PathParam("id") String id,
            ActividadPanache datos) {
        return service.actualizar(id, datos)
                ? Response.noContent().build()
                : Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET 
    @Path("/buscar")
    public Response buscar(
        @QueryParam("deporte") String deporte,
        @QueryParam("capacidadMinima") @DefaultValue("1")
        int capacidadMinima) {

        if (deporte == null || deporte.isBlank() || capacidadMinima < 1) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok(
            service.buscarActividades(deporte, capacidadMinima)
        ).build();
    }

    @GET 
    @Path("/filtrar")
    public Response filtrar(
            @QueryParam("deporte") String deporte,
            @QueryParam("capacidadMinima") Integer capacidadMinima,
            @QueryParam("capacidadMaxima") Integer capacidadMaxima,
            @QueryParam("nombre") String nombre) {

        if (capacidadMinima != null && capacidadMinima < 1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La capacidad mínima debe ser mayor que cero")
                    .build();
        }

        if (capacidadMaxima != null && capacidadMaxima < 1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La capacidad máxima debe ser mayor que cero")
                    .build();
        }

        if (capacidadMinima != null && capacidadMaxima != null
                && capacidadMinima > capacidadMaxima) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La capacidad mínima no puede superar la máxima")
                    .build();
        }

        return Response.ok(service.filtrar(
                deporte, capacidadMinima, capacidadMaxima, nombre
        )).build();
    }

    @DELETE
    @Path("/deporte/{deporte}")
    public Response eliminarPorDeporte(
            @PathParam("deporte") String deporte) {
        try {
            long eliminadas = service.eliminarPorDeporte(deporte);
            return Response.ok(
                java.util.Map.of("eliminadas", eliminadas)
            ).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build();
        }
    }

    @GET 
    @Path("/filtrar-avanzado")
    public Response filtrarAvanzado(
        @QueryParam("deporte") String deporte,
        @QueryParam("capacidadMinima") Integer capacidadMinima,
        @QueryParam("capacidadMaxima") Integer capacidadMaxima,
        @QueryParam("nombre") String nombre) {

        if (capacidadMinima != null && capacidadMinima < 1
            || capacidadMaxima != null && capacidadMaxima < 1
            || capacidadMinima != null && capacidadMaxima != null
            && capacidadMinima > capacidadMaxima) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Rango de capacidad invalido")
                    .build();
        }

        return Response.ok(service.filtrarAvanzado(
            deporte, capacidadMinima, capacidadMaxima, nombre
        )).build();
    }
}
