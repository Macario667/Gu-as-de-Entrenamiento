package com.inklusport.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * PanacheMongoEntity proporciona un identificador de tipo ObjectId. 
 * Por eso, esta clase es independiente de Actividad, que conserva 
 * su identificador relacional Long, y de ActividadMongo, que 
 * utiliza un identificador de texto.
 * ActividadPanache
 */
@MongoEntity(collection = "actividades_panache")
public class ActividadPanache extends PanacheMongoEntity {
    
    @NotBlank
    public String nombre;
    
    @NotBlank
    public String deporte;
    
    @NotNull
    @Min(1)
    public Integer capacidad;
}
