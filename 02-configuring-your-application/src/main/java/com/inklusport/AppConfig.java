package com.inklusport;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

/**
 * Configuración tipada (SmallRye Config).
 *
 * @ConfigMapping indica que esta interfaz es el reflejo de un grupo de
 * propiedades: el prefijo "app" se combina con el nombre de cada método.
 *
 *   app.nombre        -> String nombre()
 *   app.autor.nombre  -> String autor().nombre()
 *   app.modo          -> Modo modo()
 *
 * Los métodos sin valor en properties se pueden definir con @WithDefault.
 */
@ConfigMapping(prefix = "app")
public interface AppConfig {

    String nombre();

    @WithDefault("1.0.0")
    String version();

    @WithDefault("DESARROLLO")
    Modo modo();

    String bienvenida();

    /**
     * Configuración anidada: app.autor.nombre y app.autor.email.
     */
    Autor autor();

    interface Autor {
        String nombre();
        String email();
    }

    enum Modo {
        DESARROLLO, PRODUCCION, TEST
    }
}
