package com.inklusport.service;

import com.inklusport.model.ActividadMongo;
import com.inklusport.model.ActividadMongoCodec;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.ArrayList;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromCodecs;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;


@ApplicationScoped 
public class ActividadMongoCodecService {
    
    private MongoClient cliente;
    private MongoCollection<ActividadMongo> coleccion;

    @PostConstruct
    void iniciar() {
        CodecRegistry registro = fromRegistries(
                fromCodecs(new ActividadMongoCodec()),
                MongoClientSettings.getDefaultCodecRegistry()
        );

        MongoClientSettings configuracion =
                MongoClientSettings.builder()
                        .applyConnectionString(
                                new com.mongodb.ConnectionString(
                                        System.getProperty(
                                                "quarkus.mongodb.connection-string",
                                                "mongodb://localhost:27017"
                                        )
                                )
                        )
                        .codecRegistry(registro)
                        .build();

        cliente = MongoClients.create(configuracion);

        coleccion = cliente
                .getDatabase("inklusport")
                .getCollection("actividades_codec", ActividadMongo.class);
    }

    public List<ActividadMongo> listar() {
        return coleccion.find().into(new ArrayList<>());
    }

    public ActividadMongo buscarPorId(String id) {
        return coleccion.find(Filters.eq("_id", id)).first();
    }

    public void crear(ActividadMongo actividad) {
        coleccion.insertOne(actividad);
    }

    @PreDestroy
    void cerrar() {
        if (cliente != null) {
            cliente.close();
        }
    } 
}
