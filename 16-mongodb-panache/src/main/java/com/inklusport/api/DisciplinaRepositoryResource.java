package com.inklusport.api;

import java.util.List;
import java.util.Map;

import com.inklusport.model.DisciplinaDto;
import com.inklusport.persistence.entity.Disciplina;
import com.inklusport.persistence.repository.DisciplinaRepository;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/repositorio/disciplinas")
@Produces(MediaType.APPLICATION_JSON)
public class DisciplinaRepositoryResource {
    
    @Inject 
    DisciplinaRepository repository;

    @GET 
    public List<Disciplina> listar() {
        return repository.listAll();
    }

    @GET 
    @Path("/adaptadas")
    public List<Disciplina> listarAdaptadas() {
        return repository.listarAdaptadas();
    }

    @GET 
    @Path("/buscar")
    public List<Disciplina> buscar(
            @QueryParam("nombre") String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new BadRequestException(
                    "Debes indicar el nombre");
        }

        return repository.buscarPorNombre(nombre);
    }

    @GET 
    @Path("/ordenadas")
    public List<Disciplina> listarOrdenadas() {
        return repository.listarOrdenadasPorNombre();
    }

    @GET 
    @Path("/ordenadas/descendentemente")
    public List<Disciplina> listarOrdenadasDesc() {
        return repository.listarOrdenadasPorNombreDesc();
    }

    @GET 
    @Path("/pagina")
    public Response listarPagina(
        @QueryParam("numero") @DefaultValue("0") int numero,
        @QueryParam("tamaño") @DefaultValue("5") int tamaño) {

        if (numero < 0 || tamaño < 1 || tamaño > 100) {
            throw new BadRequestException(
                    "Número de página o tamaño inválido");
        }

        var consulta = repository.consultarPaginadas(numero, tamaño);

        return Response.ok(java.util.Map.of(
                "contenido", consulta.list(),
                "pagina", numero,
                "tamaño", tamaño,
                "totalRegistros", consulta.count(),
                "totalPaginas", consulta.pageCount()
        )).build();
    }

    /**
     * Endpoint: filtra disciplinas por nombre y estado de adaptación.
     * Ejemplo: GET /api/repositorio/disciplinas/buscar/adaptada?nombre=Nataci%C3%B3n&adaptada=true
     */
    @GET 
    @Path("/buscar/adaptada")
    public List<Disciplina> buscar(
            @QueryParam("nombre") String nombre,
            @QueryParam("adaptada") @DefaultValue("true") boolean adaptada) {
        if (nombre == null || nombre.isBlank()) {
            throw new BadRequestException("El parámetro 'nombre' es obligatorio");
        }

        return repository.buscarPorNombreYAdaptada(nombre, adaptada);
    }

    /**
     * Endpoint masivo: actualiza la descripción de la disciplina con el ID especificado.
     * Retorna cuántas filas fueron modificadas.
     * Ejemplo: PUT /api/repositorio/disciplinas/5/descripcion?nueva=NuevaDesc
     */
    @PUT 
    @Path("/{id}/descripcion")
    @Transactional 
    public Response actualizarDescripcion(
            @PathParam("id") Long id,
            @QueryParam("nueva") String nuevaDesc) {
        if (nuevaDesc == null || nuevaDesc.isBlank()) {
            throw new BadRequestException("Debe proporcionar la nueva descripción");
        }

        long actualizados = repository.actualizarDescripcionPorId(id, nuevaDesc); 

        if (actualizados == 0) {
            throw new NotFoundException("Disciplina con id " + id + " no encontrada");
        }

        return Response.ok()
                .entity(Map.of("actualizados", actualizados))
                .build();
    }

    /**
     * Endpoint masivo: elimina todas las disciplinas con adaptada = true.
     * Devuelve 204 sin contenido.
     * Ejemplo: DELETE /api/repositorio/disciplinas/adaptadas
     */
    @DELETE
    @Path("/adaptadas")
    @Transactional
    public Response eliminarAdaptadas() {
        repository.eliminarAdaptadas();
        return Response.noContent().build();
    }

    /**
     * Endpoint: cuenta cuántas disciplinas tienen adaptada = true.
     * Ejemplo: GET /api/repositorio/disciplinas/contarAdaptadas
     */
    @GET
    @Path("/contarAdaptadas")
    public long contarAdaptadas() {
        return repository.contarAdaptadas();
    }

    @GET
    @Path("/resumen")
    public List<DisciplinaDto> listarResumen() {
        return repository.listarResumen();
    }

    @GET
    @Path("/buscar-avanzado")
    public List<Disciplina> buscarAvanzado(
            @QueryParam("nombre") String nombre,
            @QueryParam("adaptada") boolean adaptada) {

        return repository.buscarPorNombreYAdaptada(nombre, adaptada);
    }

    @GET
    @Path("/rango")
    public List<Disciplina> consultarPorRango(
            @QueryParam("desde") int desde,
            @QueryParam("hasta") int hasta) {
        return repository.consultarPorRango(desde, hasta);
    }
}
