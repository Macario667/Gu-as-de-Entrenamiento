package com.inklusport.persistence.repository;

import java.util.List;
import java.util.Map;

import com.inklusport.model.DisciplinaDto;
import com.inklusport.persistence.entity.Disciplina;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

@ApplicationScoped 
public class DisciplinaRepository implements PanacheRepository<Disciplina> {
    
    public List<Disciplina> listarAdaptadas() {
        return list("adaptada", true);
    }

    public List<Disciplina> buscarPorNombre(String nombre) {
        return list(
                "lower(nombre) like lower(?1)",
                "%" + nombre.trim() + "%"
        );
    }

    /**
     * Sort.by("nombre") ordena ascendentemente.
     * .descending() invierte el orden.
     * Puedes combinar campos con Sort.by("adaptada").and("nombre").
     * El ordenamiento se realiza en la consulta a la base de datos.
     */

    public List<Disciplina> listarOrdenadasPorNombre() {
        return listAll(Sort.by("nombre"));
    }

    public List<Disciplina> listarOrdenadasPorNombreDesc() {
        return listAll(Sort.by("nombre").descending());
    }

    /**
     * PanacheQuery representa una consulta que puedes configurar y ejecutar
     * Además de obtener resultados, permite consultar el número total de páginas y de registros.
     * @param pagina
     * @param tamaño
     */
    public PanacheQuery<Disciplina> consultarPaginadas(int pagina, int tamaño) {
        if (pagina < 0 || tamaño < 1 || tamaño > 100) {
            throw new IllegalArgumentException(
                "Página inválida o tamaño fuera del rango permitido");
        }

        return findAll(Sort.by("nombre"))
                .page(Page.of(pagina, tamaño));
    }

    /**
     * Actualiza la descripción de la disciplina con el ID dado.
     * Retorna el número de filas afectadas (0 si no existe).
     */
    public long actualizarDescripcionPorId(Long id, String descripcion) {
        return update(
            "descripcion = :descripcion where id = :id",
            Map.of("descripcion", descripcion, "id", id)
        );
    }

    /**
     * Elimina todas las disciplinas adaptadas (adaptada = true).
     * Retorna el número de registros eliminados.
     */
    public long eliminarAdaptadas() {
        return delete("adaptada", true);
    }

    /**
     * Cuenta cuántas disciplinas tienen adaptada = true.
     */
    public long contarAdaptadas() {
        return count("adaptada", true);
    }

    public List<DisciplinaDto> listarResumen() {
        /**
         * Ordenar por nombre y proyectamos a DisciplinaDto
         */
        return findAll(Sort.by("nombre"))
                .project(DisciplinaDto.class)
                .list();
    }

    @Transactional 
    public long actualizarAdaptadaPorId(Long id, boolean nuevaAdaptada) {
        /**
         * Consulta HQL simplificado: "adaptada = ?1 where id = ?2"
         */
        return update("adaptada = ?1 where id = ?2", nuevaAdaptada, id);
    }

    @Transactional
    public long eliminarNoAdaptadas() {
        return delete("adaptada", false);
    }

    public List<Disciplina> buscarPorNombreYAdaptada(
            String nombre,
            boolean adaptada) {

        return list(
            "lower(nombre) like lower(:nombre) and adaptada = :adaptada",
            Map.of(
                "nombre", "%" + nombre.trim() + "%",
                "adaptada", adaptada
            )
        );
    }

    public List<Disciplina> consultarPorRango(int desde, int hasta) {
        if (desde < 0 || hasta < desde) {
            throw new IllegalArgumentException("Rango no válido");
        }

        return findAll(Sort.by("nombre"))
                .range(desde, hasta)
                .list();
    }

    @Transactional 
    public Disciplina buscarPorIdConBloqueo(Long id) {
        return findById(id, LockModeType.PESSIMISTIC_WRITE);
    }

    public List<Disciplina> buscarAdaptadasConConsultaNombrada() {
    return find("#Disciplina.buscarAdaptadas",
                Parameters.with("adaptada", true))
            .list();
    }
}
