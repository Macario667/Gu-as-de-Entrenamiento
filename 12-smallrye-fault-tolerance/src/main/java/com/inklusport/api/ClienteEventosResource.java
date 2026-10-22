package com.inklusport.api;

import java.util.List;
import java.util.concurrent.CompletionStage;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.inklusport.client.EventosClient;
import com.inklusport.model.Evento;

@Path("/api/cliente/eventos")
@Produces(MediaType.APPLICATION_JSON)
public class ClienteEventosResource {

    @Inject
    @RestClient
    EventosClient eventosClient;

    @GET
    @Path("/sync")
    public List<Evento> listarSincrono() {
        return eventosClient.listar();
    }

    @GET
    @Path("/async")
    public CompletionStage<List<Evento>> listarAsincrono() {
        return eventosClient.listarAsync();
    }
}
