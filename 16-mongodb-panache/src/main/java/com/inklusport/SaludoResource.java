package com.inklusport;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Segunda forma de leer configuración: la anotación @ConfigProperty.
 *
 * A diferencia de @ConfigMapping (grupo tipado), aquí se pide una propiedad
 * concreta y puede inyectarse directamente en el punto de uso.
 */
@Path("/saludo")
@Produces(MediaType.TEXT_PLAIN)
public class SaludoResource {

    @Inject
    AppConfig config;

    @ConfigProperty(name = "app.bienvenida")
    String bienvenida;

    @GET
    public String saludar() {
        return bienvenida + " — " + config.modo();
    }
}
