package com.inklusport.api;

import java.util.List;
import java.util.Map;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import com.inklusport.json.JsonUtil;

/**
 * Guía 07 · Conocimientos JSON.
 *
 * Endpoints para practicar los fundamentos de JSON con la API estandar
 * JSON-P (jakarta.json), que viene incluida con la extension quarkus-jsonp.
 */
@Path("/api/json")
public class JsonResource {

    /**
     * GET /api/json/estructura
     *
     * Documento de ejemplo con los seis tipos de JSON y su tipo detectado.
     */
    @GET
    @Path("/estructura")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> estructura() {
        JsonObject documento = JsonUtil.documentoEjemplo();

        Map<String, Object> tipos = new java.util.LinkedHashMap<>();
        documento.forEach((clave, valor) -> tipos.put(clave, JsonUtil.tipoDe(valor)));

        return Map.of(
                "documento", JsonUtil.objetoAMapa(documento),
                "tipos", tipos,
                "claves", JsonUtil.claves(documento));
    }

    /**
     * GET /api/json/constructor
     *
     * Construccion de un documento JSON con JsonObjectBuilder / JsonArrayBuilder.
     */
    @GET
    @Path("/constructor")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> constructor() {
        return JsonUtil.objetoAMapa(JsonUtil.constructorAnidado().build());
    }

    /**
     * GET /api/json/bonito
     *
     * El mismo documento, en texto JSON con sangrado.
     */
    @GET
    @Path("/bonito")
    @Produces(MediaType.TEXT_PLAIN)
    public String bonito() {
        return JsonUtil.bonito(JsonUtil.documentoEjemplo());
    }

    /**
     * GET /api/json/compactar
     *
     * Version compacta: sin espacios, util para comparaciones o firmados.
     */
    @GET
    @Path("/compactar")
    @Produces(MediaType.TEXT_PLAIN)
    public String compactar() {
        return JsonUtil.compacto(JsonUtil.documentoEjemplo());
    }

    /**
     * GET /api/json/puntero?sede/ciudad
     *
     * Acceso a valores anidados con JSON Pointer (RFC 6901).
     */
    @GET
    @Path("/puntero")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> puntero(@QueryParam("ruta") String ruta) {
        String puntero = ruta == null ? "/sede/ciudad" : ruta;
        JsonObject documento = JsonUtil.documentoEjemplo();
        JsonValue valor = JsonUtil.porPuntero(documento, puntero);

        return Map.of(
                "puntero", puntero,
                "valor", JsonUtil.valorPlano(valor),
                "tipo", JsonUtil.tipoDe(valor));
    }

    /**
     * POST /api/json/parsear
     *
     * Recibe texto JSON plano y devuelve su estructura ya analizada.
     */
    @POST
    @Path("/parsear")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> parsear(String texto) {
        JsonStructure estructura = JsonUtil.parsear(texto);
        return Map.of(
                "tipo", JsonUtil.tipoDe(estructura),
                "contenido", estructura.toString(),
                "longitud", estructura.toString().length());
    }

    /**
     * POST /api/json/objeto
     *
     * Recibe directamente un JsonObject: Quarkus REST sabe deserializarlo
     * gracias a la extension JSON-P.
     */
    @POST
    @Path("/objeto")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> objeto(JsonObject recibido) {
        JsonObjectBuilder builder = Json.createObjectBuilder(recibido)
                .add("procesado", true);
        if (!recibido.containsKey("id")) {
            builder.add("id", 1);
        }
        return JsonUtil.objetoAMapa(builder.build());
    }

    /**
     * GET /api/json/array
     *
     * Arrays JSON: homogeneos y heterogeneos.
     */
    @GET
    @Path("/array")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> array() {
        JsonObject documento = JsonUtil.documentoEjemplo();
        List<String> deportes = documento.getJsonArray("deportes").getValuesAs(JsonString.class)
                .stream()
                .map(JsonString::getString)
                .toList();

        return Map.of(
                "homogeneo", deportes,
                "heterogeneo", List.of("texto", 42, true, "nulo",
                        Map.of("clave", "valor")),
                "tamano", documento.getJsonArray("deportes").size());
    }
}
