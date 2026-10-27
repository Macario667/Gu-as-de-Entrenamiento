package com.inklusport.api;

import java.sql.SQLException;
import java.util.Map;

import com.inklusport.persistence.ConexionService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/persistencia")
@Produces(MediaType.APPLICATION_JSON)
public class PersistenciaResource {
    
    @Inject 
    ConexionService conexionService;

    @GET
    @Path("/conexion")
    public Map<String, Object> comprobarConexion() {
        try {
            return conexionService.verificarConexion();
        } catch (SQLException e) {
            throw new WebApplicationException(
                "No fue posible verificar la conexión con la base de datos",
                Response.Status.SERVICE_UNAVAILABLE
            );
        }
    }
}
