package com.inklusport.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import org.bson.codecs.pojo.annotations.BsonProperty;

/**
 * PanacheMongoEntity proporciona un identificador de tipo ObjectId. 
 * Por eso, esta clase es independiente de Actividad, que conserva 
 * su identificador relacional Long, y de ActividadMongo, que 
 * utiliza un identificador de texto.
 * ActividadPanache
 */
public class ActividadPanache extends PanacheMongoEntity {
    
    public String nombre;

    public String deporte;

    public Integer capacidad;
}
