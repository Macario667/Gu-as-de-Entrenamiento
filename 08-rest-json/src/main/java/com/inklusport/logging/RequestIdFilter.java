package com.inklusport.logging;

import java.util.UUID;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logmanager.MDC;
import org.jboss.logging.Logger;

/**
 * Correlacion de peticiones mediante MDC (Mapped Diagnostic Context).
 *
 * MDC guarda un conjunto de clave/valor asociado a la peticion en curso.
 * Todo lo que se registre dentro de esa peticion lleva esos valores,
 * lo que permite reconstruir la traza de una peticion en los logs.
 *
 * El identificador de peticion se propaga en la cabecera X-Request-Id:
 *   - si el cliente la envia, se respeta;
 *   - si no, se genera uno nuevo.
 */
@Provider
public class RequestIdFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(RequestIdFilter.class);

    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String REQUEST_ID_MDC_KEY = "requestId";
    public static final String USUARIO_MDC_KEY = "usuario";

    @Override
    public void filter(ContainerRequestContext ctx) {
        String requestId = ctx.getHeaderString(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        ctx.setProperty(REQUEST_ID_HEADER, requestId);

        MDC.put(REQUEST_ID_MDC_KEY, requestId);
        MDC.put("ruta", ctx.getUriInfo().getPath());
        MDC.put("metodo", ctx.getMethod());

        LOG.debugf("Peticion recibida %s %s (id=%s)",
                ctx.getMethod(), ctx.getUriInfo().getPath(), requestId);
    }

    @Override
    public void filter(ContainerRequestContext ctx, ContainerResponseContext ctx2) {
        Object requestId = ctx.getProperty(REQUEST_ID_HEADER);
        if (requestId != null) {
            ctx2.getHeaders().putSingle(REQUEST_ID_HEADER, requestId.toString());
        }
        LOG.infof("Peticion finalizada %s %s -> %d (id=%s)",
                ctx.getMethod(), ctx.getUriInfo().getPath(),
                ctx2.getStatus(), requestId);

        MDC.remove(REQUEST_ID_MDC_KEY);
        MDC.remove("ruta");
        MDC.remove("metodo");
    }
}
