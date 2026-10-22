package com.inklusport.api;

import java.util.Map;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.inklusport.model.EventoValidadoInput;

@Path("/api/validacion/eventos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ValidacionResource {

    @POST
    public Map<String, Object> crear(@Valid EventoValidadoInput entrada) {
        return Map.of(
                "mensaje", "Evento validado",
                "nombre", entrada.getNombre(),
                "deporte", entrada.getDeporte(),
                "capacidad", entrada.getCapacidad());
    }
}
