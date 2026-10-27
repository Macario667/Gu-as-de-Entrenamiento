package com.inklusport.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * DTO de la API JSON: aqui se controla exactamente como se serializa.
 *
 * Anotaciones de Jackson:
 *   @JsonProperty      renombra el campo (y define el nombre en el JSON)
 *   @JsonIgnoreProperties ignora campos desconocidos que llegan en el cuerpo
 *   @JsonInclude       decide si los valores nulos se emiten o no
 *   @JsonFormat        formato de las fechas
 *   @JsonPropertyOrder orden de las claves en el JSON
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "nombre", "disciplina", "fechaInicio", "plazas" })
public class EventoJson {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nombre")
    private String nombre;

    /** En el JSON sale como "disciplina", aunque en Java se llame deporte. */
    @JsonProperty("disciplina")
    private String deporte;

    @JsonProperty("fechaInicio")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate fechaInicio;

    @JsonProperty("plazas")
    private Integer plazas;

    public EventoJson() {
    }

    public EventoJson(Long id, String nombre, String deporte, LocalDate fechaInicio, Integer plazas) {
        this.id = id;
        this.nombre = nombre;
        this.deporte = deporte;
        this.fechaInicio = fechaInicio;
        this.plazas = plazas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDeporte() {
        return deporte;
    }

    public void setDeporte(String deporte) {
        this.deporte = deporte;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Integer getPlazas() {
        return plazas;
    }

    public void setPlazas(Integer plazas) {
        this.plazas = plazas;
    }
}
