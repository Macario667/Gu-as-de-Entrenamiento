package com.inklusport.api;

import com.inklusport.resilience.ResilienciaService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/resiliencia")
@Produces(MediaType.APPLICATION_JSON)
public class ResilienciaResource {
    
    @Inject 
    ResilienciaService resilienciaService;

    @GET 
    @Path("/retry")
    public String retry() {
        resilienciaService.reiniciarIntentos();
        return resilienciaService.consultarConReintentos();
    }

    @GET 
    @Path("/fallback")
    public String fallback() {
        return resilienciaService.consultarConFallback();
    }
}
