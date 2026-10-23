package com.inklusport;

import java.util.Map;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Expone la configuración tipada como JSON.
 */
@Path("/info")
@Produces(MediaType.APPLICATION_JSON)
public class InfoResource {

    @Inject
    AppConfig config;

    @GET
    public Map<String, Object> info() {
        return Map.of(
                "app", config.nombre(),
                "version", config.version(),
                "modo", config.modo().name(),
                "autor", Map.of(
                        "nombre", config.autor().nombre(),
                        "email", config.autor().email()
                )
        );
    }
}
