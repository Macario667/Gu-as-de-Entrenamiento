package com.inklusport.model;

/**
 * Modelo de entrada: el cuerpo JSON que recibe la API.
 */
public class EventoInput {

    public String nombre;
    public String deporte;
    public Integer capacidad;

    public EventoInput() {
    }

    public EventoInput(String nombre, String deporte, Integer capacidad) {
        this.nombre = nombre;
        this.deporte = deporte;
        this.capacidad = capacidad;
    }
}
