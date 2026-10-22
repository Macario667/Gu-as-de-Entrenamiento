package com.inklusport.json;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonPointer;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;

/**
 * Utilidades JSON-P (Jakarta JSON Processing).
 *
 * JSON-P es la API estandar de Jakarta para construir y consumir documentos
 * JSON sin depender de un framework de binding concreto.
 *
 * Tipos de datos de JSON (segun RFC 8259):
 *   object, array, string, number, boolean y null.
 */
public final class JsonUtil {

    private JsonUtil() {
    }

    /**
     * Construye un JsonObject a partir de clave/valor.
     */
    public static JsonObject objeto() {
        return Json.createObjectBuilder().build();
    }

    /**
     * Documento de ejemplo con los seis tipos de JSON.
     */
    public static JsonObject documentoEjemplo() {
        return Json.createObjectBuilder()
                .add("nombre", "Torneo inclusivo")
                .add("activo", true)
                .add("capacidad", 120)
                .add("precio", 12.5)
                .add("descripcion", JsonValue.NULL)
                .add("deportes", Json.createArrayBuilder()
                        .add("baloncesto")
                        .add("atletismo")
                        .add("petanca"))
                .add("sede", Json.createObjectBuilder()
                        .add("ciudad", "Valencia")
                        .add("pistas", 3))
                .build();
    }

    /**
     * Parsea una cadena JSON en un JsonStructure (objeto o array).
     */
    public static JsonStructure parsear(String texto) {
        try (jakarta.json.JsonReader reader = Json.createReader(new StringReader(texto))) {
            return reader.read();
        }
    }

    /**
     * Devuelve el texto JSON con sangrado legible.
     *
     * Para el formato con sangria hay que usar JsonWriterFactory con
     * JsonGenerator.PRETTY_PRINTING a true.
     */
    public static String bonito(JsonStructure estructura) {
        StringWriter writer = new StringWriter();
        try (jakarta.json.JsonWriter jsonWriter = Json.createWriterFactory(
                Map.of(jakarta.json.stream.JsonGenerator.PRETTY_PRINTING, true))
                .createWriter(writer)) {
            jsonWriter.write(estructura);
        }
        return writer.toString();
    }

    /**
     * Texto JSON compacto (sin espacios), util para comparaciones.
     */
    public static String compacto(JsonStructure estructura) {
        StringWriter writer = new StringWriter();
        try (jakarta.json.JsonWriter jsonWriter = Json.createWriter(writer)) {
            jsonWriter.write(estructura);
        }
        return writer.toString().replaceAll("\\s+", "");
    }

    /**
     * Acceso a un valor anidado con JSON Pointer (RFC 6901).
     *
     *   /sede/ciudad
     *   /deportes/0
     */
    public static JsonValue porPuntero(JsonObject objeto, String puntero) {
        return Json.createPointer(puntero).getValue(objeto);
    }

    /**
     * Tipo de un valor JSON como texto legible.
     */
    public static String tipoDe(JsonValue valor) {
        switch (valor.getValueType()) {
            case OBJECT:
                return "object";
            case ARRAY:
                return "array";
            case STRING:
                return "string";
            case NUMBER:
                return "number";
            case TRUE:
            case FALSE:
                return "boolean";
            case NULL:
                return "null";
            default:
                return "desconocido";
        }
    }

    /**
     * Extrae el valor "plano" de un JsonValue.
     */
    public static Object valorPlano(JsonValue valor) {
        switch (valor.getValueType()) {
            case OBJECT:
                return objetoAMapa(valor.asJsonObject());
            case ARRAY:
                List<Object> elementos = new ArrayList<>();
                valor.asJsonArray().forEach(elemento -> elementos.add(valorPlano(elemento)));
                return elementos;
            case STRING:
                return ((JsonString) valor).getString();
            case NUMBER:
                JsonNumber numero = (JsonNumber) valor;
                return numero.isIntegral() ? (Object) numero.longValue() : (Object) numero.doubleValue();
            case TRUE:
                return Boolean.TRUE;
            case FALSE:
                return Boolean.FALSE;
            case NULL:
                return null;
            default:
                return valor.toString();
        }
    }

    /**
     * Convierte un JsonObject en Map<String, Object> (util para devolverlo).
     */
    public static Map<String, Object> objetoAMapa(JsonObject objeto) {
        Map<String, Object> resultado = new java.util.LinkedHashMap<>();
        for (String clave : objeto.keySet()) {
            resultado.put(clave, valorPlano(objeto.get(clave)));
        }
        return resultado;
    }

    /**
     * Array JSON a partir de una lista de cadenas.
     */
    public static JsonArray arrayDeTexto(List<String> valores) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        valores.forEach(builder::add);
        return builder.build();
    }

    /**
     * Array JSON a partir de una lista de objetos.
     */
    public static JsonArray arrayDeObjetos(List<JsonObject> objetos) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        objetos.forEach(builder::add);
        return builder.build();
    }

    /**
     * Constructor anidado con la API fluida.
     */
    public static JsonObjectBuilder constructorAnidado() {
        return Json.createObjectBuilder()
                .add("id", 1)
                .add("evento", Json.createObjectBuilder()
                        .add("nombre", "Torneo inclusivo")
                        .add("deportes", Json.createArrayBuilder()
                                .add("baloncesto")
                                .add("petanca")));
    }

    /**
     * Lista de nombres de las claves de primer nivel.
     */
    public static List<String> claves(JsonObject objeto) {
        return new ArrayList<>(objeto.keySet());
    }
}
