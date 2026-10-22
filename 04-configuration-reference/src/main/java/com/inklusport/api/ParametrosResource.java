package com.inklusport.api;

import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.Produces;

/**
 * Los cuatro estilos de inyectar parámetros en la URL / cabeceras / cookies.
 * En RESTEasy Classic se usan las anotaciones estándar de Jakarta REST.
 */
@Path("/api/parametros")
@Produces(MediaType.APPLICATION_JSON)
public class ParametrosResource {

    /**
     * GET /api/parametros?page=2&size=10
     */
    @GET
    public Map<String, Object> consulta(
            @QueryParam("page") int page,
            @QueryParam("size") int size) {

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("tipo", "query");
        resultado.put("page", page);
        resultado.put("size", size);
        return resultado;
    }

    /**
     * GET /api/parametros/cabeceras
     */
    @GET
    @Path("/cabeceras")
    public Map<String, Object> cabeceras(
            @HeaderParam("X-Request-Id") String requestId,
            @HeaderParam("User-Agent") String userAgent) {

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("tipo", "header");
        resultado.put("requestId", requestId);
        resultado.put("userAgent", userAgent);
        return resultado;
    }

    /**
     * GET /api/parametros/cookies
     */
    @GET
    @Path("/cookies")
    public Map<String, Object> cookies(@CookieParam("preferencias") String preferencias) {
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("tipo", "cookie");
        resultado.put("preferencias", preferencias);
        return resultado;
    }

    /**
     * GET /api/parametros/matriz;formato=json
     *
     * Los parámetros de matriz viajan en el propio segmento de la ruta
     * y se separan con ";".
     */
    @GET
    @Path("/matriz;formato={formato}")
    public Map<String, Object> matriz(@MatrixParam("formato") String formato) {
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("tipo", "matriz");
        resultado.put("formato", formato);
        return resultado;
    }
}
