
package com.inklusport.service;

import com.inklusport.model.ActividadPanache;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;

@ApplicationScoped
public class ActividadPanacheService {

    @Inject
    ActividadPanacheRepository repository;

    @Inject
    MongoClient mongoClient;

    @PostConstruct 
    void crearIndices() {
        MongoCollection<Document> collection = mongoClient
                .getDatabase("inklusport")
                .getCollection("actividades_panache");

        collection.createIndex(Indexes.ascending("deporte"));
        collection.createIndex(Indexes.ascending("capacidad"));
    }

    public List<ActividadPanache> buscarActividades(
        String deporte, int capacidadMinima) {

        return repository.list(
            "deporte = ?1 and capacidad >= ?2",
            Sort.by("capacidad").descending(),
            deporte,
            capacidadMinima
        );
    }

    public List<ActividadPanache> listar() {
        return repository.listAll();
    }

    public Optional<ActividadPanache> buscarPorId(String id) {
        try {
            return Optional.ofNullable(
                repository.findById(
                    new org.bson.types.ObjectId(id)
                )
            );
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public ActividadPanache crear(ActividadPanache actividad) {
        actividad.id = null;
        repository.persist(actividad);
        return actividad;
    }

    public boolean eliminar(String id) {
        try {
            return repository.deleteById(
                new org.bson.types.ObjectId(id)
            );
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public List<ActividadPanache> buscarPorDeporte(String deporte) {
        return repository.list(
            "deporte", Sort.by("nombre").ascending(), deporte
        );
    }

    public List<ActividadPanache> buscarConCapacidadMinima(
            int capacidadMinima) {
        return repository.list(
            "capacidad >= ?1",
            Sort.by("capacidad").descending(),
            capacidadMinima
        );
    }

    public List<ActividadPanache> buscarPorNombre(String nombre) {
        return repository.list(
            "lower(nombre) like ?1",
            Sort.by("nombre").ascending(),
            "%" + nombre.toLowerCase() + "%"
        );
    }

    public List<ActividadPanache> listarPagina(int pagina, int tamano) {
        if (pagina < 0 || tamano < 1 || tamano > 100) {
            throw new IllegalArgumentException(
                "Página inválida o tamaño fuera del rango permitido (1-100)"
            );
        }

        return repository.findAll(Sort.by("nombre").ascending())
            .page(Page.of(pagina, tamano))
            .list();
    }

    public boolean actualizar(
        String id, ActividadPanache datos) {
    Optional<ActividadPanache> resultado = buscarPorId(id);

    if (resultado.isEmpty()
                || datos == null
                || datos.nombre == null || datos.nombre.isBlank()
                || datos.deporte == null || datos.deporte.isBlank()
                || datos.capacidad == null || datos.capacidad < 1) {
            return false;
        }

        ActividadPanache existente = resultado.get();
        existente.nombre = datos.nombre;
        existente.deporte = datos.deporte;
        existente.capacidad = datos.capacidad;

        repository.update(existente);
        return true;
    }

    public List<ActividadPanache> filtrar(
            String deporte,
            Integer capacidadMinima,
            Integer capacidadMaxima,
            String nombre) {

        StringBuilder query = new StringBuilder("1 = 1");
        java.util.List<Object> parametros = new java.util.ArrayList<>();

        if (deporte != null && !deporte.isBlank()) {
            query.append(" and deporte = ?")
                .append(parametros.size() + 1);
            parametros.add(deporte);
        }

        if (capacidadMinima != null) {
            query.append(" and capacidad >= ?")
                .append(parametros.size() + 1);
            parametros.add(capacidadMinima);
        }

        if (capacidadMaxima != null) {
            query.append(" and capacidad <= ?")
                .append(parametros.size() + 1);
            parametros.add(capacidadMaxima);
        }

        if (nombre != null && !nombre.isBlank()) {
            query.append(" and nombre like ?")
                .append(parametros.size() + 1);
            parametros.add("%" + nombre + "%");
        }

        return repository.find(
            query.toString(),
            Sort.by("nombre").ascending(),
            parametros.toArray()
        ).list();
    }

    public long eliminarPorDeporte(String deporte) {
        if (deporte == null || deporte.isBlank()) {
            throw new IllegalArgumentException(
                "Debe indicar un deporte"
            );
        }

        return repository.delete("deporte", deporte);
    }

    public List<ActividadPanache> filtrarAvanzado(
        String deporte,
        Integer capacidadMinima,
        Integer capacidadMaxima,
        String nombre) {

        List<Bson> filtros = new ArrayList<>();

        if (deporte != null && !deporte.isBlank()) {
            filtros.add(Filters.eq("deporte", deporte));
        }

        if (capacidadMinima != null) {
            filtros.add(Filters.gte("capacidad", capacidadMinima));
        }

        if (capacidadMaxima != null) {
            filtros.add(Filters.lte("capacidad", capacidadMaxima));
        }

        if (nombre != null && !nombre.isBlank()) {
            filtros.add(Filters.regex(
                    "nombre",
                    java.util.regex.Pattern.quote(nombre),
                    "i"
            ));
        }

        Bson filtroFinal = filtros.isEmpty()
                ? new org.bson.Document()
                : Filters.and(filtros);

        return mongoClient
                .getDatabase("inklusport")
                .getCollection("actividades_panache", ActividadPanache.class)
                .find(filtroFinal)
                .sort(new org.bson.Document("nombre", 1))
                .into(new ArrayList<>());
    }
}
