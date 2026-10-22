package com.inklusport.model;

public class Actividad {

    public Long id;
    public String nombre;
    public String deporte;
    public Integer capacidad;

    public Actividad() {
    }

    public Actividad(Long id, String nombre, String deporte, Integer capacidad) {
        this.id = id;
        this.nombre = nombre;
        this.deporte = deporte;
        this.capacidad = capacidad;
    }
}
