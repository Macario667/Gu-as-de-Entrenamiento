package com.inklusport.api;

import java.util.Map;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/api/documentacion")
@Produces(MediaType.APPLICATION_JSON)
@OpenAPIDefinition(info = @Info(
        title = "InkluSport API",
        version = "1.0.0",
        description = "API acumulativa para practicar Quarkus"),
        tags = @Tag(name = "Documentación", description = "Exploración de la API"))
@Tag(name = "Documentación")
public class DocumentacionResource {

    @GET
    @Operation(summary = "Listar las rutas de documentación disponibles")
    @APIResponse(responseCode = "200", description = "Enlaces de OpenAPI y Swagger UI")
    public Map<String, String> rutas() {
        return Map.of(
                "openapi", "/q/openapi",
                "swaggerUi", "/q/swagger-ui");
    }
}
