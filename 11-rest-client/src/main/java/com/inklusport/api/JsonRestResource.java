package com.inklusport.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.inklusport.json.JacksonConfig;
import com.inklusport.model.EventoJson;
import com.inklusport.model.Precio;
import com.inklusport.service.EventoService;

/**
 * Guía 08 · Writing JSON REST Services.
 *
 * Control fino de la serializacion JSON: nombres de los campos, inclusion de
 * nulos, formatos de fecha, serializadores propios y configuracion global
 * de Jackson.
 */
@Path("/api/json-rest")
@Produces(MediaType.APPLICATION_JSON)
public class JsonRestResource {

    @Inject
    EventoService service;

    @Inject
    ObjectMapper mapper;

    /**
     * GET /api/json-rest/eventos
     *
     * Los DTO controlan el nombre de cada campo ("deporte" sale como
     * "disciplina") y las fechas con formato dd/MM/yyyy.
     */
    @GET
    @Path("/eventos")
    public List<EventoJson> eventos() {
        return service.listar().stream()
                .map(evento -> new EventoJson(
                        evento.id,
                        evento.nombre,
                        evento.deporte,
                        LocalDate.of(2026, 3, 15),
                        evento.capacidad))
                .toList();
    }

    /**
     * POST /api/json-rest/eventos
     *
     * Acepta un cuerpo con campos que el DTO no conoce: se ignoran
     * (@JsonIgnoreProperties) y los nulos no se devuelven (@JsonInclude).
     */
    @POST
    @Path("/eventos")
    @Consumes(MediaType.APPLICATION_JSON)
    public Map<String, Object> crear(EventoJson entrada) {
        EventoJson creado = new EventoJson(99L, entrada.getNombre(), entrada.getDeporte(),
                entrada.getFechaInicio(), entrada.getPlazas());
        return Map.of(
                "recibido", creado.getNombre(),
                "disciplina", String.valueOf(creado.getDeporte()),
                "serializado", JacksonConfig.class.getSimpleName());
    }

    /**
     * GET /api/json-rest/precio
     *
     * Serializador personalizado: Precio se emite como "12.50 EUR".
     */
    @GET
    @Path("/precio")
    public Map<String, Object> precio() {
        Precio precio = new Precio(12.5, "EUR");
        return Map.of(
                "precio", precio,
                "texto", precio.toString(),
                "importe", precio.getImporte(),
                "moneda", precio.getMoneda());
    }

    /**
     * POST /api/json-rest/precio/eco
     *
     * Deserializa el texto de precio personalizado y lo vuelve a serializar.
     */
    @POST
    @Path("/precio/eco")
    @Consumes(MediaType.APPLICATION_JSON)
    public Precio ecoPrecio(Precio precio) {
        return precio;
    }

    /**
     * GET /api/json-rest/config
     *
     * Muestra la configuracion efectiva del ObjectMapper.
     */
    @GET
    @Path("/config")
    public Map<String, Object> configuracion() {
        return Map.of(
                "inclusionPorDefecto", String.valueOf(
                        mapper.getSerializationConfig().getDefaultPropertyInclusion()),
                "escribeFechasComoTimestamp", mapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS),
                "fallaConPropiedadesDesconocidas", mapper.isEnabled(
                        com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES),
                "modulos", mapper.getRegisteredModuleIds().toString());
    }

    /**
     * GET /api/json-rest/json-crudo
     *
     * Devuelve el JSON ya construido, sin pasar por el binding de Jackson.
     */
    @GET
    @Path("/json-crudo")
    @Produces(MediaType.APPLICATION_JSON)
    public String jsonCrudo() {
        return """
                {
                  "mensaje": "Este JSON se escribe a mano, sin POJOs",
                  "anidado": { "ok": true, "valores": [1, 2, 3] }
                }
                """;
    }
}
