package com.inklusport.client;

import java.util.List;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.inklusport.model.Evento;

@RegisterRestClient(configKey = "eventos-api")
@Path("/api/eventos")
@Produces(MediaType.APPLICATION_JSON)
public interface EventosClient {

    @GET
    List<Evento> listar();

    @GET
    CompletionStage<List<Evento>> listarAsync();
}
