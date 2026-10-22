package com.inklusport.logging;

import java.time.Duration;
import java.time.Instant;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

/**
 * Filtro que registra una linea por peticion: metodo, ruta, estado y duracion.
 *
 * Los filtros de contenedor se ejecutan en la cadena de filtros de Jakarta REST
 * tanto en RESTEasy Classic como en Quarkus REST.
 */
@Provider
public class LogFiltro implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(LogFiltro.class);

    private static final String INICIO = "inicio";

    @Override
    public void filter(ContainerRequestContext ctx) {
        ctx.setProperty(INICIO, Instant.now());
        LOG.infof("--> %s %s", ctx.getMethod(), ctx.getUriInfo().getPath());
    }

    @Override
    public void filter(ContainerRequestContext ctx, ContainerResponseContext response) {
        Object inicio = ctx.getProperty(INICIO);
        if (inicio instanceof Instant) {
            Duration duracion = Duration.between((Instant) inicio, Instant.now());
            LOG.infof("<-- %s %s [%d] en %d ms",
                    ctx.getMethod(), ctx.getUriInfo().getPath(),
                    response.getStatus(), duracion.toMillis());
        }
    }
}
