package com.inklusport.api;

import java.util.List;

import com.inklusport.persistence.entity.Disciplina;

import jakarta.transaction.Transactional;

// import jakarta.ws.rs.Consumes;
// import jakarta.ws.rs.GET;
// import jakarta.ws.rs.NotFoundException;
// import jakarta.ws.rs.POST;
// import jakarta.ws.rs.PUT;
// import jakarta.ws.rs.DELETE;
// import jakarta.ws.rs.Path;
// import jakarta.ws.rs.PathParam;
// import jakarta.ws.rs.Produces;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/disciplinas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DisciplinaResource {
    
    @GET 
    public List<Disciplina> listar() {
        return Disciplina.listAll();
    }

    @GET 
    @Path("/{id}")
    public Disciplina buscarPorId(@PathParam("id") Long id) {
        Disciplina disciplina = Disciplina.findById(id);

        if (disciplina == null) {
            throw new NotFoundException("Disciplina no encontrada");
        }

        return disciplina;
    }

    @POST 
    @Transactional 
    public Response crear(Disciplina disciplina) {
        disciplina.id = null;
        disciplina.persist();

        return Response.status(Response.Status.CREATED)
                .entity(disciplina)
                .build();
    }

    @GET 
    @Path("/contar")
    public long contar() {
        return Disciplina.count();
    }

    @GET 
    @Path("/adaptadas")
    public List<Disciplina> listarAdaptadas() {
        return Disciplina.list("adaptada", true);
    }

    @GET
    @Path("/buscar")
    public List<Disciplina> buscarPorNombre(
            @QueryParam("nombre") String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new BadRequestException(
                    "Debes indicar el nombre de la disciplina");
        }

        return Disciplina.list(
                "lower(nombre) like lower(?1)",
                "%" + nombre.trim() + "%"
        );
    }

    @PUT 
    @Path("/{id}")
    public Disciplina actualizar(
            @PathParam("id") Long id,
            Disciplina datos) {
        Disciplina existente = Disciplina.findById(id);

        if (existente == null) {
            throw new NotFoundException(
                    "Disciplina no encontrada");
        }

        existente.nombre = datos.nombre;
        existente.descripcion = datos.descripcion;
        existente.adaptada = datos.adaptada;

        return existente;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response eliminar(@PathParam("id") Long id) {
        boolean eliminado = Disciplina.deleteById(id);

        if (!eliminado) {
            throw new NotFoundException(
                    "Disciplina no encontrada");
        }

        return Response.noContent().build();
    }
}
