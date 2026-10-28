package com.inklusport;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Recurso REST mínimo: la unidad básica de una API en Quarkus.
 *
 * - @Path      fija la ruta base del recurso.
 * - @GET       declara el verbo HTTP.
 * - @Produces  indica el tipo de contenido de la respuesta.
 * - @PathParam captura un segmento variable de la ruta.
 */
@Path("/hello")
public class GreetingResource {

    /**
     * @Inject pide a CDI una instancia del bean GreetingService.
     * El bean existe porque la clase está anotada con @ApplicationScoped.
     */
    @Inject
    GreetingService service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus REST";
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/greeting/{name}")
    public String greeting(@PathParam("name") String name) {
        return service.greeting(name);
    }
}
