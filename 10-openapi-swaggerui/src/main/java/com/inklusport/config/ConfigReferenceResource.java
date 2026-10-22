package com.inklusport.config;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.inklusport.AppConfig;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * Referencia de configuración en caliente: qué valor tiene cada propiedad,
 * de qué fuente viene y qué ordinal tiene esa fuente.
 */
@Path("/config")
@Produces(MediaType.APPLICATION_JSON)
public class ConfigReferenceResource {

    @Inject
    AppConfig config;

    /**
     * GET /config
     */
    @GET
    public Map<String, Object> detalle() {
        ConfigValue nombre = ConfigProvider.getConfig().getConfigValue("app.nombre");
        String fuente = nombre.getSourceName() == null ? "sin nombre" : nombre.getSourceName();

        Map<String, Object> detalle = new LinkedHashMap<>();
        detalle.put("clave", "app.nombre");
        detalle.put("valor", nombre.getValue());
        detalle.put("fuente", fuente);
        detalle.put("ordinal", nombre.getSourceOrdinal());
        return detalle;
    }

    /**
     * GET /config/fuentes
     *
     * Lista todas las fuentes de configuración activas ordenadas por ordinal
     * (de menor a mayor prioridad).
     */
    @GET
    @Path("/fuentes")
    public List<Map<String, Object>> fuentes() {
        List<Map<String, Object>> fuentes = new ArrayList<>();

        ConfigProvider.getConfig().getConfigSources().forEach(origen -> {
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("nombre", origen.getName() == null ? "sin nombre" : origen.getName());
            fila.put("ordinal", origen.getOrdinal());
            fuentes.add(fila);
        });

        fuentes.sort(Comparator.comparingInt(f -> (Integer) f.get("ordinal")));
        return fuentes;
    }

    /**
     * GET /config/tipos
     *
     * Demuestra la conversión de tipos que hace SmallRye Config:
     * duraciones, listas y enumeraciones.
     */
    @GET
    @Path("/tipos")
    public Map<String, Object> tipos() {
        Map<String, Object> tipos = new LinkedHashMap<>();
        tipos.put("duracion", config.ventanaReserva().toString());
        tipos.put("deportes", config.deportes());
        tipos.put("modo", config.modo().name());
        tipos.put("entorno", ConfigProvider.getConfig().getValue("app.entorno", String.class));
        return tipos;
    }
}
