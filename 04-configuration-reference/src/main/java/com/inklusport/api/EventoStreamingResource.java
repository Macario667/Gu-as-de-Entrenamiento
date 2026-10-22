package com.inklusport.api;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.StreamingOutput;

import com.inklusport.model.Evento;
import com.inklusport.service.EventoService;

/**
 * Streaming clásico con StreamingOutput: la respuesta se escribe por partes
 * (chunked) en lugar de construirse entera en memoria.
 */
@Path("/api/informe")
public class EventoStreamingResource {

    @Inject
    EventoService service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public StreamingOutput informe() {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException {
                Writer writer = new OutputStreamWriter(output, StandardCharsets.UTF_8);
                List<Evento> eventos = service.listar();

                writer.write("Informe de eventos (" + eventos.size() + ")\n");
                for (Evento evento : eventos) {
                    writer.write("- " + evento.id + ": " + evento.nombre
                            + " (" + evento.deporte + ", " + evento.capacidad + " plazas)\n");
                }
                writer.flush();
            }
        };
    }
}
