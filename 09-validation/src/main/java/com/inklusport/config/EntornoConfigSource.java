package com.inklusport.config;

import java.util.Map;
import java.util.Set;

import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * Fuente de configuración propia (SmallRye Config / MicroProfile Config).
 *
 * El ORDINAL decide la prioridad entre fuentes:
 *
 *   100  <-- esta fuente (la más baja, se puede sobreescribir)
 *   250      application.properties
 *   300      variables de entorno
 *   400      propiedades del sistema (-D)
 *
 * Si dos fuentes tienen el mismo ordinal, gana la que se cargó más tarde.
 *
 * Para que Quarkus la cargue se declara un ConfigSourceProvider en
 * META-INF/services/org.eclipse.microprofile.config.spi.ConfigSourceProvider.
 */
public class EntornoConfigSource implements ConfigSource {

    private static final Map<String, String> VALORES = Map.of(
            "app.entorno", "local",
            "app.despliegue", "contenedor"
    );

    @Override
    public Map<String, String> getProperties() {
        return VALORES;
    }

    @Override
    public Set<String> getPropertyNames() {
        return VALORES.keySet();
    }

    @Override
    public String getValue(String propertyName) {
        return VALORES.get(propertyName);
    }

    @Override
    public String getName() {
        return "EntornoConfigSource";
    }

    @Override
    public int getOrdinal() {
        return 100;
    }
}
