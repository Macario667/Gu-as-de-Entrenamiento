
package com.inklusport.service;

import com.inklusport.model.ActividadMongo;
import com.mongodb.client.model.Filters;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.bson.Document;
import org.bson.types.ObjectId;

@ApplicationScoped
public class ActividadMongoReactiveService {

    @Inject
    ReactiveMongoClient mongoClient;

    @ConfigProperty(name = "quarkus.mongodb.database")
    String databaseName;

    private ReactiveMongoCollection<Document> coleccion() {
        return mongoClient
                .getDatabase(databaseName)
                .getCollection("actividades_reactivas");
    }

    public Multi<ActividadMongo> listar() {
        return coleccion().find()
                .map(this::convertir);
    }

    public Uni<ActividadMongo> buscarPorId(String id) {
        if (!ObjectId.isValid(id)) {
            return Uni.createFrom().nullItem();
        }

        return coleccion().find(
                        Filters.eq("_id", new ObjectId(id))
                ).collect().first()
                .map(documento ->
                        documento == null ? null : convertir(documento));
    }

    public Uni<ActividadMongo> crear(ActividadMongo actividad) {
        Document documento = new Document()
                .append("nombre", actividad.nombre)
                .append("deporte", actividad.deporte)
                .append("capacidad", actividad.capacidad);

        return coleccion().insertOne(documento)
                .map(resultado -> {
                    actividad.id = documento
                            .getObjectId("_id")
                            .toHexString();
                    return actividad;
                });
    }

    public Uni<Boolean> eliminar(String id) {
        if (!ObjectId.isValid(id)) {
            return Uni.createFrom().item(false);
        }

        return coleccion().deleteOne(
                        Filters.eq("_id", new ObjectId(id))
                )
                .map(resultado -> resultado.getDeletedCount() > 0);
    }

    private ActividadMongo convertir(Document documento) {
        return new ActividadMongo(
                documento.getObjectId("_id").toHexString(),
                documento.getString("nombre"),
                documento.getString("deporte"),
                documento.getInteger("capacidad")
        );
    }
}
