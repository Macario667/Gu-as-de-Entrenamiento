package com.inklusport.api;

import java.util.Map;

import com.inklusport.service.TareaProgramadaService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*; /** Métodos HTTP */
import jakarta.ws.rs.core.MediaType;

@Path("/scheduler")
@Produces(MediaType.APPLICATION_JSON)
public class SchedulerResource {
    
    @Inject 
    TareaProgramadaService tareaService;

    @GET 
    @Path("/estado")
    public Map<String, String> consultarEstado() {
        return Map.of(
            "ultimaEjecución", tareaService.getUltimaEjecucion(),
            "ultimoResultado", tareaService.getUltimoResultado()
        );
    }
}
