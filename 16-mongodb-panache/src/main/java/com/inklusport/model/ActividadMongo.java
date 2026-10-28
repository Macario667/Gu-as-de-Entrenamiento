package com.inklusport.model;

public class ActividadMongo {
    
    public String id;
    public String nombre;
    public String deporte;
    public Integer capacidad;

    public ActividadMongo() {
    }

    public ActividadMongo(
            String id,
            String nombre,
            String deporte,
            Integer capacidad) {
        this.id = id;
        this.nombre = nombre;
        this.deporte = deporte;
        this.capacidad = capacidad;
    }
}
