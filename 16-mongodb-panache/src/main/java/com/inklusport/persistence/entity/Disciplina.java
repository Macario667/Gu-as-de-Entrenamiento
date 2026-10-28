
package com.inklusport.persistence.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@NamedQuery(
    name = "Disciplina.buscarAdaptadas",
    query = "FROM Disciplina WHERE adaptada = :adaptada"
)
@Entity
@Table(name = "disciplinas")
public class Disciplina extends PanacheEntity {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    public String nombre;

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    public String descripcion;

    public boolean adaptada;

    public Disciplina() {
    }

    public Disciplina(String nombre, String descripcion, boolean adaptada) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.adaptada = adaptada;
    }
}
