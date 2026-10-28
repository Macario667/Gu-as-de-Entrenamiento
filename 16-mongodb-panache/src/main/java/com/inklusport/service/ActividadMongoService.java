package com.inklusport.service;

import com.inklusport.model.ActividadMongo;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * búsqueda parcial del nombre sin distinguir mayúsculas y minúsculas.
 */
import java.util.regex.Pattern;

@ApplicationScoped 
public class ActividadMongoService {
    
    @Inject 
    MongoClient mongoClient;

    @ConfigProperty(name = "quarkus.mongodb.database")
    String databaseName;

    private MongoCollection<Document> coleccion() {
        return mongoClient
                .getDatabase(databaseName)
                .getCollection("actividades");
    }

    private ActividadMongo convertir(Document documento) {
        if (documento == null) {
            return null;
        }
        
        return new ActividadMongo(
            documento.getObjectId("_id").toHexString(),
            documento.getString("nombre"),
            documento.getString("deporte"),
            documento.getInteger("capacidad")
        );
    }

    public List<ActividadMongo> listar() {
        List<ActividadMongo> resultado = new ArrayList<>();

        for (Document documento : coleccion().find()) {
            resultado.add(convertir(documento));
        }
        return resultado;
    }

    public ActividadMongo buscarPorId(String id) {
        if (!ObjectId.isValid(id)) {
            return null;
        }

        Document documento = coleccion().find(
                Filters.eq("_id", new ObjectId(id))
        ).first();

        return convertir(documento);
    }

    public ActividadMongo crear(ActividadMongo actividad) {
        Document documento = new Document()
                .append("nombre", actividad.nombre)
                .append("deporte", actividad.deporte)
                .append("capacidad", actividad.capacidad);

        coleccion().insertOne(documento);

        return convertir(documento);
    }

    public ActividadMongo actualizar(
            String id,
            ActividadMongo actividad) {
        
        if (!ObjectId.isValid(id)) {
            return null;
        }

        var resultado = coleccion().updateOne(
                Filters.eq("_id", new ObjectId(id)),
                Updates.combine(
                    Updates.set("nombre", actividad.nombre),
                    Updates.set("deporte", actividad.deporte),
                    Updates.set("capacidad", actividad.capacidad)
                )
        );

        if (resultado.getMatchedCount() == 0) {
            return null;
        }

        return buscarPorId(id);
    }

    public boolean eliminar(String id) {
        if (!ObjectId.isValid(id)) {
            return false;
        }

        return coleccion().deleteOne(
                Filters.eq("_id", new ObjectId(id))  
        ).getDeletedCount() > 0;
    }

    public List<ActividadMongo> buscarConFiltros(
        String nombre,
        String deporte,
        Integer capacidadMinima) {

        List<Bson> filtros = new ArrayList<>();

        if (nombre != null && !nombre.isBlank()) {
            filtros.add(Filters.regex(
                    "nombre",
                    Pattern.quote(nombre.trim()),
                    "i"
            ));
        }

        if (deporte != null && !deporte.isBlank()) {
            filtros.add(Filters.regex(
                    "deporte",
                    "^" + Pattern.quote(deporte.trim()) + "$",
                    "i"
            ));
        }

        /**
         * gte: busca actividades cuya capacidad sea mayor o igual al mínimo.
         */
        if (capacidadMinima != null) {
            filtros.add(Filters.gte("capacidad", capacidadMinima));
        }

        Bson filtroFinal = filtros.isEmpty()
                ? new Document()
                : Filters.and(filtros);
        
        List<ActividadMongo> resultado = new ArrayList<>();

        for (Document documento : coleccion().find(filtroFinal)) {
            resultado.add(convertir(documento));
        }

        return resultado;
    }
}
