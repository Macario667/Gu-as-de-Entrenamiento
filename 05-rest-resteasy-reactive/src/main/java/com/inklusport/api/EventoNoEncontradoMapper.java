package com.inklusport.api;

import java.util.Map;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * @Provider + ExceptionMapper: transforma la excepción de dominio en JSON.
 * Es el mecanismo estándar de Jakarta REST y funciona igual en Classic y Reactive.
 */
@Provider
public class EventoNoEncontradoMapper implements ExceptionMapper<EventoNoEncontradoException> {

    @Override
    public Response toResponse(EventoNoEncontradoException e) {
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("error", e.getMessage()))
                .build();
    }
}
