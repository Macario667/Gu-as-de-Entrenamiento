package com.inklusport.model;

/**
 * Modelo de salida de un evento.
 *
 * En RESTEasy Classic la serialización JSON la resuelve Jackson
 * (extensión quarkus-resteasy-jackson) a partir de los campos públicos.
 */
public class Evento {

    public Long id;
    public String nombre;
    public String deporte;
    public Integer capacidad;

    /** Constructor vacío: lo necesita Jackson para deserializar. */
    public Evento() {
    }

    public Evento(Long id, String nombre, String deporte, Integer capacidad) {
        this.id = id;
        this.nombre = nombre;
        this.deporte = deporte;
        this.capacidad = capacidad;
    }
}
