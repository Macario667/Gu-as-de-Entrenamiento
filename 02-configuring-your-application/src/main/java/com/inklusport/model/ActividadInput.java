package com.inklusport.model;

public class ActividadInput {

    public String nombre;
    public String deporte;
    public Integer capacidad;

    public ActividadInput() {
    }

    public ActividadInput(String nombre, String deporte, Integer capacidad) {
        this.nombre = nombre;
        this.deporte = deporte;
        this.capacidad = capacidad;
    }
}
