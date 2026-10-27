package com.inklusport.config;

import java.util.List;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

/**
 * Registra la fuente de configuración propia.
 *
 * SmallRye Config busca todas las implementaciones de ConfigSourceProvider
 * declaradas en META-INF/services y las añade a la lista de fuentes.
 */
public class EntornoConfigSourceProvider implements ConfigSourceProvider {

    @Override
    public Iterable<ConfigSource> getConfigSources(ClassLoader forClassLoader) {
        return List.of(new EntornoConfigSource());
    }
}
