package com.inklusport.api;

import java.util.Map;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.jboss.logmanager.MDC;
import org.jboss.logging.Logger;

import com.inklusport.model.Evento;
import com.inklusport.service.EventoService;

import jakarta.inject.Inject;

/**
 * Endpoints para practicar los distintos niveles de log y el uso del MDC.
 *
 *   GET /api/logging/niveles?nivel=DEBUG
 *   GET /api/logging/mdc?usuario=ana
 *   GET /api/logging/estructurado
 *   GET /api/logging/error
 */
@Path("/api/logging")
@Produces(MediaType.APPLICATION_JSON)
public class LoggingResource {

    private static final Logger LOG = Logger.getLogger(LoggingResource.class);

    @Inject
    EventoService service;

    /**
     * Escribe un mensaje en el nivel solicitado.
     */
    @GET
    @Path("/niveles")
    public Map<String, Object> niveles(@QueryParam("nivel") String nivel) {
        String mensaje = "Mensaje de prueba en nivel " + nivel;
        switch (nivel == null ? "INFO" : nivel.toUpperCase()) {
            case "TRACE" -> LOG.trace(mensaje);
            case "DEBUG" -> LOG.debug(mensaje);
            case "WARN" -> LOG.warn(mensaje);
            case "ERROR" -> LOG.error(mensaje);
            case "FATAL" -> LOG.fatal(mensaje);
            default -> LOG.info(mensaje);
        }
        return Map.of("nivel", nivel == null ? "INFO" : nivel, "mensaje", mensaje);
    }

    /**
     * Añade claves al MDC: quedan en todas las lineas de log de la peticion.
     */
    @GET
    @Path("/mdc")
    public Map<String, Object> mdc(@QueryParam("usuario") String usuario) {
        MDC.put("usuario", usuario == null ? "anonimo" : usuario);
        MDC.put("operacion", "consultaMDC");

        LOG.info("Operacion ejecutada con contexto de usuario");
        LOG.debugf("MDC actual: usuario=%s, operacion=%s",
                MDC.get("usuario"), MDC.get("operacion"));

        return Map.of(
                "usuario", MDC.get("usuario"),
                "operacion", MDC.get("operacion"),
                "requestId", String.valueOf(MDC.get("requestId")));
    }

    /**
     * Log estructurado: clave=valor para poder indexar en un agregador.
     */
    @GET
    @Path("/estructurado")
    public Map<String, Object> estructurado() {
        LOG.infof("evento=alta_equipo deporte=%s capacidad=%d",
                "baloncesto", 24);
        return Map.of("evento", "alta_equipo", "deporte", "baloncesto", "capacidad", 24);
    }

    /**
     * Provoca un error para ver la traza completa en el log.
     */
    @GET
    @Path("/error")
    public Map<String, Object> error() {
        LOG.error("Error simulado al procesar la reserva");
        throw new IllegalStateException("Fallo simulado en el servicio de reservas");
    }

    /**
     * Log con datos de negocio de la aplicacion.
     */
    @GET
    @Path("/eventos")
    public Map<String, Object> eventos() {
        for (Evento evento : service.listar()) {
            LOG.infof("evento=%s deporte=%s", evento.nombre, evento.deporte);
        }
        return Map.of("registrados", service.listar().size());
    }
}
