package com.inklusport.api;

import com.inklusport.service.SaludoService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/saludos")
@Produces(MediaType.TEXT_PLAIN)
public class SaludoResource {
    
    @Inject 
    SaludoService service;

    @GET 
    public String saludar() {
        return service.generarSaludo("Inklusport");
    }

    @GET 
    @Path("/{nombre}")
    public String saludarA(@PathParam("nombre") String nombre) {

        if (nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        return service.generarSaludo(nombre);
    }

    @GET 
    @Path("/estado")
    public String consultarEstado() {
        return "Servicio disponible";
    }
}
