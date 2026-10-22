package com.inklusport.api;

import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.reactive.RestCookie;
import org.jboss.resteasy.reactive.RestHeader;
import org.jboss.resteasy.reactive.RestMatrix;
import org.jboss.resteasy.reactive.RestQuery;

/**
 * Parametros de la peticion, en sus cuatro variantes.
 *
 * Las anotaciones estandar de Jakarta REST (@QueryParam, @PathParam...)
 * siguen siendo validas en Quarkus REST. Las anotaciones propias
 * (@RestQuery, @RestHeader, @RestCookie, @RestMatrix) anaden la conversion
 * de tipo y permiten agrupar los parametros con @BeanParam.
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
     * GET /api/parametros/consulta
     *
     * Variante reactiva con @RestQuery y valor por defecto.
     */
    @GET
    @Path("/consulta")
    public Map<String, Object> consultaReactiva(
            @RestQuery @DefaultValue("0") int offset,
            @RestQuery @DefaultValue("20") int limit) {

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("tipo", "rest-query");
        resultado.put("offset", offset);
        resultado.put("limit", limit);
        return resultado;
    }

    /**
     * GET /api/parametros/cabeceras
     */
    @GET
    @Path("/cabeceras")
    public Map<String, Object> cabeceras(
            @RestHeader("X-Request-Id") String requestId,
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
    public Map<String, Object> cookies(@RestCookie("preferencias") String preferencias) {
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("tipo", "cookie");
        resultado.put("preferencias", preferencias);
        return resultado;
    }

    /**
     * GET /api/parametros/matriz;formato=json
     *
     * Los parametros de matriz viajan dentro del propio segmento de la ruta.
     */
    @GET
    @Path("/matriz;formato={formato}")
    public Map<String, Object> matriz(@RestMatrix("formato") String formato) {
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("tipo", "matriz");
        resultado.put("formato", formato);
        return resultado;
    }

    /**
     * Grupo de parametros reutilizable (Java record + @BeanParam).
     */
    public record FiltroEventos(
            @RestQuery String deporte,
            @RestQuery @DefaultValue("0") int offset,
            @RestHeader("X-Idioma") @DefaultValue("es") String idioma) {
    }

    /**
     * GET /api/parametros/filtrar?deporte=baloncesto
     */
    @GET
    @Path("/filtrar")
    public Map<String, Object> filtrar(@BeanParam FiltroEventos filtro) {
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("tipo", "bean-param");
        resultado.put("deporte", filtro.deporte());
        resultado.put("offset", filtro.offset());
        resultado.put("idioma", filtro.idioma());
        return resultado;
    }
}
