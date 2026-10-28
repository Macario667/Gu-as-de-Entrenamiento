package com.inklusport.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection 
public class DisciplinaDto {
    public final String nombre;
    public final boolean adaptada;

    public DisciplinaDto(String nombre, boolean adaptada) {
        this.nombre = nombre;
        this.adaptada = adaptada;
    }
}
